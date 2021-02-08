package id.hadidev.WaterFlowMonitoring.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import id.hadidev.WaterFlowMonitoring.R;

public class AboutUsActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Toolbar toolbar = findViewById(R.id.appToolbar);
        setSupportActionBar(toolbar);

        // Digunakan untuk menampilkan tanda panah back
        // di bagian pojok kiri atas layar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Digunakan untuk melakukan melakukan switch antar halaman dari halaman about
        // ke halaman Setting Notifikasi
        switch (item.getItemId()){
            case R.id.settingNotification:
                intent = new Intent(AboutUsActivity.this, NotificationSettingActivity.class);
                startActivity(intent);
                break;

            // Digunakan untuk menampilkan notifikasi jika user menekan kembali pilihan menu About
            // padahal user telah berada di menu about
            case R.id.aboutUs:
                Toast.makeText(AboutUsActivity.this, "You're in About Us Page", Toast.LENGTH_SHORT)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}