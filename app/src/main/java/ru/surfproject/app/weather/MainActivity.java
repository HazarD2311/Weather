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
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private NavigationView navigationView;
    private int idFragment = 0;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Bundle bundleWeather;
    private ProgressBar progressBar;
    private LinearLayout layoutPermissionEnable;
    private Button btnPermissionEnable;
    private LocationSettingsRequest.Builder builder;
    private Status statusLocation;

    private static void openActivity(Context context, Class<?> cls) {
        Intent intent = new Intent(context, cls);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        layoutPermissionEnable = (LinearLayout) findViewById(R.id.layout_permission_enable);
        btnPermissionEnable = (Button) findViewById(R.id.btn_permission_enable);
        btnPermissionEnable.setOnClickListener(clickBtnPermissionEnable);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        if (bundleWeather == null) {
            bundleWeather = new Bundle();
        }
        loadingMapView(); // Метод прогружает MapView, чтобы не было задержки, когда пользователь перейдет на фрагмент с картой
        getPermissionLocation(); // Проверяем разрешение на геолокацию
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
                if (bundleWeather.getString("latitude") == null
                        || bundleWeather.getString("longitude") == null
                        || statusLocation.getStatusCode() != LocationSettingsStatusCodes.SUCCESS) {
                    getPermissionLocation();
                } else {
                    progressBar.setVisibility(View.GONE);
                    layoutPermissionEnable.setVisibility(View.GONE);
                    openFragment(Const.WEATHER_FRAGMENT, R.id.action_myweather, bundleWeather);
                }
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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Const.MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение было одобрено.
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }
                } else {
                    // Если пользователь отказал в доступе
                    progressBar.setVisibility(View.GONE);
                    layoutPermissionEnable.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Разрешения не предоставлены!", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // Тут можно добавить еще пермишены.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                if (bundleWeather != null) {
                    layoutPermissionEnable.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    openFragment(Const.WEATHER_FRAGMENT, R.id.action_myweather, bundleWeather);// Открываем WeatherФрагмент
                } else {
                    Toast.makeText(this, "Ошибка получения местоположения", Toast.LENGTH_SHORT).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                progressBar.setVisibility(View.GONE);
                layoutPermissionEnable.setVisibility(View.VISIBLE);
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                statusLocation = result.getStatus();
                switch (statusLocation.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // местоположение включено
                        layoutPermissionEnable.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        openFragment(Const.WEATHER_FRAGMENT, R.id.action_myweather, bundleWeather);// Открываем WeatherФрагмент
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Местоположение выключено, запускаем диалог с просьбой включить
                        try {
                            statusLocation.startResolutionForResult(MainActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Игнорируем ошибки
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Изменить параметры невозможно параметры
                        progressBar.setVisibility(View.GONE);
                        layoutPermissionEnable.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        bundleWeather.putString("latitude", String.valueOf(latLng.latitude));
        bundleWeather.putString("longitude", String.valueOf(latLng.longitude));
        progressBar.setVisibility(View.GONE);
        //Останавливаем обновление LocationServices
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    private void getPermissionLocation() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermissionActivity(); // Просим доступ к местоположению устройства.
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { // Проверяем наличаем разрешения
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();

        }
    }

    public boolean checkLocationPermissionActivity() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Запрашиваем доступ к ACCESS_FINE_LOCATION
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        Const.MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        Const.MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private View.OnClickListener clickBtnPermissionEnable = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            layoutPermissionEnable.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            getPermissionLocation();
        }
    };

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
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragments_container, fragment, tag);
        ft.commit();
    }
}
