package com.example.resumeparser2.ResumeDetailsFragments;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resumeparser2.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalDetailsFragment extends Fragment {

    private View mMainView;
    String userId;
    String resumeId;
    Activity activity;

    private TextView mName;
    private TextView mEmail;
    private TextView mPhone;
    private DatabaseReference mParsedResumesDatabase;
    private LinearLayout mParentLayout;

    private MaterialCardView mCallBtn;

    public PersonalDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_personal_details, container, false);

        activity = getActivity();

        //from ResumeListAdapter
        userId = activity.getIntent().getStringExtra("user_id");
        resumeId = activity.getIntent().getStringExtra("resume_id");

        mCallBtn = mMainView.findViewById(R.id.call_btn);

        Log.d("asd2", resumeId);

        mParsedResumesDatabase = FirebaseDatabase.getInstance().getReference().child("ParsedResumes").child(userId).child(resumeId);

        mName = mMainView.findViewById(R.id.personal_details_name);
        mEmail = mMainView.findViewById(R.id.personal_details_email);
        mPhone = mMainView.findViewById(R.id.personal_details_phone);
        mParentLayout = mMainView.findViewById(R.id.parent_layout);

        mParsedResumesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                final String phone = dataSnapshot.child("phone").getValue().toString();

                mCallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent call_intent = new Intent(Intent.ACTION_DIAL);
                        call_intent.setData(Uri.parse("tel:" + phone));
                        startActivity(call_intent);

                    }
                });

                for (DataSnapshot childSnapshot: dataSnapshot.child("links").getChildren()) {
                    String link = childSnapshot.getValue(String.class);
                    final TextView link_text = new TextView(getActivity());LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,0,15);
                    link_text.setLayoutParams(params);
                    SpannableString content = new SpannableString(link);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    link_text.setText(content);
                    link_text.setTextSize(18);

                    mParentLayout.addView(link_text);

                    link_text.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("Link", link_text.getText().toString());
                            clipboard.setPrimaryClip(clipData);

                            Toast.makeText(getActivity(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                        }
                    });
//                    links += "\n" + link;
                }

                mName.setText(name);
                mEmail.setText(email);
                mPhone.setText(phone);
//                mLinks.setText(links);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return mMainView;
    }


}
