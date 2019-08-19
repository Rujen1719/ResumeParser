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
import com.example.resumeparser2.Models.TitleContentModel;
import com.example.resumeparser2.R;
import com.example.resumeparser2.ResumeDetailsActivity;
import com.example.resumeparser2.UserDashboardFragments.BookmarksFragment;

import java.util.ArrayList;

public class TitleContentListAdapter extends RecyclerView.Adapter<TitleContentListAdapter.ViewHolder> {

    Activity activity;
    ArrayList<TitleContentModel> arrayList = new ArrayList<>();

    public TitleContentListAdapter(Activity activity, ArrayList<TitleContentModel> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public TitleContentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_layout_title_content, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TitleContentListAdapter.ViewHolder holder, final int position) {
        holder.mContent.setText(arrayList.get(position).getContent());
        holder.mTitle.setText(arrayList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitle;
        TextView mContent;


        public ViewHolder(View view) {
            super(view);

            mTitle = view.findViewById(R.id.single_layout_title);
            mContent = view.findViewById(R.id.single_layout_content);

        }
    }
}
