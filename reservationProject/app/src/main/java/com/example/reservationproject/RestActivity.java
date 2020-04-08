package com.example.reservationproject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RestActivity extends AppCompatActivity {

    private User loginedUser;
    private String collection;
    private TextView mTitle;
    private ImageButton bookmark;
    private BookMark mBookmark = new BookMark();
    private List<SeatModel> mSeatList;
    private SeatModel reservingSeat;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private Button mRestStart;
    private Button mRestEnd;


    private int mySeatNum;

    // 뒤로가기키를 누를시 계정 메인 화면으로 이동합니다.
    @Override
    public void onBackPressed() {
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

    public void buttonMakeRestStart(final User loginedUser) {

        mRestStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 휴식 시작
                mStore.collection("열람실 예약").document(mySeatNum + 100 + "").update("rest","o");

                Intent intent = new Intent(getApplicationContext(), AccountHome.class);

                intent.putExtra("id", loginedUser.getId().toString());
                intent.putExtra("pw", loginedUser.getPw().toString());
                intent.putExtra("name", loginedUser.getName().toString());
                intent.putExtra("email", loginedUser.getEmail().toString());
                intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                intent.putExtra("bookMarks", loginedUser.getBookMarks());

                startActivity(intent);
                finish();
            }
        });
    }

    public void buttonMakeRestEnd(final User loginedUser) {
        mRestEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 휴식 종료
                mStore.collection("열람실 예약").document(mySeatNum + 100 + "").update("rest","Rested");

                Intent intent = new Intent(getApplicationContext(), AccountHome.class);

                intent.putExtra("id", loginedUser.getId().toString());
                intent.putExtra("pw", loginedUser.getPw().toString());
                intent.putExtra("name", loginedUser.getName().toString());
                intent.putExtra("email", loginedUser.getEmail().toString());
                intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                intent.putExtra("bookMarks", loginedUser.getBookMarks());

                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);

        // 데이터 받아오기
        Intent intent = getIntent();
        loginedUser = new User();
        loginedUser.setId(intent.getExtras().getString("id").toString());
        loginedUser.setPw(intent.getExtras().getString("pw").toString());
        loginedUser.setName(intent.getExtras().getString("name").toString());
        loginedUser.setEmail(intent.getExtras().getString("email").toString());
        loginedUser.setDocumentId(intent.getExtras().getString("documentId").toString());
        loginedUser.setBookMarks(intent.getExtras().getStringArray("bookMarks"));
        collection = intent.getExtras().getString("collection");

        // 타이틀 셋팅
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        mTitle = findViewById(R.id.activity_title);
        mTitle.setText(collection);
        bookmark = findViewById(R.id.bookmark);

        // 유저의 정보, 게시판 이름, 북마크 이미지 버튼의 정보를 받아서 현재 북마크가 되어있는지를 표시합니다.
        mBookmark.bookmarkTest(loginedUser, collection, bookmark);

        // 마찬가지로 정보를 받아서 북마크를 on/off 할 수 있습니다. 또한 비어있는 공간이 있다면 좌측정렬합니다.
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBookmark.updateBookmark(loginedUser, collection, bookmark);
            }
        });

        mRestStart = findViewById(R.id.rest_start);
        mRestEnd = findViewById(R.id.rest_end);


        mSeatList = new ArrayList<>();
        reservingSeat = new SeatModel();

    mStore.collection("열람실 예약")
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onEvent(@android.support.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                        String documentId = (String) dc.getDocument().getData().get("documentId");
                        String reserve = (String) dc.getDocument().getData().get("reserve");
                        String seat = (String) dc.getDocument().get("seat");
                        String user = (String) dc.getDocument().getData().get("user");
                        String reserve_time = (String) dc.getDocument().getData().get("reserve_time");
                        String now_time = (String) dc.getDocument().getData().get("now_time");
                        String start_time = (String) dc.getDocument().getData().get("start_time");
                        String rest = (String) dc.getDocument().getData().get("rest");
                        SeatModel data = new SeatModel(documentId, reserve, seat, user, reserve_time, now_time, start_time, rest);

                        mSeatList.add(data);
                    }

                    for (int i = 1; i <= 68; i++) {
                        if (mSeatList.get(i - 1).getUser().equals(loginedUser.getId())) {
                            reservingSeat = mSeatList.get(i - 1);
                            mySeatNum = Integer.parseInt(mSeatList.get(i-1).getSeat()) - 100;
                        }
                    }

                    if (reservingSeat.getUser() != null) {
                        // 휴식 정보를 불러와서 어떤 버튼을 보이게 할지 정합니다.
                        if (reservingSeat.getRest().equals("o")) {
                            mRestStart.setVisibility(View.GONE);
                            mRestEnd.setVisibility(View.VISIBLE);
                            // onClick 이벤트
                            buttonMakeRestEnd(loginedUser);
                        }
                        else if (reservingSeat.getRest().equals("x")) {
                            mRestStart.setVisibility(View.VISIBLE);
                            mRestEnd.setVisibility(View.GONE);
                            // onClick 이벤트
                            buttonMakeRestStart(loginedUser);
                        }
                        // 아직 열람실 예약을 하지 않았거나 휴식을 1회 사용한 경우
                    } else {
                        mRestStart.setEnabled(false);
                        mRestEnd.setEnabled(false);
                        Toast.makeText(RestActivity.this, "열람실 기능 이용중 1회 가능합니다.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

    }
}



