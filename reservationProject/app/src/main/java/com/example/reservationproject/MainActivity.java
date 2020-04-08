package com.example.reservationproject;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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


// 로그인을 담당하는 첫 화면입니다.
public class MainActivity extends AppCompatActivity {

    String idL, pwL;
    EditText ide, pwe;
    TextView t, f;
    Button login;
    CheckBox idck, pwck;
    SharedPreferences appData;

    private boolean saveIDData, saveLOGINData;
    private TextView mTitle;
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private List<User> mUserList;
    private User loginedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        loadID();
        loadLogin();


        ide = (EditText) findViewById(R.id.id);
        pwe = (EditText) findViewById(R.id.pw);
        t = (TextView) findViewById(R.id.regist);
        f = (TextView) findViewById(R.id.find);
        login = (Button) findViewById(R.id.login);
        idck = (CheckBox) findViewById(R.id.rememberId);
        pwck = (CheckBox) findViewById(R.id.rememberPw);

        if (saveIDData) {
            ide.setText(idL);
            idck.setChecked(saveIDData);
        }

        if (saveLOGINData) {
            pwe.setText(pwL);
            pwck.setChecked(saveLOGINData);
        }



        // 상단의 타이틀 설정

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_title);

        mTitle = findViewById(R.id.activity_title);
        mTitle.setText("로그인");

        findViewById(R.id.bookmark).setVisibility(View.GONE);


        // firebase로부터 User 테이블의 모든 문서별 필드값을 mUserList라는 배열에 저장합니다.
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



                // 로그인 버튼을 눌렀을 경우 체크할 사항 및 그 과정
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String id1 = ide.getText().toString();
                        String pw1 = pwe.getText().toString();

                        if (id1.length() == 0 || pw1.length() == 0) {
                            //아이디와 비밀번호는 필수 입력사항입니다.
                            Toast toast = Toast.makeText(MainActivity.this, "아이디와 비밀번호는 필수 입력사항입니다.", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }


                        // User 테이블과 일치하는 아이디가 있다면 그 문서에 해당하는 부분을 loginedUser라는 객체와 연결시킵니다.
                        for(int i = 0; i < mUserList.size(); i++) {
                            if(mUserList.get(i).getId().equals(id1)) {
                                loginedUser = mUserList.get(i);
                            }
                        }

                        // loginedUser가 일치하는 아이디가 있었다면 전체 값이 받아와 졌을겁니다.
                        //받아와지지 않았을 경우 null, 또한 비밀번호도 있지 않을 것입니다.
                        // 아이디가 있었다고 해도 비밀번호가 동일하지 않다면 역시나 마찬가지로 로그인에 실패합니다.
                        if(loginedUser.getId() == null || !pw1.equals(loginedUser.getPw())) {
                            Toast.makeText(MainActivity.this, "아이디 또는 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                        }
                        // 그 외의 경우에는 로그인에 성공하게 됩니다.
                        else {
                            saveID();
                            saveLogin();
                            AutoCheck.autoCheck();
                            AutoCheckTime.autoCheckTime();


                            //인텐트 생성 및 호출(계정 메인화면으로 이동)
                            Intent intent = new Intent(getApplicationContext(), AccountHome.class);

                            intent.putExtra("id", loginedUser.getId().toString());
                            intent.putExtra("pw", loginedUser.getPw().toString());
                            intent.putExtra("name", loginedUser.getName().toString());
                            intent.putExtra("email", loginedUser.getEmail().toString());
                            intent.putExtra("documentId", loginedUser.getDocumentId().toString());
                            intent.putExtra("bookMarks", loginedUser.getBookMarks());

                            startActivity(intent);
                        }
                    }
                });
            }
        });

        // 회원가입 하는 창으로 이동하는 부분입니다.
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
                startActivity(intent);
                finish();
            }
        });

        // 아이디/비밀번호 찾기 창으로 이동하는 부분입니다.
        f.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindMain.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void saveID() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();

        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putBoolean("SAVE_ID_DATA", idck.isChecked());
        editor.putString("ID", ide.getText().toString().trim());

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    private void loadID() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveIDData = appData.getBoolean("SAVE_ID_DATA", false);
        idL = appData.getString("ID", "");
    }

    private void saveLogin() {
        // SharedPreferences 객체만으론 저장 불가능 Editor 사용
        SharedPreferences.Editor editor = appData.edit();
        // 에디터객체.put타입( 저장시킬 이름, 저장시킬 값 )
        // 저장시킬 이름이 이미 존재하면 덮어씌움
        editor.putBoolean("SAVE_LOGIN_DATA", pwck.isChecked());
        editor.putString("IDL", ide.getText().toString().trim());
        editor.putString("PWL", pwe.getText().toString().trim());

        // apply, commit 을 안하면 변경된 내용이 저장되지 않음
        editor.apply();
    }

    private void loadLogin() {
        // SharedPreferences 객체.get타입( 저장된 이름, 기본값 )
        // 저장된 이름이 존재하지 않을 시 기본값
        saveLOGINData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        idL = appData.getString("IDL", "");
        pwL = appData.getString("PWL", "");
    }

}

