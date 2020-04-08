package com.example.reservationproject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

// 열람실 예약 화면입니다.
public class ReservationActivity extends AppCompatActivity {

    private User loginedUser;
    private String collection;
    private TextView mTitle;
    private ImageButton bookmark;
    private BookMark mBookmark = new BookMark();

    private SeatModel mSeatModel;
    private List<SeatModel> mSeatList;
    private boolean isReserved = false;
    private boolean isUserReserved = false;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private boolean isToasted = false;
    private boolean isResting = false;

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

    // 버튼을 설정하고, onClick 이벤트를 만들어주는 메서드
    // API레벨이 21이상이어야 합니다.
    public void setButtonOnClick(final int id, boolean isReserved, boolean isResting, boolean isUserReserved) {
        Button btn = findViewById(stringToId(id));
        if(isResting) {
            btn.setBackgroundTintList(this.getResources().getColorStateList(R.color.color_list_3));
        }
        else if(isReserved) {
            btn.setBackgroundTintList(this.getResources().getColorStateList(R.color.color_list_1));
        }
        else if(isUserReserved) {
            if(!isToasted) {
                Toast.makeText(this, "이미 예약을 하셔서 더 이상 예약하실 수 없습니다.", Toast.LENGTH_SHORT).show();
                isToasted = true;
            }
        }
        else {
            btn.setBackgroundTintList(this.getResources().getColorStateList(R.color.color_list_2));

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SeatReserveActivity.class);

                    intent.putExtra("id", loginedUser.getId().toString());
                    intent.putExtra("pw", loginedUser.getPw().toString());
                    intent.putExtra("name", loginedUser.getName().toString());
                    intent.putExtra("email", loginedUser.getEmail().toString());
                    intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                    intent.putExtra("bookMarks", loginedUser.getBookMarks());
                    intent.putExtra("collection", collection);
                    intent.putExtra("seat", id);

                    startActivity(intent);
                    finish();
                }

            });
        }
    }

    // index를 주면 그에 해당하는 아이디를 반환하는 메서드
    public int stringToId(int id) {
        int R_id = 0;

        switch(id) {
            case 1: R_id = R.id.reservation_1; break;
            case 2: R_id = R.id.reservation_2; break;
            case 3: R_id = R.id.reservation_3; break;
            case 4: R_id = R.id.reservation_4; break;
            case 5: R_id = R.id.reservation_5; break;
            case 6: R_id = R.id.reservation_6; break;
            case 7: R_id = R.id.reservation_7; break;
            case 8: R_id = R.id.reservation_8; break;
            case 9: R_id = R.id.reservation_9; break;
            case 10: R_id = R.id.reservation_10; break;
            case 11: R_id = R.id.reservation_11; break;
            case 12: R_id = R.id.reservation_12; break;
            case 13: R_id = R.id.reservation_13; break;
            case 14: R_id = R.id.reservation_14; break;
            case 15: R_id = R.id.reservation_15; break;
            case 16: R_id = R.id.reservation_16; break;
            case 17: R_id = R.id.reservation_17; break;
            case 18: R_id = R.id.reservation_18; break;
            case 19: R_id = R.id.reservation_19; break;
            case 20: R_id = R.id.reservation_20; break;
            case 21: R_id = R.id.reservation_21; break;
            case 22: R_id = R.id.reservation_22; break;
            case 23: R_id = R.id.reservation_23; break;
            case 24: R_id = R.id.reservation_24; break;
            case 25: R_id = R.id.reservation_25; break;
            case 26: R_id = R.id.reservation_26; break;
            case 27: R_id = R.id.reservation_27; break;
            case 28: R_id = R.id.reservation_28; break;
            case 29: R_id = R.id.reservation_29; break;
            case 30: R_id = R.id.reservation_30; break;
            case 31: R_id = R.id.reservation_31; break;
            case 32: R_id = R.id.reservation_32; break;
            case 33: R_id = R.id.reservation_33; break;
            case 34: R_id = R.id.reservation_34; break;
            case 35: R_id = R.id.reservation_35; break;
            case 36: R_id = R.id.reservation_36; break;
            case 37: R_id = R.id.reservation_37; break;
            case 38: R_id = R.id.reservation_38; break;
            case 39: R_id = R.id.reservation_39; break;
            case 40: R_id = R.id.reservation_40; break;
            case 41: R_id = R.id.reservation_41; break;
            case 42: R_id = R.id.reservation_42; break;
            case 43: R_id = R.id.reservation_43; break;
            case 44: R_id = R.id.reservation_44; break;
            case 45: R_id = R.id.reservation_45; break;
            case 46: R_id = R.id.reservation_46; break;
            case 47: R_id = R.id.reservation_47; break;
            case 48: R_id = R.id.reservation_48; break;
            case 49: R_id = R.id.reservation_49; break;
            case 50: R_id = R.id.reservation_50; break;
            case 51: R_id = R.id.reservation_51; break;
            case 52: R_id = R.id.reservation_52; break;
            case 53: R_id = R.id.reservation_53; break;
            case 54: R_id = R.id.reservation_54; break;
            case 55: R_id = R.id.reservation_55; break;
            case 56: R_id = R.id.reservation_56; break;
            case 57: R_id = R.id.reservation_57; break;
            case 58: R_id = R.id.reservation_58; break;
            case 59: R_id = R.id.reservation_59; break;
            case 60: R_id = R.id.reservation_60; break;
            case 61: R_id = R.id.reservation_61; break;
            case 62: R_id = R.id.reservation_62; break;
            case 63: R_id = R.id.reservation_63; break;
            case 64: R_id = R.id.reservation_64; break;
            case 65: R_id = R.id.reservation_65; break;
            case 66: R_id = R.id.reservation_66; break;
            case 67: R_id = R.id.reservation_67; break;
            case 68: R_id = R.id.reservation_68; break;
        }
        return R_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

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
                            SeatModel data = new SeatModel(documentId, reserve, seat, user, reserve_time, now_time, start_time, rest);

                            mSeatList.add(data);

                            // 좌석번호를 기준으로 정렬
                            Collections.sort(mSeatList);
                        }

                        // 유저당 한 자리밖에 예약할 수 없다.
                        // 그러므로 버튼을 비활성화 시키자.
                        for(int i = 1; i<= 68; i++) {
                            if(mSeatList.get(i-1).getUser().equals(loginedUser.getId())) {
                                isUserReserved = true;

                            }
                        }

                        // 버튼 연결
                        for (int i = 1; i <= 68; i++) {

                            if (mSeatList.get(i-1).getReserve().equals("o") || mSeatList.get(i-1).getReserve().equals("u")) {
                                isReserved = true;
                            }
                            if(mSeatList.get(i-1).getRest().equals("o")) {
                                isResting = true;
                            }
                            setButtonOnClick(i, isReserved, isResting, isUserReserved);
                            isReserved = false;
                            isResting = false;
                        }
                    }
                });
    }
}
