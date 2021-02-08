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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
                Toast.makeText(NotificationSettingActivity.this, "You're in Notification Setting Page", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.aboutUs:
                intent = new Intent(NotificationSettingActivity.this, AboutUsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void defaultFgNotification() {
        SharedPreferences session = NotificationSettingActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();

        fgNotificationStatus = findViewById(R.id.fgNotificationStatus);
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
            editor.putString("fgNotification", "Disable");
            editor.apply();
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

    private void defaultBgNotification() {
        SharedPreferences session = NotificationSettingActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();

        bgNotificationStatus = findViewById(R.id.bgNotificationStatus);
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
            editor.putString("bgNotification", "Enable");
            editor.apply();
        }
        bgNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (bgNotification.isChecked()){
                    editor.putString("bgNotification", "Enable");
                    editor.apply();
                    bgNotificationStatus.setText(session.getString("bgNotification", ""));
                } else {
                    editor.putString("bgNotification", "Disable");
                    editor.apply();
                    bgNotificationStatus.setText(session.getString("bgNotification", ""));
                }
            }
        });
    }

    private void defaultWaterLimitValue() {
        SharedPreferences session = NotificationSettingActivity.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();

        waterValueRange = findViewById(R.id.waterValueRange);
        waterValueRange.setText(session.getString("WaterLimitValue", "") + " liter/second");
        settingWaterValue = findViewById(R.id.settingWaterValue);
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

                btnSaveWaterValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        tvSetWaterValue.getText();
                        if (tvSetWaterValue.length()<1){
                            tvSetWaterValue.setError("Water value can't be empty");
                        } else {
                            waterValueLimitation = Integer.parseInt(tvSetWaterValue.getText().toString());
                            if (waterValueLimitation<1){
                                tvSetWaterValue.setError("Water value limit to low");
                            } else {
                                editor.putString("WaterLimitValue", tvSetWaterValue.getText().toString());
                                editor.apply();
//                                Toast.makeText(NotificationSettingActivity.this, String.valueOf(session.getInt("WaterLimitValue", 0)), Toast.LENGTH_SHORT).show();
                                alert.dismiss();
                            }
                        }
                    }
                });

                btnCancelSetWaterValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alert.dismiss();
                    }
                });

                btnSetDefaultWaterValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editor.putInt("WaterLimitValue", 150);
                        editor.apply();
                        alert.dismiss();
                    }
                });

                alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alert.show();
            }
        });
    }

}