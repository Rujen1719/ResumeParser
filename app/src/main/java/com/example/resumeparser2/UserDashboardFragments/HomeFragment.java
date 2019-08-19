package com.example.resumeparser2.UserDashboardFragments;


import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.resumeparser2.Adapters.ResumeListAdapter;
import com.example.resumeparser2.Models.ResumeListModel;
import com.example.resumeparser2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private final int PDF_PICK_CODE = 1000;
    private final int JPG_PICK_CODE = 1001;
    private ProgressDialog mPdfProgressDialog;
    private ProgressDialog mJpgProgressDialog;

    private DatabaseReference mResumePdf;
    private DatabaseReference mResumeJpg;
    private DatabaseReference mRootRef;

    private View mMainView;
    private RecyclerView mResumeList;
    ArrayList<ResumeListModel> arrayList = new ArrayList<>();
    private SearchView mSearchBar;
    private FloatingActionButton mUploadResumeBtn;

    ResumeListAdapter mAdapter;

    private DatabaseReference mParsedResumeDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mResumeStorage;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView =  inflater.inflate(R.layout.fragment_home, container, false);

        mSearchBar = mMainView.findViewById(R.id.fragment_home_searchBar);
        mUploadResumeBtn = mMainView.findViewById(R.id.fragment_home_uploadResumeBtn);

        mResumeList = mMainView.findViewById(R.id.fragment_home_resumeList);
        mResumeList.setLayoutManager(new LinearLayoutManager(getActivity()));
        mResumeList.setHasFixedSize(true);

        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.keepSynced(true);
        mResumeStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mParsedResumeDatabase = FirebaseDatabase.getInstance().getReference().child("ParsedView").child(mCurrentUser.getUid());
        mParsedResumeDatabase.keepSynced(true);

        mUploadResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{
                        "PDF Format", "JPG Format"
                };

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Choose File Format");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int j) {
                        //Click event for each item
                        if (j == 0) {
                            Intent filePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            filePickerIntent.setType("application/pdf");
                            filePickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            startActivityForResult(filePickerIntent, PDF_PICK_CODE);
                        }
                        if (j == 1) {
                            Intent filePickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            filePickerIntent.setType("image/*");
                            filePickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            startActivityForResult(filePickerIntent, JPG_PICK_CODE);
                        }
                    }
                });
                builder.show();
            }
        });
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mParsedResumeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ResumeListModel resumeListModel = dataSnapshot1.getValue(ResumeListModel.class);
                    arrayList.add(resumeListModel);
                }

                mAdapter = new ResumeListAdapter(getActivity(), arrayList);
                mResumeList.setLayoutManager(new LinearLayoutManager(getActivity()));
                mResumeList.setAdapter(mAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (mSearchBar!= null){
            mSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return false;
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PDF_PICK_CODE && resultCode == RESULT_OK) {

            mPdfProgressDialog = new ProgressDialog(getActivity());
            mPdfProgressDialog.setTitle("Uploading Resume...");
            mPdfProgressDialog.setMessage("Please wait while we upload and process the PDF file.");
            mPdfProgressDialog.setCanceledOnTouchOutside(false);
            mPdfProgressDialog.show();

            ClipData clipData = data.getClipData();
            if (clipData != null) {

                for (int i = 0; i < clipData.getItemCount(); i++) {

                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();

                    mResumePdf = mRootRef.child("Resumes").child(mCurrentUser.getUid()).push();
                    final String pdfPushId = mResumePdf.getKey();
                    final StorageReference pdfFilePath = mResumeStorage.child("Resumes").child(mCurrentUser.getUid()).child("PDFs")
                            .child(pdfPushId + ".pdf");

                    String currentDate = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    }

                    final String finalCurrentDate = currentDate;

                    pdfFilePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                Map resumeMap = new HashMap();
                                resumeMap.put("resume_name", pdfPushId);
                                resumeMap.put("resume_format", "pdf");
                                resumeMap.put("created_at", finalCurrentDate);

                                mRootRef.child("Resumes").child(mCurrentUser.getUid()).child(pdfPushId).updateChildren(resumeMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Success uploading",
                                                    Toast.LENGTH_SHORT).show();
                                            mPdfProgressDialog.dismiss();
                                        }
                                    }
                                });

                            }else {
                                Toast.makeText(getActivity(), "Error in uploading.", Toast.LENGTH_SHORT).show();
                                mPdfProgressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        } else if (requestCode == JPG_PICK_CODE && resultCode == RESULT_OK){

            mJpgProgressDialog = new ProgressDialog(getActivity());
            mJpgProgressDialog.setTitle("Uploading Resume...");
            mJpgProgressDialog.setMessage("Please wait while we upload and process the image.");
            mJpgProgressDialog.setCanceledOnTouchOutside(false);
            mJpgProgressDialog.show();

            ClipData clipData = data.getClipData();
            if (clipData != null) {

                for (int i = 0; i < clipData.getItemCount(); i++) {

                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();

                    mResumeJpg = mRootRef.child("Resumes").child(mCurrentUser.getUid()).push();
                    final String jpgPushId = mResumeJpg.getKey();
                    final StorageReference jpgFilePath = mResumeStorage.child("Resumes").child(mCurrentUser.getUid()).child("JPGs")
                            .child(jpgPushId + ".jpg");

                    String currentDate = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    }

                    final String finalCurrentDate = currentDate;

                    jpgFilePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                Map jpgResumeMap = new HashMap();
                                jpgResumeMap.put("resume_name", jpgPushId);
                                jpgResumeMap.put("resume_format", "jpg");
                                jpgResumeMap.put("created_at", finalCurrentDate);

                                mRootRef.child("Resumes").child(mCurrentUser.getUid()).child(jpgPushId).updateChildren(jpgResumeMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getActivity(), "Success uploading",
                                                    Toast.LENGTH_SHORT).show();
                                            mJpgProgressDialog.dismiss();
                                        }
                                    }
                                });

                            }else {
                                Toast.makeText(getActivity(), "Error in uploading.", Toast.LENGTH_SHORT).show();
                                mJpgProgressDialog.dismiss();
                            }
                        }
                    });
                }
            }

        }
    }

    private void search(String newText) {

        ArrayList<ResumeListModel> arrayList1 = new ArrayList<>();
        for (ResumeListModel resumeListModel1 : arrayList){
            if (resumeListModel1.getSkills().toLowerCase().contains(newText.toLowerCase()) ||
                    resumeListModel1.getEducation().toLowerCase().contains(newText.toLowerCase()) ||
                    resumeListModel1.getExperience().toLowerCase().contains(newText.toLowerCase())){
                arrayList1.add(resumeListModel1);
            }
        }
        ResumeListAdapter resumeListAdapter = new ResumeListAdapter(getActivity(), arrayList1);
        mResumeList.setAdapter(resumeListAdapter);

    }

}
