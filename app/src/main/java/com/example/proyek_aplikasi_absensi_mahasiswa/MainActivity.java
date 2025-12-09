package com.example.proyek_aplikasi_absensi_mahasiswa;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_REQUEST_CODE = 100;

    private LocationManager locationManager;

    // Titik absensi & radius
    private final double ABSEN_LAT = -7.957218477180774;
    private final double ABSEN_LON = 112.58915398187875;
    private final float ABSEN_RADIUS_METER = 500f; // meter

    private Button buttonReqAbsen;

    // RecyclerView riwayat
    private RecyclerView rvRiwayatAbsensi;
    private AbsensiAdapter absensiAdapter;
    private final List<Absensi> absensiList = new ArrayList<>();
    private DatabaseReference absensiRef;

    // TextView counter "Disetujui"
    private TextView tvCountApproved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Firebase
        FirebaseApp.initializeApp(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Lokasi
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Tombol request absen
        buttonReqAbsen = findViewById(R.id.buttonReqAbsen);
        buttonReqAbsen.setOnClickListener(v -> cekLokasiUser());

        // === Ambil TextView counter dari card absen_accepted ===
        tvCountApproved = findViewById(R.id.tv_count);

        // === RecyclerView riwayat ===
        rvRiwayatAbsensi = findViewById(R.id.rvRiwayatAbsensi);
        rvRiwayatAbsensi.setLayoutManager(new LinearLayoutManager(this));
        absensiAdapter = new AbsensiAdapter(absensiList);
        rvRiwayatAbsensi.setAdapter(absensiAdapter);

        // Firebase reference
        absensiRef = FirebaseDatabase.getInstance().getReference("absensi");

        // Load riwayat + update counter
        setupRiwayatAbsensi();
    }

    /**
     * Ambil nama dari profile (value_nama),
     * lalu baca data absensi di Firebase untuk nama tersebut,
     * sekaligus update jumlah "Disetujui" di tv_count.
     */
    private void setupRiwayatAbsensi() {
        TextView tvNamaProfil = findViewById(R.id.value_nama);
        if (tvNamaProfil == null) {
            Toast.makeText(this, "value_nama tidak ditemukan di layout", Toast.LENGTH_SHORT).show();
            return;
        }

        String namaProfil = tvNamaProfil.getText().toString().trim();
        if (namaProfil.isEmpty()) {
            Toast.makeText(this, "Nama profil kosong, isi dulu di dashboard", Toast.LENGTH_SHORT).show();
            return;
        }

        // Query absensi dengan field "nama" = namaProfil
        Query query = absensiRef.orderByChild("nama").equalTo(namaProfil);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                absensiList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Absensi a = ds.getValue(Absensi.class);
                    if (a != null) {
                        absensiList.add(a);
                    }
                }

                // Terbaru di atas
                Collections.reverse(absensiList);
                absensiAdapter.notifyDataSetChanged();

                // === Update counter "Disetujui" ===
                // Untuk saat ini kita anggap semua data di node "absensi" adalah yang disetujui.
                int jumlahDisetujui = absensiList.size();
                if (tvCountApproved != null) {
                    tvCountApproved.setText(String.valueOf(jumlahDisetujui));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,
                        "Gagal memuat riwayat: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ================== LOKASI & DIALOG ==================

    private void cekLokasiUser() {

        // Cek permission lokasi
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE
            );
            return;
        }

        // Cek apakah GPS aktif
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Aktifkan GPS untuk mendapatkan lokasi", Toast.LENGTH_LONG).show();
            return;
        }

        // Ambil lokasi sekali
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0,
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

                        if (jarak <= ABSEN_RADIUS_METER) {
                            showFormDialog();
                        } else {
                            showRejectedDialog();
                        }

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

    private void showFormDialog() {
        AbsensiFormDialog dialog = new AbsensiFormDialog();
        dialog.show(getSupportFragmentManager(), "FormDialog");
    }

    private void showRejectedDialog() {
        AbsensiRejectedDialog dialog = new AbsensiRejectedDialog();
        dialog.show(getSupportFragmentManager(), "RejectedDialog");
    }
}
