package com.example.proyek_aplikasi_absensi_mahasiswa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RiwayatAdapter extends RecyclerView.Adapter<RiwayatAdapter.ViewHolder> {

    private List<RiwayatAbsensi> list;

    public RiwayatAdapter(List<RiwayatAbsensi> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_absensi_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RiwayatAbsensi data = list.get(position);
        holder.tvNamaMatkul.setText(data.getNamaMatkul());
        holder.tvDate.setText(data.getTanggal());
        holder.tvTime.setText(data.getJam());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaMatkul, tvDate, tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaMatkul = itemView.findViewById(R.id.tvNamaMatkul);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
