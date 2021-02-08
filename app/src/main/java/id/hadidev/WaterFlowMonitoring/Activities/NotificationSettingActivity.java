package id.hadidev.WaterFlowMonitoring.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import id.hadidev.WaterFlowMonitoring.R;

public class NotificationSettingActivity extends AppCompatActivity {

    private Intent intent;
    private Integer waterValueLimitation;
    LinearLayout settingWaterValue;
    SwitchCompat bgNotification, fgNotification;
    TextView bgNotificationStatus, fgNotificationStatus, waterValueRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);

        Toolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        // Digunakan untuk menampilkan tanda panah back
        // di bagian pojok kiri atas layar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Digunakan untuk memanggil
        // class defaultFgNotification yang dibuat di bawah
        defaultFgNotification();

        // Digunakan untuk memanggil
        // class defaultBgNotification yang dibuat di bawah
        defaultBgNotification();

        // Digunakan untuk memanggil
        // class defaultWaterLimit yang dibuat di bawah
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

            // Digunakan untuk menampilkan notifikasi jika user menekan kembali
            // pilihan menu Notificaton Setting
            // padahal user telah berada di menu about
            case R.id.settingNotification:
                Toast.makeText(NotificationSettingActivity.this, "You're in Notification Setting Page", Toast.LENGTH_SHORT)
                        .show();
                break;

            // Digunakan untuk melakukan melakukan switch antar
            // halaman dari halaman Notification Setting
            // ke halaman About
            case R.id.aboutUs:
                intent = new Intent(NotificationSettingActivity.this, AboutUsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Class ini digunakan untuk mengatur notifikasi
    // ketika aplikasi dikeluarkan ke foreground
    private void defaultFgNotification() {
        SharedPreferences session = NotificationSettingActivity.this
                .getSharedPreferences("MyPref", Context.MODE_PRIVATE); // Digunakan untuk mendeklarasikan Share Preferences
        SharedPreferences.Editor editor = session.edit();

        // digunakan untuk mencari atau (find) id dari text view
        // yang menunjukan status dari Foreground notification
        fgNotificationStatus = findViewById(R.id.fgNotificationStatus);

        // Digunakan untuk mencari atau (find) id dari toggle switch
        // yang digunakan untuk melakukan on off terhadap Foreground Notification
        fgNotification = findViewById(R.id.switchFgNotification);

        if (session.getString("fgNotification", "").equals("Disable")){
            fgNotification.setChecked(false);
            fgNotificationStatus.setText(session.getString("fgNotification", ""));
        } else if (session.getString("fgNotification", "").equals("Enable")){
            fgNotification.setChecked(true);
            fgNotificationStatus.setText(session.getString("fgNotification", ""));
        } else if (session.getString("fgNotification", "").equals("")){
            fgNotification.setChecked(false);
            fgNotificationStatus.setText("Disable");
            editor.putString("fgNotification", "Disable"); // Digunakan untuk Memasukan String Disable dengan key fgNotification
            editor.apply(); // Digunakan untuk melakukan appy string diatas ke Share Preferences
        }

        fgNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (fgNotification.isChecked()){
                    editor.putString("fgNotification", "Enable");
                    editor.apply();
                    fgNotificationStatus.setText(session.getString("fgNotification", ""));
                } else {
                    editor.putString("fgNotification", "Disable");
                    editor.apply();
                    fgNotificationStatus.setText(session.getString("fgNotification", ""));
                }
            }
        });
    }

    // Class ini digunakan untuk mengatur notifikasi
    // ketika aplikasi sedang berjalan atau dibuka
    private void defaultBgNotification() {
        SharedPreferences session = NotificationSettingActivity.this.
                getSharedPreferences("MyPref", Context.MODE_PRIVATE); // Digunakan untuk mendeklarasikan Share Preferences
        SharedPreferences.Editor editor = session.edit();

        // digunakan untuk mencari atau (find) id dari text view
        // yang menunjukan status dari Background notification
        bgNotificationStatus = findViewById(R.id.bgNotificationStatus);

        // Digunakan untuk mencari atau (find) id dari toggle switch
        // yang digunakan untuk melakukan on off terhadap Background Notification
        bgNotification = findViewById(R.id.switchBgNotification);

        if (session.getString("bgNotification", "").equals("Disable")){
            bgNotification.setChecked(false);
            bgNotificationStatus.setText(session.getString("bgNotification", ""));
        } else if (session.getString("bgNotification", "").equals("Enable")){
            bgNotification.setChecked(true);
            bgNotificationStatus.setText(session.getString("bgNotification", ""));
        } else if (session.getString("bgNotification", "").equals("")){
            bgNotification.setChecked(true);
            bgNotificationStatus.setText("Enable");
            editor.putString("bgNotification", "Enable"); // Digunakan untuk Memasukan String Enable dengan key bgNotification
            editor.apply(); // Digunakan untuk melakukan appy string diatas ke Share Preferences
        }
        bgNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (bgNotification.isChecked()){
                    editor.putString("bgNotification", "Enable"); // Digunakan untuk Memasukan String Enable dengan key bgNotification
                    editor.apply(); // Digunakan untuk melakukan appy string diatas ke Share Preferences
                    bgNotificationStatus.setText(session.getString("bgNotification", ""));
                } else {
                    editor.putString("bgNotification", "Disable"); // Digunakan untuk Memasukan String Disable dengan key bgNotification
                    editor.apply(); // Digunakan untuk melakukan appy string diatas ke Share Preferences
                    bgNotificationStatus.setText(session.getString("bgNotification", ""));
                }
            }
        });
    }

    // Class ini digunakan untuk mengatur batas
    // tingkat ketinggian minimal level air
    private void defaultWaterLimitValue() {
        SharedPreferences session = NotificationSettingActivity.this.
                getSharedPreferences("MyPref", Context.MODE_PRIVATE); // Digunakan untuk mendeklarasikan Share Preferences
        SharedPreferences.Editor editor = session.edit();

        waterValueRange = findViewById(R.id.waterValueRange); // Digunakan untuk menemukan id dari Text View water value range
        // Digunakan untuk mengeset text pada Text View water value range dengan mengisinya menggunakan data pada Share Preference
        waterValueRange.setText(session.getString("WaterLimitValue", "") + " liter/second");
        settingWaterValue = findViewById(R.id.settingWaterValue); // Digunakan untuk menemukan id dari tombol settring water value

        // Code ini digunakan untuk menampilkan custom dialog box ketika tombol setting water value ditekan
        settingWaterValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = LayoutInflater.from(NotificationSettingActivity.this);
                View setWaterValue = layoutInflater.inflate(R.layout.set_water_level, null);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(NotificationSettingActivity.this);
                alertDialog.setView(setWaterValue);
                final EditText tvSetWaterValue = setWaterValue.findViewById(R.id.setWaterValue);
                final TextView btnSetDefaultWaterValue = setWaterValue.findViewById(R.id.btnSetDefaultWaterValue);
                final TextView btnSaveWaterValue = setWaterValue.findViewById(R.id.btnSaveWaterValue);
                final TextView btnCancelSetWaterValue = setWaterValue.findViewById(R.id.btnCancelSetWaterValue);
                alertDialog.setCancelable(true);
                AlertDialog alert = alertDialog.create();

                // Digunakan untuk menyimpan value yang diinput oleh user ke share preference menggunakan tombol Save
                btnSaveWaterValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvSetWaterValue.length()<1){
                            tvSetWaterValue.setError("Water value can't be empty");
                        } else {
                            waterValueLimitation = Integer.parseInt(tvSetWaterValue.getText().toString());
                            if (waterValueLimitation<1){
                                tvSetWaterValue.setError("Water value limit to low");
                            } else {
                                editor.putString("WaterLimitValue", tvSetWaterValue.getText().toString());
                                editor.apply();
                                alert.dismiss();
                            }
                        }
                    }
                });

                // Digunakan untuk mengaktifkan tombol Cancel pada dialog
                btnCancelSetWaterValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });

                // Digunakan untuk mengaktifkan tombol untuk megembalikan nilai default water value
                btnSetDefaultWaterValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editor.putInt("WaterLimitValue", 150);
                        editor.apply();
                        alert.dismiss();
                    }
                });

                // Digunakan untuk melakukan transparansi terhadap default dialog
                // sehingga dapat ditumpul dengan custom dialog yang telah dibuat
                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alert.show();
            }
        });
    }

}