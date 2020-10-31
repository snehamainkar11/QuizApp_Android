package com.example.myapplication;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyboardShortcutGroup;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class QuestionActivity extends AppCompatActivity {
    public static final String FILE_NAME="QUIZAPP";
    public static final String KEYNAME="QUESTIONS";
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
    private TextView question,noindicator,textViewCountDown;
    private FloatingActionButton bookmarkBtn;
    private LinearLayout optionsContainer;
    private Button nextBtn;
    List<QuestionModel> list;
    private int score=0;
    private int count=0;
    private int position=0;
    private String category;
    private  int setNo;
    private Dialog loading;
    private List<QuestionModel> bookmarkList;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private int matchquestionpos;

public int counter=0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Toolbar toolbar = findViewById(R.id.toolbar);

        question = findViewById(R.id.txtquestion);
        noindicator = findViewById(R.id.no_indicator);
        bookmarkBtn = findViewById(R.id.bookmarkBtn);
        optionsContainer = findViewById(R.id.options_container);
        nextBtn = findViewById(R.id.btnNext);
        textViewCountDown = findViewById(R.id.txtTimer);
        category = getIntent().getStringExtra("category");
        setNo = getIntent().getIntExtra("setNo", 1);
        final String titlename=category+ "-"+(setNo);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(titlename);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferences=getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor=preferences.edit();
        gson=new Gson();
        getBookmarks();
        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modelMatch()){
                    bookmarkList.remove(matchquestionpos);
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));

                }else{
                    bookmarkList.add(list.get(position));
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_bookmark_24));

                }
            }
        });
        loading=new Dialog(this);
        loading.setContentView(R.layout.loading);
        loading.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loading.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loading.setCancelable(false);

        question.setVerticalScrollBarEnabled(true);
        question.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        question.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
        question.setMovementMethod(ScrollingMovementMethod.getInstance());
        question.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                if ((motionEvent.getAction() & MotionEvent.ACTION_UP) != 0 && (motionEvent.getActionMasked() & MotionEvent.ACTION_UP) != 0)
                {
                    view.getParent().requestDisallowInterceptTouchEvent(false);
                }
                return false;
            }
        });
        list = new ArrayList<>();
        loading.show();
        myRef.child("SETS").child(category).child("questions").orderByChild("setNo").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot snapshot : datasnapshot.getChildren()) {
                    list.add(snapshot.getValue(QuestionModel.class));
                }
                if (list.size() > 0) {
                    playAnim(question, 0, list.get(position).getQuestion());
                    TimerTick();


                    for (int i = 0; i < 4; i++) {
                        optionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                checkAnswer((Button) v);
                            }
                        });
                    }
                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            nextBtn.setEnabled(false);
                            nextBtn.setAlpha(0.7f);
                            enableOption(true);
                            position++;

                            if (position == list.size()) {
                                    loading.show();
                                    Intent scoreIntent = new Intent(QuestionActivity.this, ResultActivity.class);
                                    scoreIntent.putExtra("score", score);
                                    scoreIntent.putExtra("total", list.size());
                                    scoreIntent.putExtra("titlename", titlename);

                                    startActivity(scoreIntent);
                                        finish();
                                        return;
                            }
                            count = 0;
                            playAnim(question, 0, list.get(position).getQuestion());

                        }
                    });

                }
                else{
                    finish();
                    Toast.makeText(QuestionActivity.this,"No Questions",Toast.LENGTH_SHORT).show();
                }
                loading.dismiss();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuestionActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
                loading.dismiss();
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    private void playAnim(final View view, final int value, final String data)
    {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener()
                                                                           {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        String option = "";
                        if (value == 0 && count < 4) {
                            if (count == 0) {
                                option = list.get(position).getOption1();
                            } else if (count == 1) {
                                option = list.get(position).getOption2();

                            } else if (count == 2) {
                                option = list.get(position).getOption3();

                            } else if (count == 3) {
                                option = list.get(position).getOption4();
                            }

                            playAnim(optionsContainer.getChildAt(count), 0, option);
                            count++;


                        }
                        }


                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                                          if(value==0){
                                              try{

                                                  ((TextView)view).setText(data);
                                                  noindicator.setText(position+1+"/"+list.size());
                                                  if(modelMatch()){
                                                      bookmarkBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_bookmark_24));
                                                  }else{
                                                      bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));


                                                  }

                                              }
                                              catch(ClassCastException ex)
                                              {
                                                  ((Button)view).setText(data);
                                              }
                                              view.setTag(data);
                                              playAnim(view,1,data);

                        }
                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                                                                           }

        );
    }

    private void checkAnswer(Button selectedoption){
        enableOption(false);
        nextBtn.setEnabled(true);
        nextBtn.setAlpha(1);

        if(selectedoption.getText().toString().equals(list.get(position).getCorrectAns()))
        {
            selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#388E3C")));
            score++;
        }
        else{
            selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D32F2F")));
            Button correctoption=(Button)optionsContainer.findViewWithTag(list.get(position).getCorrectAns());
            correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#388E3C")));
        }

    }
    private void enableOption(boolean enable)
    {
        for(int i=0;i<4;i++){
            optionsContainer.getChildAt(i).setEnabled(enable);

            if(enable)
            optionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9FCEFB")));

        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
private void TimerTick(){
    textViewCountDown.setText("00:10:00");
    CountDownTimer timer = new CountDownTimer(600000, 10) {
        @Override
        public void onTick(long millisUntilFinished) {
            long millis = millisUntilFinished;
            String hms = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(millis)));
            if (millis < 10000) {
                textViewCountDown.setTextColor(Color.RED);
            } else {
                textViewCountDown.setTextColor(Color.GREEN);
            }
            textViewCountDown.setText(hms);
        }

        @Override
        public void onFinish() {
            textViewCountDown.setText("Time is up");
            finish();


        }
    };
    timer.start();
}
private void getBookmarks(){
       String json= preferences.getString(KEYNAME,"");
       Type type=new TypeToken<List<QuestionModel>>(){}.getType();
       bookmarkList=gson.fromJson(json,type);
       if(bookmarkList==null){
           bookmarkList=new ArrayList<>();
       }

}
private boolean modelMatch(){
        boolean match=false;
        int i=0;
        for(QuestionModel model:bookmarkList) {
            if (model.getQuestion().equals(list.get(position).getQuestion())
                    && model.getCorrectAns().equals(list.get(position).getCorrectAns())
                    && model.getSetNo() == list.get(position).getSetNo()){
                match = true;
                matchquestionpos=i;
        }
            i++;
        }
        return  match;

        }

    private void storeBookmarks(){
        String json= gson.toJson(bookmarkList);
        editor.putString(KEYNAME,json);
        editor.commit();
    }
}