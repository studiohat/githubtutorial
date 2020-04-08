package com.example.reservationproject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

// 게시글을 클릭하면 넘어오는 화면입니다.
public class ShowActivity extends AppCompatActivity {

    private String title;
    private String contents;
    private String name;
    private String time;

    private TextView mTitle;
    private TextView mContents;
    private TextView mName;
    private TextView mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        // 넘어온 정보를 바탕으로 화면을 구성합니다.
        Intent intent = getIntent();

        title = intent.getExtras().getString("title");
        contents = intent.getExtras().getString("contents");
        name = intent.getExtras().getString("name");
        time = intent.getExtras().getString("time");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        mTitle = findViewById(R.id.activity_title);
        mTitle.setText(title);
        findViewById(R.id.bookmark).setVisibility(View.GONE);

        mContents = findViewById(R.id.show_contents);
        mContents.setText(contents);

        mName = findViewById(R.id.show_name);
        mName.setText("작성자 : " + name);

        mTime = findViewById(R.id.show_time);
        mTime.setText(time);
    }
}
