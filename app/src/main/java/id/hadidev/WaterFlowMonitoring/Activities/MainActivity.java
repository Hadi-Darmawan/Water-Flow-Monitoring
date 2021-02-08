package id.hadidev.WaterFlowMonitoring.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import id.hadidev.WaterFlowMonitoring.Fragments.HomeFragment;
import id.hadidev.WaterFlowMonitoring.R;
import id.hadidev.WaterFlowMonitoring.Services.BackgroundNotificationService;
import id.hadidev.WaterFlowMonitoring.Services.ForegroundNotificationService;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private Intent intent;
    private Integer waterValueLimitation;
    LinearLayout settingWaterValue;
    SwitchCompat bgNotification, fgNotification;
    TextView bgNotificationStatus, fgNotificationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerHome);
        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_open, R.string.navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.menuHome);
        }

        defaultFgNotification();

        defaultBgNotification();

        defaultWaterLimitValue();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settingNotification:
                intent = new Intent(MainActivity.this, NotificationSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.aboutUs:
                intent = new Intent(MainActivity.this, AboutUsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

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

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent fgNotificationIntent = new Intent(MainActivity.this, ForegroundNotificationService.class);
            stopService(fgNotificationIntent);

            super.onBackPressed();
        }
    }

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

    private void defaultFgNotification() {
        SharedPreferences session = MainActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        if (session.getString("fgNotification", "").equals("")){
            editor.putString("fgNotification", "Disable");
            editor.apply();
        }
    }

    private void defaultBgNotification() {
        SharedPreferences session = MainActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        if (session.getString("bgNotification", "").equals("")){
            editor.putString("bgNotification", "Enable");
            editor.apply();
        }
    }

    private void defaultWaterLimitValue() {
        SharedPreferences session = MainActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        if (session.getString("WaterLimitValue", "").equals("")){
            editor.putString("WaterLimitValue", "150");
            editor.apply();
        }
    }


//    private void checkWaterValue(){
//        SharedPreferences session = MainActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = session.edit();
//        databaseReference = FirebaseDatabase.getInstance().getReference("AIR");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                checkWaterValue.clear();
//                for (DataSnapshot waterData: dataSnapshot.getChildren()){
//                    CheckWaterValueModel checkWaterValueModel = waterData.getValue(CheckWaterValueModel.class);
//                    checkWaterValue.add(checkWaterValueModel);
//                }
//
//                for (int i = 0; i< checkWaterValue.size(); i++){
//                    valueEntries.add(checkWaterValue.get(i).getValue());
//                }
//
//                lastValue = (Integer) valueEntries.get(valueEntries.size()-1);
//                if (session.getInt("WaterValue", 0) != lastValue) {
//                    if (lastValue >250){
//                        editor.putInt("WaterValue", lastValue);
//                        editor.apply();
//                        showNotification();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(MainActivity.this, "Failed to Retrieved Data", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void showNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            NotificationChannel channel =
//                    new NotificationChannel("ID", "Water Notification", NotificationManager.IMPORTANCE_DEFAULT);
//            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ID")
//                .setSmallIcon(R.drawable.ic_home)
//                .setAutoCancel(true)
//                .setContentTitle("Water Level to High")
//                .setContentText("Water level passed the limit");
//        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
//        managerCompat.notify(999, builder.build());
//    }
}