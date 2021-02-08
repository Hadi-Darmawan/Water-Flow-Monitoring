package id.hadidev.WaterFlowMonitoring.Fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import id.hadidev.WaterFlowMonitoring.Model.WaterModel;
import id.hadidev.WaterFlowMonitoring.R;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    DatabaseReference databaseReference;
    List<WaterModel> waterModelList = new ArrayList<>();
    LineChart lineChart;
    LineData lineData;
    LineDataSet lineDataSet;
    List barEntries = new ArrayList();
    List valueEntries = new ArrayList();
    List dateEntries = new ArrayList();

    public static class InternetConnection {
        public static boolean checkConnection(Context context) {
            final ConnectivityManager connectionCheck =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectionCheck != null) {
                NetworkInfo activeNetworkInfo = connectionCheck.getActiveNetworkInfo();
                if (activeNetworkInfo != null) { // connected to the internet
                    // connected to the mobile provider's data plan
                    if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        // connected to wifi
                        return true;
                    } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
                }
            }
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        lineChart = view.findViewById(R.id.waterDiagram);

        // Pengkondisian ini digunakan untuk mengecek koneksi internet apakah terhubung atau tidak
        if (InternetConnection.checkConnection(getContext())) {
            // Apabila internet tersedia
            lineChart.setNoDataText("Please wait...");
            lineChart.setNoDataTextColor(Color.BLACK);
            getData();
        } else {
            // Apabila internet tidak tersedia
            lineChart.setNoDataText("Can't Retrieve Data");
            lineChart.setNoDataTextColor(Color.BLACK);
            connectionNotification(); // Memanggil class connectionNotification yg telah dibuat di bawah
        }

        return view;
    }

    // Class ini digunakan untuk mengambil data dari Firebase Relatime Database untuk ditampilkan pada Home Fragment
    private void getData(){
        databaseReference = FirebaseDatabase.getInstance().getReference("AIR"); // path: AIR diambil dari nama Table pada Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // semua code dengan .clear() digunakan untuk membersihkan array agar
                // ketika data yang di fatch dari firebase berbeda dengan data sebelumnya
                // maka tidak akan terjadi error saat menampilkan data terbaru.
                // Baik itu data yg diedit, di delete, maupun ditambahkan baru
                waterModelList.clear();
                barEntries.clear();
                dateEntries.clear();
                valueEntries.clear();
                lineChart.refreshDrawableState();

                // Digunakan untuk melakukan looping terhadap data dari firebase ke dalam model array list yang telah dibuat sebelumnya
                for (DataSnapshot waterData: snapshot.getChildren()){
                    WaterModel waterModel = waterData.getValue(WaterModel.class);
                    waterModelList.add(waterModel);
                }

                // Dilakukan pengecekan apabila length data nya lebih dari 11 data
                // maka data yang tambil akan dibatasi menjadi hanya 11 data terbaru
                if (waterModelList.size()>11){
                    for (int i=waterModelList.size()-12; i<waterModelList.size(); i++){
                        // Perulangan ini digunakan untuk mengurutkan data secara desc
                        barEntries.add(new Entry(i-(waterModelList.size()-12), waterModelList.get(i).getValue()));
                        valueEntries.add(waterModelList.get(i).getValue());
                        String currentDateTime = waterModelList.get(i).getDateString();
                        String[] separateDateTime = currentDateTime.split(" ");

                        String currentTime = separateDateTime[1];
                        String[] separateTime = currentTime.split(":");

                        dateEntries.add(separateTime[0] + ":" + separateTime[1]);
                    }
                } else {
                    for (int i=0; i<waterModelList.size(); i++){
                        // Perulangan ini digunakan untuk mengurutkan data secara asc
                        barEntries.add(new Entry(i, waterModelList.get(i).getValue()));
                        valueEntries.add(waterModelList.get(i).getValue());

                        String currentDateTime = waterModelList.get(i).getDateString();
                        String[] separateDateTime = currentDateTime.split(" ");

                        String currentTime = separateDateTime[1];
                        String[] separateTime = currentTime.split(":");

                        dateEntries.add(separateTime[1] + ":" + separateTime[2]);
                    }
                }

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setLabelCount(dateEntries.size());
                xAxis.setValueFormatter(new IndexAxisValueFormatter(dateEntries));
                xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
                xAxis.setGridColor(R.color.white);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setTextSize(10);
                xAxis.setSpaceMin(0.5f);
                xAxis.setSpaceMax(0.5f);

                lineDataSet = new LineDataSet(barEntries, "Debit Air");
                lineDataSet.setHighLightColor(R.color.blue);
                lineDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                lineDataSet.setValueTextColor(Color.BLACK);
                lineDataSet.setValueTextSize(10f);
                lineDataSet.setLineWidth(4);

                lineData = new LineData(lineDataSet);
                lineChart.getDescription().setText("");
                lineChart.setPinchZoom(true);
                lineChart.notifyDataSetChanged();
                lineChart.setData(lineData);
                lineChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to Retrieved Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Class ini digunakan untuk mengecek sambungan internet pada perangkat user
    private void connectionNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =
                    new NotificationChannel("ID", "Water Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "ID")
                .setSmallIcon(R.drawable.ic_home)
                .setAutoCancel(true)
                .setContentTitle("You're offline!")
                .setContentText("Please check your internet connection");
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getContext());
        managerCompat.notify(999, builder.build());
    }
}