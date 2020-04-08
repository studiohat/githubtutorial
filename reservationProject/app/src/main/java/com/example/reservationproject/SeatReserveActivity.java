package com.example.reservationproject;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 좌석 예약하기 화면입니다.
public class SeatReserveActivity extends AppCompatActivity {

    private User loginedUser;
    private String collection;
    private TextView mTitle;
    private ImageButton bookmark;
    private int seat;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private List<SeatModel> mSeatList;
    private SeatModel reservingSeat;
    private Button reserveBtn;
    private Spinner mReserveTime;



    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time;

    // 뒤로가기키를 누를시 열람실 예약화면으로 이동합니다.
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ReservationActivity.class);

        intent.putExtra("id", loginedUser.getId().toString());
        intent.putExtra("pw", loginedUser.getPw().toString());
        intent.putExtra("name", loginedUser.getName().toString());
        intent.putExtra("email", loginedUser.getEmail().toString());
        intent.putExtra("documentId", loginedUser.getDocumentId().toString());
        intent.putExtra("bookMarks", loginedUser.getBookMarks());
        intent.putExtra("collection", collection);

        startActivity(intent);
        finish();

        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_reserve);

        // 데이터를 받아옵니다.
        Intent intent = getIntent();
        loginedUser = new User();
        loginedUser.setId(intent.getExtras().getString("id").toString());
        loginedUser.setPw(intent.getExtras().getString("pw").toString());
        loginedUser.setName(intent.getExtras().getString("name").toString());
        loginedUser.setEmail(intent.getExtras().getString("email").toString());
        loginedUser.setDocumentId(intent.getExtras().getString("documentId").toString());
        loginedUser.setBookMarks(intent.getExtras().getStringArray("bookMarks"));
        collection = intent.getExtras().getString("collection");
        seat = intent.getExtras().getInt("seat");

        // 타이틀 셋팅
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        mTitle = findViewById(R.id.activity_title);
        bookmark = findViewById(R.id.bookmark);
        bookmark.setVisibility(View.GONE);

        // 예약 하기 버튼
        reserveBtn = findViewById(R.id.reserve);

        // 드랍 다운 메뉴
        mReserveTime = findViewById(R.id.reserve_time);
        String[] items = new String[] { "3", "5", "7"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        mReserveTime.setAdapter(adapter);


        // db에서 자리 데이터를 가져옵니다.
        mSeatList = new ArrayList<>();
        reservingSeat = new SeatModel();

        mStore.collection(collection)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
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

                        // 현재 예약하고자 하는 자리와 매칭시킵니다. 100을 더해준 이유는
                        // 앞의 열람실 예약 화면에서 문자열로 사전식으로 정렬하게 되는데. 5와 40을 비교하면 5가 뒤로가게 되기 때문에 101 ~ 168 로
                        // 정렬하기 위하여 100을 더해준 것입니다.
                        for(int i = 1; i <= 68; i++) {
                            if(mSeatList.get(i-1).getSeat().equals(seat + 100 + "")) {
                                reservingSeat = mSeatList.get(i-1);
                            }
                        }
                        // 예약하기 버튼
                        reserveOnClick(reserveBtn, collection, reservingSeat, loginedUser);
                    }
                });




/*
// 데이터베이스 생성할 때 사용한 코드
// 만약에 예약 데이터가 꼬였다면 firebase 계정 홈페이지에 들어가서 컬렉션..
// 열람실 예약 컬렉션을 삭제하시고 다시 이 주석으로 해놓은 부분을 다른 화면을 사용하여 실행하던지
// 이 화면에서 나머지 부분을 주석으로 하고 실행하던지 하시면 됩니다.



        for(int i = 1; i <= 68; i++) {
            Map<String, Object> post = new HashMap<>();
            post.put("seat", 100 + i + "");
            post.put("documentId", 100 + i+ "");
            post.put("user", "");
            post.put("reserve", "x");
            post.put("rest", "");
            post.put("reserve_time", "");
            post.put("now_time", "");

            mStore.collection(collection).document(100 + i + "").set(post)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // 성공했다면 열람실 예약화면으로 이동합니다.
                            Toast.makeText(SeatReserveActivity.this, " 업로드 성공!", Toast.LENGTH_SHORT).show();
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 실패했다면 대기
                            Toast.makeText(SeatReserveActivity.this, "업로드 실패!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        intent = new Intent(getApplicationContext(), ReservationActivity.class);

        intent.putExtra("id", loginedUser.getId().toString());
        intent.putExtra("pw", loginedUser.getPw().toString());
        intent.putExtra("name", loginedUser.getName().toString());
        intent.putExtra("email", loginedUser.getEmail().toString());
        intent.putExtra("documentId", loginedUser.getDocumentId().toString());
        intent.putExtra("bookMarks", loginedUser.getBookMarks());
        intent.putExtra("collection", collection);

        startActivity(intent);

        finish();
*/
    }

    public void reserveOnClick(Button reserveBtn, final String collection, final SeatModel reservingSeat, final User loginedUser) {
        reserveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 예약 정보 변경
                mStore.collection(collection).document(reservingSeat.getDocumentId()).update("reserve","o");
                reservingSeat.setReserve("o");
                mStore.collection(collection).document(reservingSeat.getDocumentId()).update("user",loginedUser.getId());
                reservingSeat.setUser(loginedUser.getId());
                mStore.collection(collection).document(reservingSeat.getDocumentId()).update("reserve_time", mReserveTime.getSelectedItem().toString());
                reservingSeat.setReserve_time(mReserveTime.getSelectedItem().toString());
                time = mFormat.format(new Date());
                mStore.collection(collection).document(reservingSeat.getDocumentId()).update("now_time", time);
                reservingSeat.setNow_time(time);
                mStore.collection(collection).document(reservingSeat.getDocumentId()).update("rest","");
                reservingSeat.setRest("x");


                // 열람실 예약 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), ReservationActivity.class);

                intent.putExtra("id", loginedUser.getId().toString());
                intent.putExtra("pw", loginedUser.getPw().toString());
                intent.putExtra("name", loginedUser.getName().toString());
                intent.putExtra("email", loginedUser.getEmail().toString());
                intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                intent.putExtra("bookMarks", loginedUser.getBookMarks());
                intent.putExtra("collection", collection);

                startActivity(intent);
                finish();
            }
        });
    }

}
