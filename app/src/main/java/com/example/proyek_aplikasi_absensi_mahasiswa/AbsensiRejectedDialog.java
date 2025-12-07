package com.example.proyek_aplikasi_absensi_mahasiswa;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class AbsensiRejectedDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = new Dialog(getActivity());
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_absensi_rejected, null);

        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnClose = view.findViewById(R.id.btnCloseRejected);
        btnClose.setOnClickListener(v -> dismiss());

        return dialog;
    }
}

