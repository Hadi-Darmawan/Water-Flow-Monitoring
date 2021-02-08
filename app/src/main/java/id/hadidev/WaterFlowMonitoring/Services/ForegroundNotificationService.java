package id.hadidev.WaterFlowMonitoring.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import id.hadidev.WaterFlowMonitoring.Activities.MainActivity;
import id.hadidev.WaterFlowMonitoring.Model.CheckWaterValueModel;
import id.hadidev.WaterFlowMonitoring.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static id.hadidev.WaterFlowMonitoring.Services.App.CHANNEL_ID;

public class ForegroundNotificationService extends Service {

    DatabaseReference databaseReference;
    List<CheckWaterValueModel> checkWaterValue = new ArrayList<>();
    List valueEntries = new ArrayList();
    Integer lastValue;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("ID", "Water Notification System", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_home)
                .setAutoCancel(true)
                .setContentTitle("Water Monitoring System")
                .setContentText("You'll receive notification only when app open")
                .setContentIntent(pendingIntent);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());

        checkWaterValue();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkWaterValue(){
        SharedPreferences session = ForegroundNotificationService.this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = session.edit();
        databaseReference = FirebaseDatabase.getInstance().getReference("AIR");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                checkWaterValue.clear();
                for (DataSnapshot waterData: dataSnapshot.getChildren()){
                    CheckWaterValueModel checkWaterValueModel = waterData.getValue(CheckWaterValueModel.class);
                    checkWaterValue.add(checkWaterValueModel);
                }

                for (int i = 0; i< checkWaterValue.size(); i++){
                    valueEntries.add(checkWaterValue.get(i).getValue());
                }

                lastValue = (Integer) valueEntries.get(valueEntries.size()-1);
                if (session.getInt("WaterValue", 0) != lastValue) {
                    if (lastValue < Integer.parseInt(session.getString("WaterLimitValue", ""))){
                        editor.putInt("WaterValue", lastValue);
                        editor.apply();
                        showNotification();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ForegroundNotificationService.this, "Failed to Retrieved Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("ID", "Water Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "ID")
                .setSmallIcon(R.drawable.ic_home)
                .setAutoCancel(true)
                .setContentTitle("Water Level to Low")
                .setContentText("Water level passed the limit");
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(999, builder.build());
    }

}
