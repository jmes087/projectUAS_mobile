package com.example.proyek_aplikasi_absensi_mahasiswa;

public class Absensi {

    public String nama;
    public String nim;
    public String matkul;
    public String tanggal;
    public String jam;

    // Wajib ada constructor kosong untuk Firebase
    public Absensi() {
    }

    public Absensi(String nama, String nim, String matkul, String tanggal, String jam) {
        this.nama = nama;
        this.nim = nim;
        this.matkul = matkul;
        this.tanggal = tanggal;
        this.jam = jam;
    }
}
