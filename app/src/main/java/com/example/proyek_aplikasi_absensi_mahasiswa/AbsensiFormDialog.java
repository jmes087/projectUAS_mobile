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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AbsensiFormDialog extends DialogFragment {
    private Button btnCancel;
    private Button btnKirim;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Gunakan builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_form_absensi, null);

        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // SETUP DROPDOWN
        AutoCompleteTextView dropdown = view.findViewById(R.id.dropdownMatkul);

        ArrayAdapter<CharSequence> adapterMatkul =
                ArrayAdapter.createFromResource(
                        getActivity(),
                        R.array.list_matkul,
                        android.R.layout.simple_dropdown_item_1line
                );

        dropdown.setAdapter(adapterMatkul);

        dropdown.setFocusable(false);
        dropdown.setFocusableInTouchMode(false);

        dropdown.setOnClickListener(v -> dropdown.showDropDown());
        dropdown.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) dropdown.showDropDown();
        });

        // BUTTON BATAL  POPUP
        btnCancel = view.findViewById(R.id.cancel_button);
        btnCancel.setOnClickListener(v -> showCancelPopup());

        btnKirim = view.findViewById(R.id.kirim_button);
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String matkul = dropdown.getText().toString();
                // Ambil waktu realtime
                Calendar calendar = Calendar.getInstance();

                SimpleDateFormat formatTanggal = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                SimpleDateFormat formatJam = new SimpleDateFormat("HH:mm", Locale.getDefault());

                String tanggal = formatTanggal.format(calendar.getTime());
                String jam = formatJam.format(calendar.getTime());


                if (listener != null) {
                    listener.onSubmitAbsensi(matkul, tanggal, jam);
                }

                dismiss(); // tutup dialog
            }
        });


        return dialog;
    }

    private void showCancelPopup() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Konfirmasi")
                .setMessage("Apakah anda yakin ingin batal absensi?")
                .setPositiveButton("Ya", (dialogInterface, i) -> {

                    // Ini sekarang berfungsi
                    AbsensiFormDialog.this.dismiss();
                })
                .setNegativeButton("Tidak", null)
                .show();
    }



    public interface OnAbsensiSubmitListener {
        void onSubmitAbsensi(String matkul, String tanggal, String jam);
    }

    private OnAbsensiSubmitListener listener;

    public void setOnAbsensiSubmitListener(OnAbsensiSubmitListener listener) {
        this.listener = listener;
    }


}



