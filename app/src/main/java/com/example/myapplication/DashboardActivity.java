package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    User users;
    NavigationView navigationView;
    ProgressDialog pd;
    TextView email,fname;
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("user");
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        Query query=mDatabaseReference.orderByChild("userName").equalTo(user.getEmail());
        navigationView = findViewById(R.id.nav_view);
        View mHeaderView=  navigationView.getHeaderView(0);
        email= mHeaderView.findViewById(R.id.txtemail);
        fname= mHeaderView.findViewById(R.id.txtname);
        img= mHeaderView.findViewById(R.id.profile);


        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeActivity()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }


    query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot SubSnapshot : dataSnapshot.getChildren()) {
                        String name=""+SubSnapshot.child("name").getValue();
                        String uname=""+SubSnapshot.child("userName").getValue();
                        String image=""+SubSnapshot.child("url").getValue();
                        fname.setText(name);
                        email.setText(uname);
                        try{
                            Picasso.get().load(image).into(img);
                        }
                        catch (Exception e){
                            Picasso.get().load(R.drawable.user_profile).into(img);

                        }


                        /*users = SubSnapshot.getValue(User.class);
                        assert users != null;
                        email.setText(users.getUserName().toString());
                        fname.setText(users.getName().toString());
                        Glide.with(getApplicationContext()).load(users.getUrl()).into(img);*/

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(DashboardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (item.isChecked()) {
            drawer.closeDrawer(GravityCompat.START);
            return false;
        }
        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeActivity()).commit();
        } else if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        } else if (id == R.id.nav_about) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new About_us()).commit();
        }
        else if (id == R.id.nav_bookmark) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new BookmarkActivity()).commit();
        }
        else if (id == R.id.nav_share) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SettingsFragment()).commit();        }
        else if (id == R.id.nav_logout) {
            pd = new ProgressDialog(DashboardActivity.this);
            pd.setTitle("Logout...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();

            mAuth.getInstance().signOut();
            startActivity(new Intent(DashboardActivity.this, MainActivity.class));
            finish();
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }

    }
