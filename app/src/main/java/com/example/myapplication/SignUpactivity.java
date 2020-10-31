package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;

import java.util.Objects;

import static android.content.ContentValues.TAG;
public class SignUpactivity extends Activity
{
    EditText editTextUserName,editTextPassword,editTextConfirmPassword,editTextcontact,editTextName;
    Button btnCreateAccount;
    ProgressDialog pd;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    User user;
    String url="https://firebasestorage.googleapis.com/v0/b/quizapp-777db.appspot.com/o/userprofile%2Fstorage%2Femulated%2F0%2FWhatsApp%2FMedia%2FWhatsApp%20Images%2FIMG-20201004-WA0011.jpg?alt=media&token=baf2ff54-668c-4e4d-8b60-4e36c04f3a0c";
    String name,confirmPassword,password,userName,contact;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("user");
        mAuth = FirebaseAuth.getInstance();

        editTextName=(EditText)findViewById(R.id.editTextName);
        editTextUserName=(EditText)findViewById(R.id.editTextUserName);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        editTextConfirmPassword=(EditText)findViewById(R.id.editTextConfirmPassword);
        editTextcontact=(EditText)findViewById(R.id.contact);
        btnCreateAccount=(Button)findViewById(R.id.buttonCreateAccount);


        btnCreateAccount.setOnClickListener(new View.OnClickListener() {

            public void onClick(View V) {
                pd = new ProgressDialog(SignUpactivity.this);
                pd.setTitle("Creating Account...");
                pd.setMessage("Authenticating");
                pd.setCancelable(true);
                pd.setIndeterminate(true);
                user = new User(name, userName, password, contact,"");

                if (!validateFullname() |  !validateEmail() | !validatePassword() | !validateContact()) {
                  return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
                    editTextUserName.setError("Please enter valid email");
                }
                if (password.equals(confirmPassword)) {
                    pd.show();

                    mAuth.createUserWithEmailAndPassword(userName, password).addOnCompleteListener
                            (new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                                        pd.dismiss();
                                                        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                                        updateUI(user);
                                                        Toast.makeText(SignUpactivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();

                                                    finish();
                                                    }
                                    else{
                                        pd.dismiss();
                                        Toast.makeText(SignUpactivity.this, "You are already registered..", Toast.LENGTH_SHORT).show();
                                        finish();

                                    }
                                }
                            });
                } else {
                    pd.dismiss();
                    Toast.makeText(SignUpactivity.this, "Password didn't match", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean validateFullname() {
        name = editTextName.getText().toString();

        if (TextUtils.isEmpty(name)) {
            editTextName.setError("Required");
            return false;
        } else {
            return true;
        }
    }
    private boolean validateContact() {
        contact = editTextcontact.getText().toString().trim();
        if (TextUtils.isEmpty(contact)) {
            editTextcontact.setError("Required");
            return false;
        }else if (contact.length() !=10) {
            Toast.makeText(SignUpactivity.this, "Invalid Contact Number", Toast.LENGTH_SHORT).show();
            return false;}
        else {
            return true;
        }
    }

    private boolean validateEmail() {
        userName = editTextUserName.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            editTextUserName.setError("Required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
            editTextUserName.setError("Please enter valid email");
            return false;
        } else {
            return true;
        }
    }

    private boolean validatePassword() {
        password = editTextPassword.getText().toString().trim();
        confirmPassword = editTextConfirmPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password Required");
            return false;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmPassword.setError("Password Required");
            return false;
        } else if (password.length() <= 6) {
            Toast.makeText(SignUpactivity.this, "Password is Very Short", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    public void updateUI(FirebaseUser currentUser) {
        String keyid = mDatabase.push().getKey();
         mDatabase.child(keyid).setValue(user);
         //finish();
        //Intent loginIntent = new Intent(this, MainActivity.class);
        //startActivity(loginIntent);
    }

}
