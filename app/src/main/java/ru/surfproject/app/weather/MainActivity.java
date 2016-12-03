package ru.surfproject.app.weather;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import ru.surfproject.app.weather.Fragments.FragmentFavorites;
import ru.surfproject.app.weather.Fragments.FragmentMain;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView navigationView;
    int idFragment=0;
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

        openFragment(new FragmentMain(), R.id.action_myweather);// При старте приложения открываем ФрагментМайн
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
        switch (item.getItemId()){
            case R.id.action_settings:
                openActivity(this,SettingActivity.class);
                break;
            case R.id.action_about:
                openActivity(this,AboutActivity.class);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_myweather:
                openFragment(new FragmentMain(),R.id.action_myweather);
                break;
            case R.id.action_favorites:
                openFragment(new FragmentFavorites(),R.id.action_favorites);
                break;
            case R.id.action_search:
                openActivity(this,SearchActivity.class);
                break;
            case R.id.action_settings:
                openActivity(this,SettingActivity.class);
                break;
            case R.id.action_about:
                openActivity(this,AboutActivity.class);
                break;
            case R.id.action_exit:
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private static void openActivity(Context context, Class<?> cls){
            Intent intent = new Intent(context, cls);
            context.startActivity(intent);

    }
    private void openFragment(Fragment fragment, int idFragment){
        this.idFragment=idFragment; // Запоминаем id нажатой менюшки, для того чтобы было выделение элемента меню, только при переходе на фрагменты
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragments_container, fragment);
        ft.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (idFragment!=0){
            navigationView.setCheckedItem(idFragment); //По умолчанию делаем "Моя погода" выделенным
        }

    }
}
