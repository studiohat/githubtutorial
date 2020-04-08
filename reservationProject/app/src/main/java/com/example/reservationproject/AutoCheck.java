package com.example.reservationproject;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class AutoCheck {
    private static SeatModel mSeatModel;
    private static List<SeatModel> mSeatList;
    private static FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private static String collection;
    private static SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void autoCheck() {
        mSeatModel = new SeatModel();
        mSeatList = new ArrayList<>();
        collection = "열람실 예약";

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
                        for (int i = 1; i <= 68; i++) {
                            try {
                                long now = System.currentTimeMillis();
                                Date date = new Date(now);
                                String date1 = TimeCalculator.timeAddMin(mSeatList.get(i - 1).getNow_time(), 30);
                                Date date2 = mFormat.parse(date1);
                                if (date.after(date2) && !mSeatList.get(i - 1).getReserve().equals("u")) {
                                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("reserve", "x");
                                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("user", "");
                                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("reserve_time", "");
                                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("now_time", "");
                                    mStore.collection(collection).document(mSeatList.get(i - 1).getDocumentId()).update("start_time", "");
                                    break;
                                }
                            } catch (Exception e1) {
                            }
                        }
    }
});

    }
}
