package com.example.myapplication;

import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.app.ProgressDialog;
import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {
    Button btnSignIn;
    TextView txtsignUp;
    ProgressDialog pd;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtsignUp = (TextView) findViewById(R.id.btnSignUP);

        txtsignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intentSignUP = new Intent(getApplicationContext(), SignUpactivity.class);
                startActivity(intentSignUP);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            //close this activity
            finish();
            //opening profile activity
            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
            finish();
        }
    /*  if (mAuth.getCurrentUser() != null) {
            updateUI(mAuth.getCurrentUser());
        }*/
    }

    public void resetPass(View V) {

        Intent intentReset = new Intent(getApplicationContext(), ResetPasswordActivity.class);
        startActivity(intentReset);

    }

    public void signIn(View V) {

        final EditText editTextUserName = (EditText) findViewById(R.id.editTextUserNameToLogin);
        final EditText editTextPassword = (EditText) findViewById(R.id.editTextPasswordToLogin);

        Button btnSignIn = (Button) findViewById(R.id.buttonSignIn);


        btnSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pd = new ProgressDialog(MainActivity.this);
                pd.setTitle("Login...");
                pd.setMessage("Authenticating");
                pd.setCancelable(false);
                pd.setIndeterminate(true);
                pd.show();


                final String userName = editTextUserName.getText().toString();
                final String password = editTextPassword.getText().toString();

                if (TextUtils.isEmpty(userName)) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener
                        (MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                              //updateUI(user);
                            Intent profileIntent = new Intent(MainActivity.this, DashboardActivity.class);
                            startActivity(profileIntent);
                            finish();

                        } else {// there was an error
                            if (password.length() < 6) {
                                pd.dismiss();
                                editTextPassword.setError(getString(R.string.minimum_password));
                            } else
                                pd.dismiss();
                            Toast.makeText(MainActivity.this, "User Name or Password does not match", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }

            //@Override
            // protected void onDestroy() {
            //    super.onDestroy();
// Close The Database
            // loginDataBaseAdapter.close();
            // }
        });

    }

    }

