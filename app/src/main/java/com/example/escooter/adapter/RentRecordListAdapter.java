package com.example.escooter.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.escooter.R;
import com.example.escooter.data.model.RentalRecord;
import com.example.escooter.databinding.ListRentRecordBinding;

import java.util.List;

public class RentRecordListAdapter extends RecyclerView.Adapter<RentRecordListAdapter.ViewHolder> {

    private final List<RentalRecord> rentRecordList;

    public RentRecordListAdapter(List<RentalRecord> rentRecordList) {
        this.rentRecordList= rentRecordList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ListRentRecordBinding binding;

        public ViewHolder(View view) {
            super(view);
            binding = ListRentRecordBinding.bind(view);
        }

        public void bind(RentalRecord rentRecord) {
            binding.escooterModel.setText(rentRecord.getEscooterModel());
            binding.rentRecordId.setText(rentRecord.getRentRecordId());
            binding.escooterId.setText(rentRecord.getEscooterId());
            binding.escooterRentTime.setText(rentRecord.getEscooterRentTime());
            binding.escooterReturnTime.setText(rentRecord.getEscooterReturnTime());
            binding.feePerMin.setText(String.valueOf(rentRecord.getFeePerMin()));
            binding.duration.setText(String.valueOf(rentRecord.getDuration()));
            binding.totalFee.setText(String.valueOf(rentRecord.getTotalFee()));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_rent_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(rentRecordList.get(position));
    }

    @Override
    public int getItemCount() {
        return rentRecordList.size();
    }
}


