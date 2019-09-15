package com.example.resumeparser2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resumeparser2.ResumeDetailsFragments.EducationFragment;
import com.example.resumeparser2.ResumeDetailsFragments.ExperienceFragment;
import com.example.resumeparser2.ResumeDetailsFragments.PersonalDetailsFragment;
import com.example.resumeparser2.ResumeDetailsFragments.SkillsFragment;
import com.example.resumeparser2.UserDashboardFragments.BookmarksFragment;
import com.example.resumeparser2.UserDashboardFragments.HomeFragment;
import com.example.resumeparser2.UserDashboardFragments.MeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ResumeDetailsActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    Dialog myDialog;
    String userId;
    String resumeId;
    private DatabaseReference mParsedResumesDatabase;
    private DatabaseReference mParsedResumesDatabase2;
    private DatabaseReference mBookmarksDatabase;
    private FloatingActionButton mBookmarkBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_details);

        myDialog = new Dialog(this);
        mBookmarkBtn = findViewById(R.id.bookmarkBtn);

        BottomNavigationView navigation = findViewById(R.id.resume_details_bottom_navigation_view);
        navigation.setOnNavigationItemSelectedListener(this);

        //from ResumeListAdapter
        userId = getIntent().getStringExtra("user_id");
        resumeId = getIntent().getStringExtra("resume_id");

        mParsedResumesDatabase = FirebaseDatabase.getInstance().getReference().child("ParsedResumes").child(userId).child(resumeId);
        mParsedResumesDatabase2 = FirebaseDatabase.getInstance().getReference().child("ParsedView").child(userId).child(resumeId);
        mBookmarksDatabase = FirebaseDatabase.getInstance().getReference().child("Bookmarks").child(userId).child(resumeId);

        mBookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mParsedResumesDatabase2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String resume_id = dataSnapshot.child("resume_id").getValue().toString();
                        String created_at = dataSnapshot.child("created_at").getValue().toString();
                        String user_id = dataSnapshot.child("user_id").getValue().toString();
                        String skills = dataSnapshot.child("skills").getValue().toString();

                        HashMap<String, String> bookmarkMap = new HashMap<>();
                        bookmarkMap.put("name", name);
                        bookmarkMap.put("resume_id", resume_id);
                        bookmarkMap.put("created_at", created_at);
                        bookmarkMap.put("user_id", user_id);
                        bookmarkMap.put("skills", skills);

                        mBookmarksDatabase.setValue(bookmarkMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ResumeDetailsActivity.this, "Added to Bookmarks", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        loadFragments(new PersonalDetailsFragment());
    }

    private boolean loadFragments(Fragment fragment) {
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.resume_details_fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()){
            case R.id.resume_details_personal_details:
                fragment = new PersonalDetailsFragment();
                break;
            case R.id.resume_details_skills:
                fragment = new SkillsFragment();
                break;
            case R.id.resume_details_experience:
                fragment = new ExperienceFragment();
                break;
            case R.id.resume_details_education:
                fragment = new EducationFragment();
                break;
        }

        return loadFragments(fragment);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResumeDetailsActivity.this, UserDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    public void ShowPopUp(View v){
        TextView close_btn;
        myDialog.setContentView(R.layout.email_pop_up);
        close_btn = myDialog.findViewById(R.id.close_btn);

        //email popup
        final EditText mEditTextTo;
        final EditText mEditTextSubject;
        final EditText mEditTextMessage;
        MaterialCardView mSendEmailBtn;

        mEditTextTo = myDialog.findViewById(R.id.edit_text_to);
        mEditTextSubject = myDialog.findViewById(R.id.edit_text_subject);
        mEditTextMessage = myDialog.findViewById(R.id.edit_text_message);
        mSendEmailBtn = myDialog.findViewById(R.id.send_email_btn);

        mParsedResumesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String email = dataSnapshot.child("email").getValue().toString();
                mEditTextTo.setText(email);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipientList = mEditTextTo.getText().toString();
                String[] recipients = recipientList.split(",");

                String subject = mEditTextSubject.getText().toString();
                String message = mEditTextMessage.getText().toString();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, message);

                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Choose an email client"));
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}