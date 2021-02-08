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

        if (InternetConnection.checkConnection(getContext())) {
            // Its Available...
            lineChart.setNoDataText("Please wait...");
            lineChart.setNoDataTextColor(Color.BLACK);
            getData();
        } else {
            // Not Available...
            lineChart.setNoDataText("Can't Retrieve Data");
            lineChart.setNoDataTextColor(Color.BLACK);
            connectionNotification();
        }

        return view;
    }

    private void getData(){
        databaseReference = FirebaseDatabase.getInstance().getReference("AIR");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                waterModelList.clear();
                barEntries.clear();
                dateEntries.clear();
                valueEntries.clear();
                lineChart.refreshDrawableState();

                for (DataSnapshot waterData: snapshot.getChildren()){
                    WaterModel waterModel = waterData.getValue(WaterModel.class);
                    waterModelList.add(waterModel);
                }

//                for (int i=0; i<waterModelList.size(); i++){
//                    barEntries.add(new Entry(i, waterModelList.get(i).getValue()));
//                    valueEntries.add(waterModelList.get(i).getValue());
//                    dateEntries.add(waterModelList.get(i).getDateString());
//                }

                if (waterModelList.size()>11){
                    for (int i=waterModelList.size()-12; i<waterModelList.size(); i++){
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
//                xAxis.setLabelRotationAngle(90f);
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
//                lineChart.setDrawGridBackground(false);
                lineChart.notifyDataSetChanged();
                lineChart.setData(lineData);
                lineChart.invalidate();
//                lineChart.setVisibleXRangeMaximum(3f);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to Retrieved Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

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