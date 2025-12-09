package com.example.proyek_aplikasi_absensi_mahasiswa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AbsensiAdapter extends RecyclerView.Adapter<AbsensiAdapter.AbsensiViewHolder> {

    private List<Absensi> listAbsensi;

    public AbsensiAdapter(List<Absensi> listAbsensi) {
        this.listAbsensi = listAbsensi;
    }

    @NonNull
    @Override
    public AbsensiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_absensi_card, parent, false);
        return new AbsensiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsensiViewHolder holder, int position) {
        Absensi item = listAbsensi.get(position);

        // Sesuaikan dengan field yang disimpan di Firebase
        holder.tvNamaMatkul.setText(item.matkul != null ? item.matkul : "-");
        holder.tvDate.setText(item.tanggal != null ? item.tanggal : "-");
        holder.tvTime.setText(item.jam != null ? item.jam : "-");
    }

    @Override
    public int getItemCount() {
        return listAbsensi != null ? listAbsensi.size() : 0;
    }

    static class AbsensiViewHolder extends RecyclerView.ViewHolder {

        TextView tvNamaMatkul, tvDate, tvTime;

        public AbsensiViewHolder(@NonNull View itemView) {
            super(itemView);

            // PENTING: id harus sama persis dengan yang ada di item_absensi_card.xml
            tvNamaMatkul = itemView.findViewById(R.id.tvNamaMatkul);
            tvDate       = itemView.findViewById(R.id.tvDate);
            tvTime       = itemView.findViewById(R.id.tvTime);
        }
    }
}
