package ru.surfproject.app.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import ru.surfproject.app.weather.fragments.FavouritesFragment;
import ru.surfproject.app.weather.fragments.WeatherFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private int idFragment = 0;

    private static void openActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        loadingMapView(); // Метод прогружает MapView, чтобы не было задержки, когда пользователь перейдет на фрагмент с картой
        openFragment(Const.WEATHER_FRAGMENT, R.id.action_myweather, null); // При первом запуске, открываем этот фрагмент
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                openActivity(this, SettingsActivity.class);
                break;
            case R.id.action_about:
                openActivity(this, AboutActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_myweather:
                openFragment(Const.WEATHER_FRAGMENT, R.id.action_myweather, null);
                break;
            case R.id.action_favorites:
                openFragment(Const.FAVOURITES_FRAGMENT, R.id.action_favorites, null);
                break;
            case R.id.action_search:
                openActivity(this, SearchActivity.class);
                break;
            case R.id.action_settings:
                openActivity(this, SettingsActivity.class);
                break;
            case R.id.action_about:
                openActivity(this, AboutActivity.class);
                break;
            case R.id.action_exit:
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (idFragment != 0) {
            navigationView.setCheckedItem(idFragment); //Делаем выделеным необходимый элемент меню
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Здесь возвращается ифна о том, включил ли пользователь геолокацию
        // Но эту инфу надо отправить во фрагмен WeatherFragment, поэтому отправляем
        FragmentManager fragmentManager = getSupportFragmentManager();
        WeatherFragment fragment = (WeatherFragment) fragmentManager.findFragmentByTag(Const.WEATHER_FRAGMENT);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void loadingMapView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    private void openFragment(String tag, int idFragment, Bundle bundle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (fragment == null) {
            if (tag.equals(Const.WEATHER_FRAGMENT)) {
                fragment = new WeatherFragment();
                fragment.setArguments(bundle);
            }
            if (tag.equals(Const.FAVOURITES_FRAGMENT)) {
                fragment = new FavouritesFragment();
            }
        }
        this.idFragment = idFragment; // Запоминаем id нажатой менюшки, для того чтобы было выделение элемента меню, только при переходе на фрагменты
        ft.replace(R.id.fragments_container, fragment, tag);
        ft.commit();
    }
}
