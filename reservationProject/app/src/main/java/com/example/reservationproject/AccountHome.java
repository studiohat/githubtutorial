package com.example.reservationproject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

// 로그인에 성공시 계정의 홈화면입니다.
public class AccountHome extends AppCompatActivity {

    private TextView mTitle;
    private User loginedUser;
    private List<ImageButton> mBookMark;
    private BookMark imgReturn = new BookMark();

    private AccumulatedTimeModel mTime;
    private List<AccumulatedTimeModel> mTimeList;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    public void setBookmarkBtn(List<ImageButton> mBookMark, final int i) {
        mBookMark.get(i).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginedUser.getBookMarks()[i].equals("자유 게시판") || loginedUser.getBookMarks()[i].equals("신고 게시판") || loginedUser.getBookMarks()[i].equals("공지 게시판")) {
                    Intent intent = new Intent(getApplicationContext(), Board.class);

                    intent.putExtra("id", loginedUser.getId().toString());
                    intent.putExtra("pw", loginedUser.getPw().toString());
                    intent.putExtra("name", loginedUser.getName().toString());
                    intent.putExtra("email", loginedUser.getEmail().toString());
                    intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                    intent.putExtra("bookMarks", loginedUser.getBookMarks());

                    intent.putExtra("collection", loginedUser.getBookMarks()[i]);
                    startActivity(intent);
                    finish();
                } else if (loginedUser.getBookMarks()[i].equals("스탑워치")) {
                    Intent intent = new Intent(getApplicationContext(), StopWatch.class);

                    intent.putExtra("id", loginedUser.getId().toString());
                    intent.putExtra("pw", loginedUser.getPw().toString());
                    intent.putExtra("name", loginedUser.getName().toString());
                    intent.putExtra("email", loginedUser.getEmail().toString());
                    intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                    intent.putExtra("bookMarks", loginedUser.getBookMarks());

                    intent.putExtra("collection", loginedUser.getBookMarks()[i]);
                    startActivity(intent);
                    finish();
                } else if (loginedUser.getBookMarks()[i].equals("열람실 예약")) {
                    Intent intent = new Intent(getApplicationContext(), ReservationActivity.class);

                    intent.putExtra("id", loginedUser.getId().toString());
                    intent.putExtra("pw", loginedUser.getPw().toString());
                    intent.putExtra("name", loginedUser.getName().toString());
                    intent.putExtra("email", loginedUser.getEmail().toString());
                    intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                    intent.putExtra("bookMarks", loginedUser.getBookMarks());

                    intent.putExtra("collection", loginedUser.getBookMarks()[i]);
                    startActivity(intent);
                    finish();
                }
                else if(loginedUser.getBookMarks()[i].equals("예약 확인")) {
                    Intent intent = new Intent(getApplicationContext(), ReservationCheckActivity.class);

                    intent.putExtra("id", loginedUser.getId().toString());
                    intent.putExtra("pw", loginedUser.getPw().toString());
                    intent.putExtra("name", loginedUser.getName().toString());
                    intent.putExtra("email", loginedUser.getEmail().toString());
                    intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                    intent.putExtra("bookMarks", loginedUser.getBookMarks());

                    intent.putExtra("collection", "열람실 예약");
                    startActivity(intent);
                    finish();
                }
                else if(loginedUser.getBookMarks()[i].equals("휴식")) {
                    Intent intent = new Intent(getApplicationContext(), RestActivity.class);

                    intent.putExtra("id", loginedUser.getId().toString());
                    intent.putExtra("pw", loginedUser.getPw().toString());
                    intent.putExtra("name", loginedUser.getName().toString());
                    intent.putExtra("email", loginedUser.getEmail().toString());
                    intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                    intent.putExtra("bookMarks", loginedUser.getBookMarks());

                    intent.putExtra("collection", loginedUser.getBookMarks()[i]);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_home);


        Intent intent = getIntent();

        // 각종 화면에서 받아온 정보를 바탕으로 유저 객체를 생성합니다.
        loginedUser = new User();

        loginedUser.setId(intent.getExtras().getString("id").toString());
        loginedUser.setPw(intent.getExtras().getString("pw").toString());
        loginedUser.setName(intent.getExtras().getString("name").toString());
        loginedUser.setEmail(intent.getExtras().getString("email").toString());
        loginedUser.setDocumentId(intent.getExtras().getString("documentId").toString());
        loginedUser.setBookMarks(intent.getExtras().getStringArray("bookMarks"));


        // 이미지 버튼.. 북마크 배열 준비
        ImageButton bookmark1 = findViewById(R.id.bookmark1);
        ImageButton bookmark2 = findViewById(R.id.bookmark2);
        ImageButton bookmark3 = findViewById(R.id.bookmark3);
        ImageButton bookmark4 = findViewById(R.id.bookmark4);

