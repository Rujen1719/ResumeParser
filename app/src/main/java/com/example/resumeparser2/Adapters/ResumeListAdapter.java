package com.example.resumeparser2.Adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resumeparser2.Models.ResumeListModel;
import com.example.resumeparser2.R;
import com.example.resumeparser2.ResumeDetailsActivity;
import com.example.resumeparser2.UserDashboardFragments.BookmarksFragment;

import java.util.ArrayList;

public class ResumeListAdapter extends RecyclerView.Adapter<ResumeListAdapter.ViewHolder> {

    Activity activity;
    ArrayList<ResumeListModel> arrayList = new ArrayList<>();

    public ResumeListAdapter(Activity activity, ArrayList<ResumeListModel> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ResumeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.resume_list_single_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ResumeListAdapter.ViewHolder holder, final int position) {
        holder.mName.setText("Name: " + arrayList.get(position).getName());
        holder.mSkills.setText("Skills: " + arrayList.get(position).getSkills());
        holder.mCreatedAt.setText("Created At: " + arrayList.get(position).getCreated_at());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, ResumeDetailsActivity.class)
                        .putExtra("resume_id", arrayList.get(position).getResume_id())
                        .putExtra("user_id", arrayList.get(position).getUser_id()));
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mCreatedAt;
        TextView mSkills;


        public ViewHolder(View view) {
            super(view);

            mName = view.findViewById(R.id.resume_list_single_layout_name);
            mCreatedAt = view.findViewById(R.id.resume_list_single_layout_created_at);
            mSkills = view.findViewById(R.id.resume_list_single_layout_skills);

        }
    }
}
