package com.pratham.prathamdigital.ui.fragment_child_attendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.pratham.prathamdigital.PrathamApplication;
import com.pratham.prathamdigital.R;
import com.pratham.prathamdigital.models.Modal_Student;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChildAdapter extends RecyclerView.Adapter<ChildAdapter.ChildHolder> {
    private ArrayList<Modal_Student> datalist;
    private ArrayList<String> child_avatar;
    private Context context;
    private ContractChildAttendance.attendanceView attendanceView;

    public ChildAdapter(Context context, ArrayList<Modal_Student> datalist,
                        ArrayList<String> child_avatar, ContractChildAttendance.attendanceView attendanceView) {
        this.context = context;
        this.datalist = datalist;
        this.child_avatar = child_avatar;
        this.attendanceView = attendanceView;
    }

    @NonNull
    @Override
    public ChildHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = null;
        v = inflater.inflate(R.layout.item_child_attendance, parent, false);
        return new ChildHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildHolder viewHolder, int pos) {
        pos = viewHolder.getAdapterPosition();
        viewHolder.child_name.setText(datalist.get(pos).getFullName());
        viewHolder.child_avatar.setAnimation(child_avatar.get(pos));
//        if (datalist.get(pos).isChecked()) {
//            viewHolder.card_avatar.setCardBackgroundColor(context.getResources().getColor(R.color.green));
//        } else {
//            viewHolder.card_avatar.setCardBackgroundColor(context.getResources().getColor(R.color.white));
//        }
        int finalPos = pos;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PrathamApplication.isTablet)
                    attendanceView.childItemClicked(datalist.get(finalPos), finalPos);
                else
                    attendanceView.moveToDashboardOnChildClick(datalist.get(finalPos), finalPos, viewHolder.itemView);
            }
        });
    }
/*
    @Override
    public void onBindViewHolder(@NonNull ChildHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Modal_Student student = (Modal_Student) payloads.get(0);
            holder.child_name.setText(student.getFullName());
            if (student.isChecked()) {
                holder.card_avatar.setCardBackgroundColor(context.getResources().getColor(R.color.green));
            } else {
                holder.card_avatar.setCardBackgroundColor(context.getResources().getColor(R.color.white));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attendanceView.childItemClicked(datalist.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                }
            });
        }
    }

    public void updateChildItems(final ArrayList<Modal_Student> newStudents) {
        final ChildAttendanceDiffCallback diffCallback = new ChildAttendanceDiffCallback(this.datalist, newStudents);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
//        this.datalist.clear();
//        this.datalist.addAll(newStudents);
        this.datalist = newStudents;
        diffResult.dispatchUpdatesTo(this);
    }*/

    @Override
    public int getItemCount() {
        return datalist.size();
    }

    public class ChildHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.child_name)
        TextView child_name;
        @BindView(R.id.child_avatar)
        LottieAnimationView child_avatar;
//        @BindView(R.id.card_avatar)
//        MaterialCardView card_avatar;

        public ChildHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
