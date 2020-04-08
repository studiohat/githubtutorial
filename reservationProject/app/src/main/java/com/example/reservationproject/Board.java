package com.example.reservationproject;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

// 계정 메인 화면에서 테이블 정보를 받아 그 정보를 바탕으로 게시판 화면을 구성합니다.
public class Board extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private TextView mTitle;

    private RecyclerView mMainRecyclerview;

    private MainAdapter mAdapter;
    private List<BoardModel> mBoardList;

    public static String collection;

    private User loginedUser;
    private ImageButton bookmark;

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
        setContentView(R.layout.activity_board);

        // 테이블의 정보와 유저의 정보를 받아 구성합니다.
        Intent intent = getIntent();
        collection = intent.getExtras().getString("collection");


        loginedUser = new User();

        loginedUser.setId(intent.getExtras().getString("id").toString());
        loginedUser.setPw(intent.getExtras().getString("pw").toString());
        loginedUser.setName(intent.getExtras().getString("name").toString());
        loginedUser.setEmail(intent.getExtras().getString("email").toString());
        loginedUser.setDocumentId(intent.getExtras().getString("documentId").toString());
        loginedUser.setBookMarks(intent.getExtras().getStringArray("bookMarks"));

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        mTitle = findViewById(R.id.activity_title);
        mTitle.setText(collection);
        bookmark = findViewById(R.id.bookmark);

        // 유저의 정보, 게시판 이름, 북마크 이미지 버튼의 정보를 받아서 현재 북마크가 되어있는지를 표시합니다.
        BookMark boardBookmark = new BookMark();
        boardBookmark.bookmarkTest(loginedUser, collection, bookmark);


        // 마찬가지로 정보를 받아서 북마크를 on/off 할 수 있습니다. 또한 비어있는 공간이 있다면 좌측정렬합니다.
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookMark boardBookmark = new BookMark();

                boardBookmark.updateBookmark(loginedUser, collection, bookmark);
            }
        });

        // 리사이클러 뷰를 통해 게시글들을 구성합니다.
        mMainRecyclerview = findViewById(R.id.main_recycler_view);

        findViewById(R.id.main_write_button).setOnClickListener(this);

        mBoardList = new ArrayList<>();

        // 게시판 이름을 받아 db에서 읽어옵니다.
        mStore.collection(collection)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            String id = (String) dc.getDocument().getData().get("id");
                            String title = (String) dc.getDocument().getData().get("title");
                            String contents = (String) dc.getDocument().get("contents");
                            String name = (String) dc.getDocument().getData().get("name");
                            String time = (String) dc.getDocument().getData().get("time");
                            BoardModel data = new BoardModel(id, title, contents, name, time);

                            mBoardList.add(data);
                        }

                        // 시간순으로 정렬합니다.
                        Collections.sort(mBoardList);

                        // 정렬한 정보를 바탕으로 어뎁터에 추가하고 리사이클러 뷰를 어뎁터에 연결합니다.
                        mAdapter = new MainAdapter(mBoardList);
                        mMainRecyclerview.setAdapter(mAdapter);
                    }
                });

    }

    // 화면 우측 하단의 글작성 버튼입니다.
    @Override
    public void onClick(View view) {
        // 글작성 화면으로 넘어갑니다.
        Intent intent = new Intent(this, WriteActivity.class);

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

    // 어뎁터를 구성하는 부분입니다.
    private static class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

        private List<BoardModel> mBoardList;

        // 배열로 정보를 받아 어뎁터를 구성합니다.
        public MainAdapter(List<BoardModel> mBoardList) {
            this.mBoardList = mBoardList;
        }

        // item_main.xml을 하나 하나의 게시글로 뷰홀더로 만듭니다.
        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MainViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false));
        }

        // 뷰홀더의 내부 셋팅입니다.
        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, final int position) {
            // 몇번째에 위치한 뷰홀더인가에 따라 다르게 셋팅합니다.
            BoardModel data = mBoardList.get(position);
            holder.mTitleTextView.setText(data.getTitle());
            holder.mNameTextView.setText(data.getName());
            holder.mTimeTextView.setText(data.getTime());

            // 뷰홀더. 즉 게시글을 클릭하게 되었을 때
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 해당하는 정보를 바탕으로 게시글을 보여주는 화면으로 이동합니다.
                    Context context = v.getContext();
                    Toast.makeText(context, position + "", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent (context, ShowActivity.class);

                    intent.putExtra("title", mBoardList.get(position).getTitle().toString());
                    intent.putExtra("contents", mBoardList.get(position).getContents().toString());
                    intent.putExtra("name", mBoardList.get(position).getName().toString());
                    intent.putExtra("time", mBoardList.get(position).getTime().toString());

                    context.startActivity(intent);
                }
            });
        }

        // 아이템의 갯수를 정해주는 부분인데요. 받아들인 배열의 사이즈만큼.. 즉 무한대로 늘릴 수 있습니다.
        @Override
        public int getItemCount() {
            return mBoardList.size();
        }

        // 뷰홀더 관련 셋팅입니다.
        class MainViewHolder extends RecyclerView.ViewHolder {

            private TextView mTitleTextView;
            private TextView mNameTextView;
            private TextView mTimeTextView;
            public final View mView;

            public MainViewHolder(View itemview) {
                super(itemview);

                // 어디의 정보를 건드릴 것인지. 아이디를 바탕으로 지정합니다.
                mTitleTextView = itemview.findViewById(R.id.item_title_text);
                mNameTextView = itemview.findViewById(R.id.item_name_text);
                mTimeTextView = itemview.findViewById(R.id.item_time_text);
                mView = itemview;
            }
        }
    }
}
