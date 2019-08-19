package com.example.resumeparser2.UserDashboardFragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.resumeparser2.Adapters.ResumeListAdapter;
import com.example.resumeparser2.Models.ResumeListModel;
import com.example.resumeparser2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookmarksFragment extends Fragment {

    private View mMainView;
    private RecyclerView mBookmarksList;
    ArrayList<ResumeListModel> arrayList = new ArrayList<>();
    ResumeListAdapter mAdapter;
    private DatabaseReference mBookmarksDatabase;
    private FirebaseUser mCurrentUser;

    public BookmarksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_bookmarks, container, false);

        mBookmarksList = mMainView.findViewById(R.id.fragment_bookmarks_resumeList);
        mBookmarksList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBookmarksList.setHasFixedSize(true);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mBookmarksDatabase = FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(mCurrentUser.getUid());
        mBookmarksDatabase.keepSynced(true);

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mBookmarksDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ResumeListModel resumeListModel = dataSnapshot1.getValue(ResumeListModel.class);
                    arrayList.add(resumeListModel);
                }

                mAdapter = new ResumeListAdapter(getActivity(), arrayList);
                mBookmarksList.setLayoutManager(new LinearLayoutManager(getActivity()));
                mBookmarksList.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