        mBookMark = new ArrayList<>();

        mBookMark.add(bookmark1);
        mBookMark.add(bookmark2);
        mBookMark.add(bookmark3);
        mBookMark.add(bookmark4);


        // 화면 상단 타이틀
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        mTitle = findViewById(R.id.activity_title);
        mTitle.setText("열람실 예약 홈화면");
        findViewById(R.id.bookmark).setVisibility(View.GONE);

        // 로그인 화면 또는 각종 화면으로부터 받아온 유저의 정보에서 북마크 정보를 바탕으로 이미지를 셋팅합니다.
        for(int i = 0; i < 4; i++) {
            mBookMark.get(i).setImageResource(imgReturn.getR_drawable_source(loginedUser.getBookMarks()[i]));
            // 북마크 정보를 바탕으로 화면이동을 셋팅합니다.
            setBookmarkBtn(mBookMark, i);
        }

    }

    // 아직 만들지 않은 예약에 관련된 기능입니다. 팝업창만 띄우게 만들었습니다.
    public void clickA(View button){
        PopupMenu popup = new PopupMenu(getApplicationContext(), button);
        popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(),  item.getTitle(), Toast.LENGTH_SHORT).show();

                if(item.getItemId() == R.id.reservation) {
                    Intent intent = new Intent(getApplicationContext(), ReservationActivity.class);

                    intent.putExtra("id", loginedUser.getId().toString());
                    intent.putExtra("pw", loginedUser.getPw().toString());
                    intent.putExtra("name", loginedUser.getName().toString());
                    intent.putExtra("email", loginedUser.getEmail().toString());
                    intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                    intent.putExtra("bookMarks", loginedUser.getBookMarks());

                    intent.putExtra("collection", item.getTitle().toString());
                    startActivity(intent);
                    finish();
                }
                else if(item.getItemId() == R.id.reservation_check) {
                    Intent intent = new Intent(getApplicationContext(), ReservationCheckActivity.class);

                    intent.putExtra("id", loginedUser.getId().toString());
                    intent.putExtra("pw", loginedUser.getPw().toString());
                    intent.putExtra("name", loginedUser.getName().toString());
                    intent.putExtra("email", loginedUser.getEmail().toString());
                    intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                    intent.putExtra("bookMarks", loginedUser.getBookMarks());

                    intent.putExtra("collection", "열람실 예약");
                    startActivity(intent);
                    finish();
                }
                else if(item.getItemId() == R.id.rest) {
                    Intent intent = new Intent(getApplicationContext(), RestActivity.class);

                    intent.putExtra("id", loginedUser.getId().toString());
                    intent.putExtra("pw", loginedUser.getPw().toString());
                    intent.putExtra("name", loginedUser.getName().toString());
                    intent.putExtra("email", loginedUser.getEmail().toString());
                    intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                    intent.putExtra("bookMarks", loginedUser.getBookMarks());

                    intent.putExtra("collection", item.getTitle());
                    startActivity(intent);
                    finish();
                }

                return false;
            }
        });

        popup.show();

    }

    // 게시판으로 화면을 넘기는 메뉴입니다.
    public void clickB(View button){
        PopupMenu popup = new PopupMenu(getApplicationContext(), button);
        popup.getMenuInflater().inflate(R.menu.popup2, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getApplicationContext(), Board.class);

                intent.putExtra("id", loginedUser.getId().toString());
                intent.putExtra("pw", loginedUser.getPw().toString());
                intent.putExtra("name", loginedUser.getName().toString());
                intent.putExtra("email", loginedUser.getEmail().toString());
                intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                intent.putExtra("bookMarks", loginedUser.getBookMarks());

                // 팝업 메뉴 타이틀에 따라 불러올 db를 다르게 조정합니다.
                intent.putExtra("collection", item.getTitle());
                startActivity(intent);
                finish();

                return false;
            }
        });

        popup.show();

    }

    // 부가 기능 부분입니다. 스톱워치
    public void clickC(View button){
        PopupMenu popup = new PopupMenu(getApplicationContext(), button);
        popup.getMenuInflater().inflate(R.menu.popup3, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();

                if(item.getItemId() == R.id.stop_watch) {
                    Intent intent = new Intent(getApplicationContext(), StopWatch.class);

                    intent.putExtra("id", loginedUser.getId().toString());
                    intent.putExtra("pw", loginedUser.getPw().toString());
                    intent.putExtra("name", loginedUser.getName().toString());
                    intent.putExtra("email", loginedUser.getEmail().toString());
                    intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                    intent.putExtra("bookMarks", loginedUser.getBookMarks());

                    intent.putExtra("collection", item.getTitle().toString());
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });

        popup.show();

    }

}
