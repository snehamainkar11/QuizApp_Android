package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScoreBoardActivity extends AppCompatActivity {
    private List<ScoreData> list;
    RecyclerView recyclerView;
     ScoreAdapter adapter;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Scorecard");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView=findViewById(R.id.rv);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        list=new ArrayList<>();
        adapter=new ScoreAdapter(list);
        recyclerView.setAdapter(adapter);
        myRef.child("Score").child(String.valueOf(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()))

                .addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                try{
                    for(DataSnapshot datasnapshot1: datasnapshot.getChildren()){
                        list.add(datasnapshot1.getValue(ScoreData.class));


                    }
                    adapter.notifyDataSetChanged();

                }


                catch(Exception e)
                {
                    Toast.makeText(ScoreBoardActivity.this,e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ScoreBoardActivity.this,error.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }


        } );
    }

    @Override
    protected  void onStart(){
        super.onStart();
        adapter.notifyDataSetChanged();

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}