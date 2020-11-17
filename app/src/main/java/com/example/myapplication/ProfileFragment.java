package com.example.myapplication;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
public class ProfileFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    public EditText name;
    public EditText email;
    public EditText contact;
    private Button btnupload;
    private ImageView imgProfile;
    public User users;
    private Uri image;
    String downloadUrl,dw;
    private StorageReference UserProfileImagesRef;
    private ProgressDialog loadingBar;
    private String currentUserID;

    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = (EditText) view.findViewById(R.id.name);
        email = (EditText) view.findViewById(R.id.email);
        contact = (EditText) view.findViewById(R.id.contact);
        btnupload = (Button) view.findViewById(R.id.uploadprofile);
        imgProfile = (ImageView) view.findViewById(R.id.userprofile);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("user");
        currentUserID = mAuth.getCurrentUser().getUid();
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("userprofile");


        showUser(name,email,contact,imgProfile);
        //uploadData();


        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery,101);

            }
        });

        btnupload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String nm=name.getText().toString();
                String em=email.getText().toString();
                String cn=contact.getText().toString();
                if (TextUtils.isEmpty(cn)) {
                    contact.setError("Required");
                }else if (contact.length() !=10) {
                    Toast.makeText(getContext(), "Invalid Contact Number", Toast.LENGTH_SHORT).show();
                }
                else {
                    editUser(nm, em, cn, downloadUrl);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            image = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), image);
                imgProfile.setImageBitmap(bitmap);
                uploadData();

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void showUser(final EditText uname, final EditText usremail, final EditText usrcontact,final ImageView profile) {
        mDatabaseReference.orderByChild("userName").equalTo(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot SubSnapshot : snapshot.getChildren()) {
                            users = SubSnapshot.getValue(User.class);
                            assert users != null;
                            uname.setText(users.getName().toString());
                            usremail.setText(users.getUserName().toString());
                            usrcontact.setText(users.getContact().toString());
                            try {
                                Glide.with(getContext()).load(users.getUrl()).into(profile);
                            }
                             catch (Exception e){
                                Glide.with(getContext()).load(R.drawable.user_profile).into(profile);

                            }
                      }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }

                });
    }

    private void editUser(final String name1, final String email1, final String contact1,final String urls) {
            Query editQuery = mDatabaseReference.orderByChild("userName").equalTo(email1.toString());
            editQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot edtData: snapshot.getChildren()){
                        edtData.getRef().child("name").setValue(name1);
                        edtData.getRef().child("userName").setValue(email1);
                        edtData.getRef().child("contact").setValue(contact1);
                        edtData.getRef().child("url").setValue(""+urls);

                    }
                    Toast.makeText(getContext(),"Profile Updated" ,Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(),"Failed to Update" ,Toast.LENGTH_LONG).show();

                }


            });

        }

    private void uploadData() {
        if (image != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
           progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            final StorageReference imageReference = storageReference.child("userprofile").child(Objects.requireNonNull(image.getLastPathSegment()));
        imageReference.putFile(image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                           progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
            imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    downloadUrl=uri.toString();
                    Log.d("URI", uri.toString()); //check path is correct or not ?
                   //FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                   // mDatabaseReference.child(Objects.requireNonNull(mDatabaseReference.push().getKey())).child("url").setValue(downloadUrl.toString());
                    //You will get donwload URL in uri
                    //Adding that URL to Realtime database
                }
            });
        }
        }


        }


