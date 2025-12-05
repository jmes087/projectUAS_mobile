package com.example.proyek_aplikasi_absensi_mahasiswa;

public class RiwayatAbsensi {
    private String namaMatkul;
    private String tanggal;
    private String jam;

    public RiwayatAbsensi(String namaMatkul, String tanggal, String jam) {
        this.namaMatkul = namaMatkul;
        this.tanggal = tanggal;
        this.jam = jam;
    }

    public String getNamaMatkul() { return namaMatkul; }
    public String getTanggal() { return tanggal; }
    public String getJam() { return jam; }
}
