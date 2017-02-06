package taca.com.recycleviewtest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import taca.com.recycleviewtest.model.User;

public class NewPostActivity extends AppCompatActivity {

    EditText title, content;
    FloatingActionButton fab;


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        title = (EditText) findViewById(R.id.post_title);
        content = (EditText) findViewById(R.id.post_content);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendPost();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void onSendPost(){

        final String title_str = title.getText().toString();
        final String content_str = content.getText().toString();

        // 작성글 입력

        // 제목, 내용이 존재해야함
        if(TextUtils.isEmpty(title_str)){
            title.setError(" 필수 입력 값");
            return ;
        }

        if(TextUtils.isEmpty(content_str)){
            content.setError(" 필수 입력 값");
            return ;
        }
        // 비속어 처리
        // 입력을 못하게 막아야함 (편집불가)
        setEditable(false);
        // 회원이 맞는지 체크

        Log.i("FIREBASE UID : ", FirebaseAuth.getInstance().getCurrentUser().getEmail());

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 1. uid 존재 체크
        databaseReference.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if(user == null){
                    // 2. 없으면 불가
                    Toast.makeText(NewPostActivity.this, "회원이 아니야", Toast.LENGTH_SHORT).show();
                    setEditable(true);
                    finish();

                }else{

                    // 3. 있으면 이후 작업
                    // 로딩 시작
                    // 글 작성 업로드
                    // 입력 잠금 해제
                    setEditable(true);
                    // 로딩 닫기
                    // 화면 닫힘

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public void setEditable(boolean flag){
        title.setEnabled(flag);
        content.setEnabled(flag);

        if(flag){
            fab.setVisibility(View.VISIBLE);
        }else{
            fab.setVisibility(View.GONE);
        }
    }

}
