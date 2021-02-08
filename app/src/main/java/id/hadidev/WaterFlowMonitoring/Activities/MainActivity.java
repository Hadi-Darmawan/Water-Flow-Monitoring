package id.hadidev.WaterFlowMonitoring.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import id.hadidev.WaterFlowMonitoring.Fragments.HomeFragment;
import id.hadidev.WaterFlowMonitoring.R;
import id.hadidev.WaterFlowMonitoring.Services.BackgroundNotificationService;
import id.hadidev.WaterFlowMonitoring.Services.ForegroundNotificationService;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        // Digunakan untuk mendeklarasikan drawer pada halaman utama (MainActivity)
        drawerLayout = findViewById(R.id.drawerHome);
        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        // Digunakan untuk menambahkan menu toggle pada bagian pojok kiri layar
        // sehingga drawer dapat di open dan di close dengan menekan tombol tersebut
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

        // Digunakan ketika rotasi layar diubah, halaman yang tampil
        // tidak berubah dan tetap pada halaman yang tengah di buka sebelummnya
        // Jika code ini tidak ditambahkan maka ketika layar di rotate maka akan secara otomatis kembali ke kemu home
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.menuHome);
        }

        defaultFgNotification(); // Digunakan untuk memanggil class defaultFgNotification yang telah dibuat di bawah

        defaultBgNotification(); // Digunakan untuk memanggil class defaultBgNotification yang telah dibuat di bawah

        defaultWaterLimitValue(); // // Digunakan untuk memanggil class defaultWaterValue yang telah dibuat di bawah

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    // Class ini digunakan untuk mengaktifkan menu Notification Setting
    // dan About US pada bagian pojok kanan atas layar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            // Digunakan untuk melakukan melakukan switch antar
            // halaman dari halaman utama (Main Activity)
            // ke halaman Notification Setting
            case R.id.settingNotification:
                intent = new Intent(MainActivity.this, NotificationSettingActivity.class);
                startActivity(intent);
                break;

            // Digunakan untuk melakukan melakukan switch antar
            // halaman dari halaman utama (Main Activity)
            // ke halaman Notification Setting
            case R.id.aboutUs:
                intent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Class ini digunakan untuk menentukan halaman yang
    // akan muncul ketika pilihan menu pada drawer di tekan
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.menuHome:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                        new HomeFragment()).commit();
                break;
        }
        return true;
    }

    // Class ini digunakan untuk mengatur fungsi tombol back pada aplikasi
    @Override
    public void onBackPressed(){
        // Ketika menekan tombol back saat drawer terbuka maka tombol back tersebut akan menutup drawer
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else { // Dan ketika menekan tombol back saat drawer tidak terbuka maka aplikasi akan menutup atau keluar
            Intent fgNotificationIntent = new Intent(MainActivity.this, ForegroundNotificationService.class);
            stopService(fgNotificationIntent); // Digunakan untuk menghentikan service pada Foreground ketika apliksi di tutup
            super.onBackPressed();
        }
    }

    // Class ini digunakan untuk mengecek apakah Foreground Notification di hidupkan atau dimatikan oleh user
    // Jika di hidupkan maka Service untuk Foreground Notification akan dijalankan
    @Override
    protected void onStart() {
        super.onStart();
        Intent bgNotificationIntent = new Intent(MainActivity.this, BackgroundNotificationService.class);
        stopService(bgNotificationIntent);

        Intent fgNotificationIntent = new Intent(MainActivity.this, ForegroundNotificationService.class);
        SharedPreferences session = MainActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        if (session.getString("fgNotification", "").equals("Enable")){
            startService(fgNotificationIntent);
        }
    }

    // Class ini digunakan untuk mengecek apakah Foreground Notification di hidupkan atau dimatikan
    // oleh user ketika aplikasi di hentikan atau dikeluarkan
    // Jika di hidupkan maka Service untuk Background Notification akan dijalankan
    @Override
    protected void onStop() {
        Intent fgNotificationIntent = new Intent(MainActivity.this, ForegroundNotificationService.class);
        stopService(fgNotificationIntent);

        Intent intent = new Intent(MainActivity.this, BackgroundNotificationService.class);
        SharedPreferences session = MainActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        if (session.getString("bgNotification", "").equals("Enable")){
            startService(intent);
        } else if (session.getString("bgNotification", "").equals("Disable")){
            stopService(intent);
        }

        super.onStop();
    }

    // Class ini digunakan untuk mengatur default value dari Foreground Notification
    // ketika value di Share Preference bernilai null
    private void defaultFgNotification() {
        SharedPreferences session = MainActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        if (session.getString("fgNotification", "").equals("")){
            editor.putString("fgNotification", "Disable");
            editor.apply();
        }
    }

    // Class ini digunakan untuk mengatur default value dari Background Notification
    // ketika value di Share Preference bernilai null
    private void defaultBgNotification() {
        SharedPreferences session = MainActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        if (session.getString("bgNotification", "").equals("")){
            editor.putString("bgNotification", "Enable");
            editor.apply();
        }
    }

    // Class ini digunakan untuk mengatur default value dari water limit
    // ketika value di Share Preference bernilai null
    private void defaultWaterLimitValue() {
        SharedPreferences session = MainActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        if (session.getString("WaterLimitValue", "").equals("")){
            editor.putString("WaterLimitValue", "150");
            editor.apply();
        }
    }

}