package com.example.reservationproject;

import android.widget.ImageButton;

import com.google.firebase.firestore.FirebaseFirestore;

// 북마크 관련 클래스 입니다.
public class BookMark {
    private int R_drawable_source;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    // 북마크 정보를 받아서 이미지를 리턴하는 메서드
    public int getR_drawable_source(String collection) {

        if(collection.equals("자유 게시판")) this.R_drawable_source = R.drawable.free_board;
        else if(collection.equals("신고 게시판")) this.R_drawable_source = R.drawable.report;
        else if(collection.equals("공지 게시판")) this.R_drawable_source = R.drawable.notice;
        else if(collection.equals("스탑워치")) this.R_drawable_source = R.drawable.stopwatch;
        else if(collection.equals("최대 누적시간")) this.R_drawable_source = R.drawable.accumulated_time_check;
        else if(collection.equals("예약 확인")) this.R_drawable_source = R.drawable.reservation_check;
        else if(collection.equals("열람실 예약")) this.R_drawable_source = R.drawable.reservation;
        else if (collection.equals("휴식")) this.R_drawable_source = R.drawable.rest;
        else this.R_drawable_source = R.drawable.img2;

        return this.R_drawable_source;
    }

    // 북마크를 on/off 하는 기능입니다. 정렬기능까지 포함
    public void updateBookmark(User loginedUser, String collection, ImageButton bookmark) {

        boolean isBookmarked = false;
        int bookmarkNum = 100;
        int bookmarkNull = 100;
        // 북마크 정보 확인
        for ( int i = 3; i >= 0; i--) {
            if ( loginedUser.getBookMarks()[i].equals(collection) && i >= 0 ) {
                bookmark.setImageResource(R.drawable.bookmarked);
                // 현재 북마크가 되어있다면 true
                isBookmarked = true;
                // 몇번째 북마크에 등록되어 있는지
                bookmarkNum = i;
            }

            // 북마크 구성에 비어있는 곳이 있는지
            if(loginedUser.getBookMarks()[i].length() == 0 && i >= 0) {
                bookmarkNull = i;
            }
        }

        // 북마크 되어있다면 북마크 안되게 변경
        if(isBookmarked) {
            // 북마크 정보를 false로 변경
            isBookmarked = false;
            // 이미지 셋팅
            bookmark.setImageResource(R.drawable.bookmark);

            // 북마크가 되어있는 곳의 정보를 삭제
            mStore.collection("User").document(loginedUser.getDocumentId()).update("bookMarks" + bookmarkNum,"");
            loginedUser.setBookMark("",bookmarkNum);
        }
        // 안되어 있고, 즐겨찾기에 빈공간이 있다면
        else {
            if (bookmarkNull != 100) {
                isBookmarked = true;
                bookmark.setImageResource(R.drawable.bookmarked);

                // 비어있는 곳에 북마크 입력
                mStore.collection("User").document(loginedUser.getDocumentId()).update("bookMarks" + bookmarkNull, collection);
                loginedUser.setBookMark(collection, bookmarkNull);
            }
        }
        

        // 그 후 중간에 비어있는 곳이 있다면 좌측으로 당깁니다. 좌측정렬
        for (int i = 0; i < 3; i++) {
            if(loginedUser.getBookMarks()[i].equals("") && !(loginedUser.getBookMarks()[i+1].equals(""))) {
                mStore.collection("User").document(loginedUser.getDocumentId()).update("bookMarks" + i,loginedUser.getBookMarks()[i+1]);
                loginedUser.setBookMark(loginedUser.getBookMarks()[i+1],i);
                mStore.collection("User").document(loginedUser.getDocumentId()).update("bookMarks" + (i+1),"");
                loginedUser.setBookMark("", i+1);
            }
        }
    }

    // 현재 화면에 북마크가 되어있는지 확인하고 이미지를 셋팅하는 기능입니다.
    public void bookmarkTest(User loginedUser, String collection, ImageButton bookmark) {

        for ( int i = 3; i >= 0; i--) {
            if ( loginedUser.getBookMarks()[i].equals(collection) && i >= 0 ) {
                bookmark.setImageResource(R.drawable.bookmarked);
            }
        }
    }
}
