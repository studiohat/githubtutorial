package com.example.reservationproject;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class FindMain extends AppCompatActivity {
    TextView textId, textPw;
    EditText idEdit, pwEdit1, pwEdit2;
    Button idBtn, pwBtn, home1;

    private TextView mTitle;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private List<User> mUserList;
    private User loginedUser;

    // 뒤로가기키를 누를시 로그인 화면으로 돌아갑니다.
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
        setContentView(R.layout.find_main);

        textId = (TextView)findViewById(R.id.findId);
        textPw = (TextView)findViewById(R.id.findpw);
        idEdit = (EditText)findViewById(R.id.emailInput1);
        pwEdit1 = (EditText)findViewById(R.id.emailInput2);
        pwEdit2 = (EditText)findViewById(R.id.idInput);
        idBtn = (Button)findViewById(R.id.IDsearch);
        pwBtn = (Button)findViewById(R.id.Passwordsearch);
        home1 = (Button)findViewById(R.id.home1);


        // 화면 상단의 타이틀입니다.
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        mTitle = findViewById(R.id.activity_title);
        mTitle.setText("아이디/비밀번호 찾기");
        findViewById(R.id.bookmark).setVisibility(View.GONE);

        loginedUser = new User();
        mUserList = new ArrayList<>();

        // 유저 db의 정보를 끌어옵니다.
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

        // 아이디 확인. 이메일 정보를 바탕으로 아이디를 찾습니다.
        idBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailId = idEdit.getText().toString();

                // 해당 이메일과 동일하는 정보가 db에 있는지 확인합니다.
                for(int i = 0; i < mUserList.size(); i++) {
                    if(mUserList.get(i).getEmail().equals(emailId)) {
                        loginedUser = mUserList.get(i);
                    }
                }

                // 이메일을 적지 않았을 경우
                if(emailId.equals("")){
                    Toast.makeText(getApplicationContext(), "email을 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }

                // 해당하는 이메일이 없을 경우
                if (loginedUser.getEmail() == null) {
                    Toast.makeText(FindMain.this, "email을 확인후 다시 입력해 주세요", Toast.LENGTH_SHORT).show();
                }

                // 해당이메일이 있을 경우 매칭된 아이디를 출력합니다.
                else {
                    String id2 = loginedUser.getId();
                    textId.setText(id2);
                }
            }
        });

        // 비밀번호 찾기. 이메일과 아이디를 정보로 찾습니다.
        pwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = pwEdit1.getText().toString();
                String ID =  pwEdit2.getText().toString();

                // 이메일이나 아이디를 입력하지 않았을 경우
                if(email.equals("") || ID.equals("")){
                    Toast.makeText(getApplicationContext(), "email 또는 ID를 입력하세요", Toast.LENGTH_LONG).show();
                    return;
                }

                // 이메일과 아이디를 동시에 만족하는 문서를 찾습니다.
                for(int i = 0; i < mUserList.size(); i++) {
                    if(mUserList.get(i).getEmail().equals(email) && mUserList.get(i).getId().equals(ID)) {
                        loginedUser = mUserList.get(i);
                    }
                }

                // 이메일이나 아이디를 찾을 수 없을 경우
                if (loginedUser.getId() == null || loginedUser.getEmail() == null) {
                    Toast.makeText(FindMain.this, "email 또는 ID를 확인후 다시 입력해 주세요", Toast.LENGTH_SHORT).show();
                }

                // 찾았을 경우 매칭된 비밀번호를 출력합니다.
                else {
                    String pw1 = loginedUser.getPw();
                    textPw.setText(pw1);
                }
            }
        });

        // 홈으로 가기 버튼입니다. 로그인화면으로 이동합니다. 없어도 될거같습니다.
        home1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, 0);
                finish();
            }
        });
    }
}
