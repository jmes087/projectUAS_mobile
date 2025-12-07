package com.example.proyek_aplikasi_absensi_mahasiswa;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import com.example.proyek_aplikasi_absensi_mahasiswa.AbsensiFormDialog;
import com.example.proyek_aplikasi_absensi_mahasiswa.AbsensiRejectedDialog;


public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 100;

    private LocationManager locationManager;

    // Titik absensi & radius
    private final double ABSEN_LAT = -7.957218477180774;
    private final double ABSEN_LON = 112.58915398187875;
    private final float RADIUS_ABSEN = 30; // 300 meter

    private Button buttonReqAbsen;


//    RecyclerView rvRiwayatAbsensi;
//    List<RiwayatAbsensi> riwayatList;
//    RiwayatAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        buttonReqAbsen = findViewById(R.id.buttonReqAbsen);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);



//        rvRiwayatAbsensi = findViewById(R.id.rvRiwayatAbsensi);
//        riwayatList = new ArrayList<>();
//        riwayatList.add(new RiwayatAbsensi("Pemrograman Mobile", "4 Des 2025", "08:00"));
//        riwayatList.add(new RiwayatAbsensi("Basis Data", "1 Des 2025", "09:00"));
//        riwayatList.add(new RiwayatAbsensi("AI Dasar", "28 Nov 2025", "10:00"));
//
//        adapter = new RiwayatAdapter(riwayatList);
//
//        rvRiwayatAbsensi.setLayoutManager(new LinearLayoutManager(this));
//        rvRiwayatAbsensi.setAdapter(adapter);

        buttonReqAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekLokasiUser();
            }
        });
    }
    private void cekLokasiUser() {

        // Cek permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE
            );
            return;
        }

        // Cek apakah GPS ON
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Aktifkan GPS untuk mendapatkan lokasi", Toast.LENGTH_LONG).show();
            return;
        }

        // Ambil lokasi 1x
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,    // waktu minimum
                0,    // jarak minimum
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {

                        double userLat = location.getLatitude();
                        double userLon = location.getLongitude();

                        // Hitung jarak user ke titik absen
                        float[] result = new float[1];
                        Location.distanceBetween(
                                userLat,
                                userLon,
                                ABSEN_LAT,
                                ABSEN_LON,
                                result
                        );

                        float jarak = result[0];

                        if (jarak <= 50) {
                            showFormDialog();        // tampilkan popup form absensi
                        } else {
                            showRejectedDialog();    // tampilkan popup penolakan
                        }


                        // Stop menerima update
                        locationManager.removeUpdates(this);
                    }
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cekLokasiUser();
            } else {
                Toast.makeText(this, "Izin lokasi ditolak", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showPopupFormAbsen() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_form_absensi, null);

        // tombol di popup
        Button cancelBtn = view.findViewById(R.id.cancel_button);
        Button kirimBtn = view.findViewById(R.id.kirim_button);

        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        kirimBtn.setOnClickListener(v -> {
            // BELUM DIISI — sesuai permintaan
            Toast.makeText(this, "Form akan diproses nanti", Toast.LENGTH_SHORT).show();
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void showPopupRejected() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_absensi_rejected, null);

        Button closeBtn = view.findViewById(R.id.btnCloseRejected);

        closeBtn.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(view);
        dialog.show();
    }

    private void showFormDialog() {
        AbsensiFormDialog dialog = new AbsensiFormDialog();
        dialog.show(getSupportFragmentManager(), "FormDialog");
    }

    private void showRejectedDialog() {
        AbsensiRejectedDialog dialog = new AbsensiRejectedDialog();
        dialog.show(getSupportFragmentManager(), "RejectedDialog");
    }

}

