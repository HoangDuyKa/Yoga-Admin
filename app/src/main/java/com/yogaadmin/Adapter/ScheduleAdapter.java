package com.yogaadmin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yogaadmin.Model.ScheduleModel;
import com.yogaadmin.R;

import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private Context context;
    private List<ScheduleModel> scheduleList;

    public ScheduleAdapter(Context context, List<ScheduleModel> scheduleList) {
        this.context = context;
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleModel schedule = scheduleList.get(position);
        holder.tvTeacher.setText("Teacher: " + schedule.getTeacher());
        holder.tvDate.setText("Date: " + schedule.getDate());
        holder.tvComment.setText("Comment: " + schedule.getComment());
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {

        TextView tvTeacher, tvDate, tvComment;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTeacher = itemView.findViewById(R.id.tvTeacher);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvComment = itemView.findViewById(R.id.tvComment);
        }
    }
}
