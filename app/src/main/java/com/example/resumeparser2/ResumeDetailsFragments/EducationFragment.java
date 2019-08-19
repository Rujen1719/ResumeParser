package com.example.resumeparser2.ResumeDetailsFragments;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.resumeparser2.Adapters.TitleContentListAdapter;
import com.example.resumeparser2.Models.TitleContentModel;
import com.example.resumeparser2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EducationFragment extends Fragment {

    private View mMainView;
    private RecyclerView mEducationList;
    ArrayList<TitleContentModel> arrayList = new ArrayList<>();
    TitleContentListAdapter mAdapter;
    private DatabaseReference mParsedResumesDatabase;
    Activity activity;
    String userId;
    String resumeId;


    public EducationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_education, container, false);

        activity = getActivity();

        mEducationList = mMainView.findViewById(R.id.education_list);
        mEducationList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEducationList.setHasFixedSize(true);

        //from ResumeListAdapter
        userId = activity.getIntent().getStringExtra("user_id");
        resumeId = activity.getIntent().getStringExtra("resume_id");

        mParsedResumesDatabase = FirebaseDatabase.getInstance().getReference().child("ParsedResumes")
                .child(userId).child(resumeId).child("education");
        mParsedResumesDatabase.keepSynced(true);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mParsedResumesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    TitleContentModel titleContentModel = dataSnapshot1.getValue(TitleContentModel.class);
                    arrayList.add(titleContentModel);
                }

                mAdapter = new TitleContentListAdapter(getActivity(), arrayList);
                mEducationList.setLayoutManager(new LinearLayoutManager(getActivity()));
                mEducationList.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
