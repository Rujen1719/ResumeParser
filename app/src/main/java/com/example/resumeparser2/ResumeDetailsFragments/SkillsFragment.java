package com.example.resumeparser2.ResumeDetailsFragments;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resumeparser2.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SkillsFragment extends Fragment {

    private View mMainView;
    String userId;
    String resumeId;
    Activity activity;
    private DatabaseReference mParsedResumesDatabase;

    private LinearLayout mSkillsParentLayout;

    public SkillsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_skills, container, false);

        mSkillsParentLayout = mMainView.findViewById(R.id.skills_parent_layout);

        activity = getActivity();

        //from ResumeListAdapter
        userId = activity.getIntent().getStringExtra("user_id");
        resumeId = activity.getIntent().getStringExtra("resume_id");

        mParsedResumesDatabase = FirebaseDatabase.getInstance().getReference().child("ParsedResumes").child(userId).child(resumeId);

        mParsedResumesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot: dataSnapshot.child("skills").getChildren()) {
                    String skill = childSnapshot.getValue(String.class);
                    final TextView skill_text = new TextView(getActivity());LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,0,10);
                    skill_text.setLayoutParams(params);
                    Drawable drawable = getResources().getDrawable(R.drawable.icon_arrow_right_black);
                    skill_text.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    skill_text.setCompoundDrawablePadding(10);
                    skill_text.setPadding(10,10,10,10);
                    skill_text.setText(skill);
                    skill_text.setTextSize(22);

                    mSkillsParentLayout.addView(skill_text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return mMainView;
    }

}
