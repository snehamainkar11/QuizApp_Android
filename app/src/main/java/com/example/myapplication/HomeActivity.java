package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.myapplication.CategoriesActivity;
import com.example.myapplication.R;

public class HomeActivity extends Fragment {
private ImageView category,score;
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.activity_home, container, false);



        super.onCreate(savedInstanceState);
        category = (ImageView)view.findViewById(R.id.imgcategory);
        score = (ImageView) view.findViewById(R.id.imgscore);


        category.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext() , CategoriesActivity.class);
                startActivity(intent1);
            }
        });
        score.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = new Intent(getContext() , ScoreBoardActivity.class);
                startActivity(intent1);
            }
        });



return view;
    }
}