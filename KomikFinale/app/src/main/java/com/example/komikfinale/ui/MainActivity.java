package com.example.komikfinale.ui;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
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

        // Mengatur tema aplikasi saat pertama kali dibuka berdasarkan data yang tersimpan
        SharedPreferences sharedPreferences = getSharedPreferences("theme_prefs", MODE_PRIVATE);
        int nightMode = sharedPreferences.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(nightMode);

        setContentView(R.layout.activity_main);

        // Inisialisasi komponen navigasi
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Mendefinisikan halaman mana saja yang menjadi tujuan level atas (tidak ada tombol kembali)
        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.homeFragment);
        topLevelDestinations.add(R.id.libraryFragment);
        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations).build();

        // Menghubungkan NavController dengan ActionBar dan BottomNavigationView
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    // Method ini dipanggil saat salah satu item menu di ActionBar diklik
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Activity akan mencoba menangani item menu yang bersifat global terlebih dahulu
        if (item.getItemId() == R.id.action_change_theme) {
            // Logika untuk mengganti tema
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            int newNightMode = (currentNightMode == Configuration.UI_MODE_NIGHT_YES) ?
                    AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES;
            AppCompatDelegate.setDefaultNightMode(newNightMode);

            // Simpan pilihan tema ke SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences("theme_prefs", MODE_PRIVATE).edit();
            editor.putInt("night_mode", newNightMode);
            editor.apply();
            return true;
        }
        // Jika bukan item menu global, biarkan NavController atau Fragment yang menanganinya
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    // Method ini diperlukan agar tombol "Kembali" (Up button) di ActionBar berfungsi
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}