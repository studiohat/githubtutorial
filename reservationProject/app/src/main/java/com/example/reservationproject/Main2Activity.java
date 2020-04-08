package com.example.reservationproject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;


// 회원가입을 담당하는 화면입니다.
public class Main2Activity extends AppCompatActivity {
    Button cancle , check, complete;
    EditText name,id,pw,pw2,email;
    boolean checkok =false;

    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private String documentId;
    private TextView mTitle;
    private List<User> mUserList;
    private User loginedUser;


    // 뒤로가기키를 눌렀을 경우 로그인 화면으로 돌아갑니다.
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(intent);
        finish();

        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        name = (EditText)findViewById(R.id.name);
        id = (EditText)findViewById(R.id.id);
        pw = (EditText)findViewById(R.id.pw);
        pw2 = (EditText)findViewById(R.id.pw2);
        email = (EditText)findViewById(R.id.email);


        // 화면 상단의 타이틀을 집어넣습니다.
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        mTitle = findViewById(R.id.activity_title);
        mTitle.setText("회원 가입");
        findViewById(R.id.bookmark).setVisibility(View.GONE);


        // 마찬가지로 firebase로부터 User 테이블의 정보를 긁어옵니다.
        loginedUser = new User();
        mUserList = new ArrayList<>();

        mStore.collection("User").addSnapshotListener(new EventListener<QuerySnapshot>() {
              @Override
              public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                  for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                      String id = (String) dc.getDocument().getData().get("id");
                      String pw = (String) dc.getDocument().getData().get("pw");
                      String name = (String) dc.getDocument().get("name");
                      String email = (String) dc.getDocument().getData().get("email");
                      String documentId = (String) dc.getDocument().getData().get("documentId");
                      String[] bookMarks = new String[4];
                      bookMarks[0] = (String) dc.getDocument().getData().get("bookMarks0");
                      bookMarks[1] = (String) dc.getDocument().getData().get("bookMarks1");
                      bookMarks[2] = (String) dc.getDocument().getData().get("bookMarks2");
                      bookMarks[3] = (String) dc.getDocument().getData().get("bookMarks3");

                      User user = new User(id, pw, email, name, documentId, bookMarks);

                      mUserList.add(user);
                  }
              }
          });

        // 로그인화면으로 돌아갑니다.
        cancle = (Button) findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // 정보를 입력후 회원가입하는 부분입니다.

        // 비밀번호와 비밀번호 확인란을 통한 조건에 대한 부분입니다.
        check = (Button)findViewById(R.id.check);
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = pw.getText().toString();
                String a2 = pw2.getText().toString();
                if(a.equals("")||a2.equals("")){
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(a.equals(a2)){
                    Toast.makeText(getApplicationContext(), "일치합니다.", Toast.LENGTH_LONG).show();
                    pw.setFocusable(false);
                    pw.setClickable(false);
                    pw2.setFocusable(false);
                    pw2.setClickable(false);
                    checkok = true;
                }else{
                    Toast.makeText(getApplicationContext(), "일치하지 않습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

        // 나머지 부분에 대해 조건을 걸어놓은 부분입니다.
        complete = (Button)findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_name = name.getText().toString();
                String s_id = id.getText().toString();
                String s_email = email.getText().toString();
                String s_pw = pw.getText().toString();

                if(s_name.equals("") || s_id.equals("") || s_email.equals("") || !checkok){
                    Toast.makeText(getApplicationContext(), "이름 ,ID, 비밀번호, Email을 확인하고 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                // User 테이블에 해당하는 정보가 이미 있는지 확인합니다.
                for(int i = 0; i < mUserList.size(); i++) {
                    if(mUserList.get(i).getId().equals(s_id)) {
                        loginedUser = mUserList.get(i);
                    }
                }

                // 없다면 가입이 완료됩니다.
                if(loginedUser.getId() == null) {
                    Toast.makeText(Main2Activity.this, "가입이 완료되었습니다. 로그인을 해주세요.", Toast.LENGTH_SHORT).show();

                    // User 테이블에 해당 정보를 가진 문서를 담습니다.
                    documentId = mStore.collection("User").document().getId();

                    Map<String, Object> post = new HashMap<>();
                    post.put("id",id.getText().toString());
                    post.put("pw", pw.getText().toString());
                    post.put("email", email.getText().toString());
                    post.put("name", name.getText().toString());
                    post.put("documentId", documentId.toString());
                    post.put("bookMarks0", "");
                    post.put("bookMarks1", "");
                    post.put("bookMarks2", "");
                    post.put("bookMarks3", "");

                    // 담은 정보를 db에 업로드 하는 부분입니다.
                    mStore.collection("User").document(documentId).set(post)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // 업로드 성공시 로그인 화면으로 돌아갑니다.
                                    Toast.makeText(Main2Activity.this, " 업로드 성공!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            })

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // 업로드 실패시 대기합니다.
                                    Toast.makeText(Main2Activity.this, "업로드 실패!", Toast.LENGTH_SHORT).show();
                                }
                            });


                } else{
                    // 이미 해당하는 아이디가 있다면 회원가입에 실패합니다.
                    Toast toast = Toast.makeText(Main2Activity.this, "중복된 아이디입니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }
}
