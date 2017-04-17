package ru.surfproject.app.weather.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.RadioGroup;

import ru.surfproject.app.weather.R;
import ru.surfproject.app.weather.ui.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Инициализируем ToolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Добавление кнопки назад в toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_settings_container, new SettingsFragment())
                .commit();
    }

    // Метод слушает нажатые кнопки в actionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Завершаем текущее активити
        }
        return super.onOptionsItemSelected(item);
    }
}
