package com.example.komikfinale.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.komikfinale.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- LOGIKA MENGATUR TEMA SAAT APLIKASI DIBUKA ---
        SharedPreferences sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        // Baca mode malam yang tersimpan, defaultnya adalah mode sistem
        int nightMode = sharedPreferences.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(nightMode);
        // --- AKHIR LOGIKA TEMA ---

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.homeFragment);
        topLevelDestinations.add(R.id.libraryFragment);
        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    // --- TAMBAHKAN DUA METHOD DI BAWAH INI ---

    // Method untuk menampilkan menu (main_menu.xml) di ActionBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Method untuk menangani klik pada item menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Cek apakah item yang diklik adalah tombol ganti tema
        if (item.getItemId() == R.id.action_change_theme) {
            // Ambil mode malam saat ini
            int currentNightMode = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            int newNightMode;

            // Ganti tema
            if (currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                newNightMode = AppCompatDelegate.MODE_NIGHT_NO;
            } else {
                newNightMode = AppCompatDelegate.MODE_NIGHT_YES;
            }

            // Terapkan mode yang baru
            AppCompatDelegate.setDefaultNightMode(newNightMode);

            // Simpan pilihan ke SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences("theme_prefs", MODE_PRIVATE).edit();
            editor.putInt("night_mode", newNightMode);
            editor.apply();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}