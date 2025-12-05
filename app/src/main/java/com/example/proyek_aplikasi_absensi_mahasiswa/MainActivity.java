package com.example.proyek_aplikasi_absensi_mahasiswa;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView rvRiwayatAbsensi;
    List<RiwayatAbsensi> riwayatList;
    RiwayatAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        rvRiwayatAbsensi = findViewById(R.id.rvRiwayatAbsensi);
        riwayatList = new ArrayList<>();
        riwayatList.add(new RiwayatAbsensi("Pemrograman Mobile", "4 Des 2025", "08:00"));
        riwayatList.add(new RiwayatAbsensi("Basis Data", "1 Des 2025", "09:00"));
        riwayatList.add(new RiwayatAbsensi("AI Dasar", "28 Nov 2025", "10:00"));

        adapter = new RiwayatAdapter(riwayatList);

        rvRiwayatAbsensi.setLayoutManager(new LinearLayoutManager(this));
        rvRiwayatAbsensi.setAdapter(adapter);
    }

}

