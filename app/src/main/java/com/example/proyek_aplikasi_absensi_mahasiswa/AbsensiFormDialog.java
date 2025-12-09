package com.example.proyek_aplikasi_absensi_mahasiswa;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class AbsensiFormDialog extends DialogFragment {

    private Button btnCancel;
    private Button btnKirim;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Builder dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_form_absensi, null);

        builder.setView(view);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // SETUP DROPDOWN MATA KULIAH
        AutoCompleteTextView dropdown = view.findViewById(R.id.dropdownMatkul);

        ArrayAdapter<CharSequence> adapterMatkul =
                ArrayAdapter.createFromResource(
                        getActivity(),
                        R.array.list_matkul,
                        android.R.layout.simple_dropdown_item_1line
                );

        dropdown.setAdapter(adapterMatkul);

        // Biar dropdown selalu tampil dengan tap (bukan harus ketik)
        dropdown.setFocusable(false);
        dropdown.setFocusableInTouchMode(false);
        dropdown.setOnClickListener(v -> dropdown.showDropDown());
        dropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) dropdown.showDropDown();
        });

        // BUTTON BATAL
        btnCancel = view.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(v -> showCancelPopup());

        // BUTTON KIRIM
        btnKirim = view.findViewById(R.id.kirim_button);
        btnKirim.setOnClickListener(v -> {
            String matkul = dropdown.getText().toString().trim();
            if (matkul.isEmpty()) {
                Toast.makeText(getActivity(), "Pilih mata kuliah dulu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ambil waktu realtime
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat formatTanggal = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            SimpleDateFormat formatJam = new SimpleDateFormat("HH:mm", Locale.getDefault());

            String tanggal = formatTanggal.format(calendar.getTime());
            String jam = formatJam.format(calendar.getTime());

            // AMBIL NAMA & NIM DARI LAYOUT CARD PROFILE DI ACTIVITY_MAIN
            // activity_main.xml meng-include layout_card_profile.xml,
            // di dalamnya ada TextView value_nama dan value_nim.
            if (getActivity() == null) {
                Toast.makeText(getContext(), "Activity tidak tersedia", Toast.LENGTH_SHORT).show();
                return;
            }

            TextView tvNama = getActivity().findViewById(R.id.value_nama);
            TextView tvNim  = getActivity().findViewById(R.id.value_nim);

            String nama = tvNama != null ? tvNama.getText().toString().trim() : "";
            String nim  = tvNim != null ? tvNim.getText().toString().trim() : "";

            if (nama.isEmpty() || nim.isEmpty()) {
                Toast.makeText(getActivity(), "Nama/NIM belum terisi di kartu profil", Toast.LENGTH_SHORT).show();
                return;
            }

            // KIRIM KE FIREBASE LANGSUNG DARI DIALOG
            HashMap<String, Object> data = new HashMap<>();
            data.put("nama", nama);
            data.put("nim", nim);
            data.put("matkul", matkul);
            data.put("tanggal", tanggal);
            data.put("jam", jam);

            FirebaseDatabase.getInstance()
                    .getReference("absensi")
                    .push()
                    .setValue(data)
                    .addOnSuccessListener(a -> {
                        Toast.makeText(getActivity(), "Absensi tersimpan!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getActivity(), "Gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        return dialog;
    }

    private void showCancelPopup() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin batal absensi?")
                .setPositiveButton("Ya", (dialogInterface, i) -> AbsensiFormDialog.this.dismiss())
                .setNegativeButton("Tidak", null)
                .show();
    }

    // Interface listener TIDAK DIPAKAI LAGI, boleh dihapus kalau mau lebih bersih.
    public interface OnAbsensiSubmitListener {
        void onSubmitAbsensi(String matkul, String tanggal, String jam);
    }

    private OnAbsensiSubmitListener listener;

    public void setOnAbsensiSubmitListener(OnAbsensiSubmitListener listener) {
        this.listener = listener;
    }
}
