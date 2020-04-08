package com.example.reservationproject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

// 예약 확인 화면입니다.
public class ReservationCheckActivity extends AppCompatActivity {

    private User loginedUser;
    private String collection;
    private TextView mTitle;
    private ImageButton bookmark;
    private BookMark mBookmark = new BookMark();

    private SeatModel mSeatModel;
    private List<SeatModel> mSeatList;
    private boolean isReserved = false;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private TextView mSeat;
    private TextView mArriveTime;
    private TextView mReserveTime;
    private TextView mRestTime;
    private Button mArrive;
    private Button mCancel;


    private SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String time;

    private AccumulatedTimeModel mTime;
    private List<AccumulatedTimeModel> mTimeList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_check);

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
        mTitle.setText("예약 확인");
        bookmark = findViewById(R.id.bookmark);

        // 유저의 정보, 게시판 이름, 북마크 이미지 버튼의 정보를 받아서 현재 북마크가 되어있는지를 표시합니다.
        mBookmark.bookmarkTest(loginedUser, "예약 확인", bookmark);

        // 마찬가지로 정보를 받아서 북마크를 on/off 할 수 있습니다. 또한 비어있는 공간이 있다면 좌측정렬합니다.
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBookmark.updateBookmark(loginedUser, "예약 확인", bookmark);
            }
        });

        // 텍스트뷰와 버튼을 연결.
        mSeat = findViewById(R.id.reservation_check_seat);
        mArriveTime = findViewById(R.id.reservation_check_arrive_time);
        mReserveTime = findViewById(R.id.reservation_check_reserve_time);
        mRestTime = findViewById(R.id.reservation_check_rest_time);
        mArrive = findViewById(R.id.reservation_check_arrive);
        mCancel = findViewById(R.id.reservation_check_exit);

        // 어떤 사용자던 열람실 창을 열었을 때
        // 자리 도착한 시간 + 예약 시간 < 현재 시간 이라면
        // 휴식의 종료를 확인하고 사용을 종료시키고 누적시간에 추가하고 삭제한다.


        // 자리의 예약을 확인
        mSeatModel = new SeatModel();
        mSeatList = new ArrayList<>();

        mStore.collection(collection)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            SeatModel data = new SeatModel(documentId, reserve, seat, user, reserve_time, now_time, start_time,rest);

                            mSeatList.add(data);

                            // 좌석번호를 기준으로 정렬
                            Collections.sort(mSeatList);
                        }

                        // 예약이 되어있고, 현재 사용자와 일치하면 불러옵니다.
                        for (int i = 1; i <= 68; i++) {
                            if (( mSeatList.get(i-1).getReserve().equals("o") || mSeatList.get(i-1).getReserve().equals("u") )&& mSeatList.get(i-1).getUser().equals(loginedUser.getId())) {
                                mySeatNum = Integer.parseInt(mSeatList.get(i-1).getSeat()) - 100;
                                mSeat.setText("좌석 번호 : " + mySeatNum);
                                try {
                                    mArriveTime.setText("도착 예정 시각 : " + TimeCalculator.timeAddMin(mSeatList.get(i-1).getNow_time(), 30));
                                    mReserveTime.setText("예약 종료 시간 : " + TimeCalculator.timeAddHour(mSeatList.get(i-1).getStart_time(),Integer.parseInt(mSeatList.get(i-1).getReserve_time())) );
                                } catch (Exception a) {
                                    a.printStackTrace();
                                }

                                mRestTime.setText("휴식 : " + mSeatList.get(i-1).getRest());

                                makeArriveButton(mArrive, i);
                                makeCancelButton(mCancel, i);
                            }
                        }
                    }
                });
    }

    public void makeCancelButton(Button mButton, final int i) {
        if (mSeatList.get(i - 1).getReserve().equals("u")) {
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    // 예약 초기화
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("reserve", "x");
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("user", "");
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("reserve_time", "");
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("now_time", "");
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("start_time", "");
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("rest", "");



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
        else {
            // 자리 도착 하지 않았을 경우 텍스트 변경
            mCancel.setText("예약 취소");
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 예약 초기화
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("reserve", "x");
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("user", "");
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("reserve_time", "");
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("now_time", "");
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("start_time", "");
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("rest", "");
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
    }

    public void makeArriveButton(Button mArrive, final int i) {
        if (!(mSeatList.get(i - 1).getReserve().equals("u"))) {
            mArrive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 자리 도착 버튼을 누를시 사용중인 상태로 변경하고, 사용중일 경우

                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("reserve", "u");
                    time = mFormat.format(new Date());
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("start_time", time);
                    mSeatList.get(i - 1).setStart_time(time);
                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("rest", "x");
                    mStore.collection(collection)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
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

                                        // 좌석번호를 기준으로 정렬
                                        Collections.sort(mSeatList);
                                    }

                                    // 예약이 되어있고, 현재 사용자와 일치하면 불러옵니다.
                                    for (int i = 1; i <= 68; i++) {
                                        if ((mSeatList.get(i - 1).getReserve().equals("o") || mSeatList.get(i - 1).getReserve().equals("u")) && mSeatList.get(i - 1).getUser().equals(loginedUser.getId())) {
                                            mSeat.setText("좌석 번호 : " + (Integer.parseInt(mSeatList.get(i - 1).getSeat()) - 100));
                                            try {
                                                mArriveTime.setText("도착 예정 시각 : " + TimeCalculator.timeAddMin(mSeatList.get(i - 1).getNow_time(), 30));
                                                mReserveTime.setText("예약 종료 시간 : " + TimeCalculator.timeAddHour(mSeatList.get(i - 1).getStart_time(), Integer.parseInt(mSeatList.get(i - 1).getReserve_time())));
                                            } catch (ParseException a) {
                                                a.printStackTrace();
                                            }
                                            mRestTime.setText("휴식 :" + mSeatList.get(i - 1).getRest());

                                        }


                                    }
                                }
                            });

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
        else {
            // 이미 한번 눌렀을 경우 안보이게
            mArrive.setVisibility(View.GONE);
        }
    }


}
