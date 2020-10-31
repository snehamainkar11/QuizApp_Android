package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ResultActivity extends AppCompatActivity {
    TextView score, total;
    RatingBar ratingbar;
    Button button;
    Toolbar toolbar;
    String titlename;
    int sc = 0;
    private Dialog loading;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private List<ScoreData> list;
    final String id = UUID.randomUUID().toString();
    final HashMap<String, Object> map = new HashMap<>();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = findViewById(R.id.tb1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Score");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        score = findViewById(R.id.score);
        button = findViewById(R.id.submit);
        ratingbar = findViewById(R.id.ratingBar);
        total = findViewById(R.id.total);
        titlename = String.valueOf(getIntent().getStringExtra("titlename"));
        score.setText(String.valueOf(getIntent().getIntExtra("score", 0)));
        total.setText(String.valueOf(getIntent().getIntExtra("total", 0)));
        loading = new Dialog(this);
        loading.setContentView(R.layout.loading);
        loading.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loading.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loading.setCancelable(false);

       /*myRef.child("Score").child(user.getUid()).child(titlename).child("result").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot data) {
                data.getRef().setValue(score.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
*/
        map.put("category", titlename.toString());
        map.put("result", Integer.parseInt(score.getText().toString()));
        map.put("total", Integer.parseInt(total.getText().toString()));
        list = new ArrayList<>();

        assert user != null;
        myRef.child("Score").child(user.getUid()).child(id).setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            if(myRef.child("Score").child(user.getUid()).child("category").equals(titlename))
                            {
                            checkScore();
                            }
                            else {
                                list.add(new ScoreData(titlename.toString(), Integer.parseInt(String.valueOf(sc)), Integer.parseInt(total.getText().toString())));
                                Toast.makeText(ResultActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else

                        {

                            Toast.makeText(ResultActivity.this, "Failed to store", Toast.LENGTH_SHORT).show();

                        }


                    }
                });

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                loading.show();
                String rating = String.valueOf(ratingbar.getRating());
                Toast.makeText(getApplicationContext(), "You give us " + rating + " Thanks for Rating", Toast.LENGTH_LONG).show();
                finish();
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }





public void checkScore(){
    myRef.child("Score").child( FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("category").equalTo(titlename.toString())
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot edtData : dataSnapshot.getChildren())
                    {
                        ScoreData data=edtData.getValue(ScoreData.class);
                          int prv = data.getResult();
                            if (prv > Integer.parseInt(score.getText().toString())) {
                                sc = prv;
                            } else {
                                sc = Integer.parseInt(score.getText().toString());
                            }
                            edtData.getRef().child("result").setValue(sc);
                    }

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(ResultActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

}

}