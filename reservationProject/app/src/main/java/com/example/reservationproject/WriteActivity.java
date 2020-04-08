package com.example.reservationproject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


// 글작성 버튼을 클릭했을 때 넘어오는 화면입니다.
public class WriteActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    private EditText mWriteTitleText;
    private EditText mWriteContentsText;

    private String id;

    private static String collection;

    private TextView mTitle;

    private User loginedUser;

    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String time;


    // 뒤로가기 키를 눌렀을 시 게시판 화면으로 넘어갑니다.(글작성 취소시?)
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Board.class);

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
        setContentView(R.layout.activity_write);

        // 넘어온 정보를 바탕으로 화면 구성
        Intent intent = getIntent();
        collection = intent.getExtras().getString("collection");

        mWriteTitleText = findViewById(R.id.write_title_text);
        mWriteContentsText = findViewById(R.id.write_contents_text);

        findViewById(R.id.write_upload_button).setOnClickListener(this);

        // 상단의 타이틀
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        mTitle = findViewById(R.id.activity_title);
        // 어느 게시판의 게시글을 작성하는지
        mTitle.setText(collection + " 게시글 작성");
        findViewById(R.id.bookmark).setVisibility(View.GONE);

        // 받아온 정보를 바탕으로 다시 객체 생성
        loginedUser = new User();

        loginedUser.setId(intent.getExtras().getString("id").toString());
        loginedUser.setPw(intent.getExtras().getString("pw").toString());
        loginedUser.setName(intent.getExtras().getString("name").toString());
        loginedUser.setEmail(intent.getExtras().getString("email").toString());
        loginedUser.setDocumentId(intent.getExtras().getString("documentId").toString());
        loginedUser.setBookMarks(intent.getExtras().getStringArray("bookMarks"));
    }

    @Override
    public void onClick(View view) {
        // 해당 db의 문서 아이디를 하나 획득합니다.
        id = mStore.collection(collection).document().getId();

        // 시간 구하는 방식입니다.
        time = mFormat.format(new Date());

        // 게시글에 들어갈 내용을 구성합니다.
        Map<String, Object> post = new HashMap<>();
        post.put("id",id);
        post.put("title", mWriteTitleText.getText().toString());
        post.put("contents", mWriteContentsText.getText().toString());
        post.put("name", loginedUser.getName());
        post.put("time", time);

        // db에 넣습니다.
        mStore.collection(collection).document(id).set(post)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 성공했다면 게시판 화면으로 이동합니다.
                        Toast.makeText(WriteActivity.this, " 업로드 성공!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), Board.class);

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
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 실패했다면 대기
                        Toast.makeText(WriteActivity.this, "업로드 실패!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
