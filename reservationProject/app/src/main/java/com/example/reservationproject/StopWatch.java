package com.example.reservationproject;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


// 부가기능의 스탑워치 기능입니다.
public class StopWatch extends AppCompatActivity {

    private TextView mTitle;

    private Button mStartButton;
    private Button mPauseButton;
    private Button mInitializeButton;
    private TextView mTimeView;
    private Thread timeThread = null;
    private static Boolean isStarting = false;
    private static Boolean isRunning = true;
    public static Handler handler;


    public static String collection;
    private User loginedUser;
    private ImageButton bookmark;
    private BookMark stopwatchBookmark = new BookMark();

    private boolean isFirstexcuted = false;

    // 시작하면 스레드를 시작하면서 시간초 텍스트를 변경
    class tThread implements Runnable {
        @Override
        public void run() {
            int i = 0;

            while (true) {
                while (isRunning) {
                    Message msg = new Message();
                    msg.arg1 = i++;
                    StopWatch.handler.sendMessage(msg);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void run() {
                                mTimeView.setText("");
                                mTimeView.setText("00:00:00:00");
                            }
                        });
                        return;
                    }
                }
            }
        }
    }


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_watch);

        // 화면 상단의 타이틀
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        mTitle = findViewById(R.id.activity_title);
        mTitle.setText("스탑 워치");

        // 화면에 데이터 받아오기
        Intent intent = getIntent();

        //유저 객체..
        loginedUser = new User();

        loginedUser.setId(intent.getExtras().getString("id").toString());
        loginedUser.setPw(intent.getExtras().getString("pw").toString());
        loginedUser.setName(intent.getExtras().getString("name").toString());
        loginedUser.setEmail(intent.getExtras().getString("email").toString());
        loginedUser.setDocumentId(intent.getExtras().getString("documentId").toString());
        loginedUser.setBookMarks(intent.getExtras().getStringArray("bookMarks"));

        collection = intent.getExtras().getString("collection");

        bookmark = findViewById(R.id.bookmark);


        // 북마크가 되어있는지 확인하고 이미지 셋팅
        stopwatchBookmark.bookmarkTest(loginedUser, collection, bookmark);

        // 북마크 버튼 on/off
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopwatchBookmark.updateBookmark(loginedUser, collection, bookmark);
            }
        });


        mStartButton = findViewById(R.id.stop_watch_start);
        mPauseButton = findViewById(R.id.stop_watch_restart);
        mInitializeButton = findViewById(R.id.stop_watch_initialize);
        mTimeView = findViewById(R.id.stop_watch_time);

        // 스탑워치의 시간초 텍스트 받아와서 출력
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int mSec = msg.arg1 % 100;
                int sec = (msg.arg1 / 100) % 60;
                int min = (msg.arg1 / 100) / 60;
                int hour = (msg.arg1 / 100) / 360;
                //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

                @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);

                mTimeView.setText(result);
            }
        };


        mStartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isStarting == false) {
                    isStarting = true;
                    v.setVisibility(View.GONE);
                    mPauseButton.setVisibility(View.VISIBLE);

                    timeThread = new Thread(new tThread());
                    timeThread.start();
                    isFirstexcuted = true;
                }
            }
        });

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = !isRunning;
                if(isRunning) {
                    mPauseButton.setText("일시정지");
                }
                else {
                    mPauseButton.setText("시작");
                }
            }
        });

        mInitializeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isFirstexcuted) {
                    isStarting = false;
                    isRunning = true;
                    mPauseButton.setText("일시정지");

                    mStartButton.setVisibility(View.VISIBLE);
                    mPauseButton.setVisibility(View.GONE);

                    timeThread.interrupt();
                    timeThread = new Thread(new tThread());
                }
            }
        });
    }

    // 뒤로가기키 누를시 스탑워치를 초기화하고 계정 메인화면으로 이동
    @Override
    public void onBackPressed() {
        if(isFirstexcuted) {
            isStarting = false;
            isRunning = true;
            mPauseButton.setText("일시정지");

            mStartButton.setVisibility(View.VISIBLE);
            mPauseButton.setVisibility(View.GONE);

            timeThread.interrupt();
            timeThread = new Thread(new tThread());
        }

        Intent intent = new Intent(getApplicationContext(), AccountHome.class);

        intent.putExtra("id", loginedUser.getId().toString());
        intent.putExtra("pw", loginedUser.getPw().toString());
        intent.putExtra("name", loginedUser.getName().toString());
        intent.putExtra("email", loginedUser.getEmail().toString());
        intent.putExtra("documentId", loginedUser.getDocumentId().toString());
        intent.putExtra("bookMarks", loginedUser.getBookMarks());

        startActivity(intent);
        finish();

        super.onBackPressed();

    }
}
