package taca.com.recycleviewtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import taca.com.recycleviewtest.db.StorageHelper;
import taca.com.recycleviewtest.model.User;

public class SignUpActivity extends BaseActivity {

    EditText email_et, password_et;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email_et = (EditText) findViewById(R.id.email_et);
        password_et = (EditText) findViewById(R.id.password_et);
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        String email =
        StorageHelper.getInstance().getString(SignUpActivity.this, "email");

        String pwd =
        StorageHelper.getInstance().getString(SignUpActivity.this, "password");


        if(email != null && pwd != null && !email.equals("") && !pwd.equals("")){

            email_et.setText(email);
            password_et.setText(pwd);
            onLogin(null);

        }

    }

    public void onSignup(View view)
    {
        if(!isValidate()) return;

        // 1. 로딩
        showProgress(" 기다려 봐 ");
        // 2. 이메일 비번 획득
        final String email = email_et.getText().toString();
        final String pwd = password_et.getText().toString();

        // 3. 인증에 데이터 입력

        firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // 4. 성공하면 로딩닫어
                hideProgress();
                if(task.isSuccessful()){
                    Log.i("TASK createuseremail" , "성공");
                    // 4-1. 회원정보를 디비에입력
                    onUserSaved(email);
                    // 5. 로그인 처리로 이동
                }else{
                    // 실패
                    Log.i("TASK createuseremail" , "실패");

                }
            }
        });
    }

    public void goCenter(){

        Intent intent = new Intent(this, CenterActivity.class); startActivity(intent);
    }

    public void onUserSaved(String emailParam) {
        String id = emailParam.split("@")[0];
        final String email = emailParam;
        final String pwd = password_et.getText().toString();

        User user = new User(id, email);

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 디비입력
        databaseReference.child("users").child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Log.i("TASK" , "성공");

                    StorageHelper.getInstance().setString(SignUpActivity.this, "email", email);
                    StorageHelper.getInstance().setString(SignUpActivity.this, "password", pwd);
                    goCenter();
                    finish();
                    // 로그인
                }else {


                    Log.i("TASK" , "실패");
                }

            }
        });
    }

    public void onLogin(View view){

        if(!isValidate()) return;

        // 1. 로딩
        showProgress(" 기다려 봐 ");
        // 2. 이메일 비번 획득
        final String email = email_et.getText().toString();
        String pwd = password_et.getText().toString();

        // 3. 인증에 데이터 입력

        firebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // 4. 성공하면 로딩닫어
                hideProgress();
                if(task.isSuccessful()){
                    Log.i("TASK createuseremail" , "성공");
                    // 4-1. 회원정보를 디비에입력
                    onUserSaved(email);
                    // 5. 로그인 처리로 이동
                }else{
                    // 실패
                    Log.i("TASK createuseremail" , "실패");

                }
            }
        });

    }

    public boolean isValidate(){

        if(TextUtils.isEmpty(email_et.getText().toString())){

            email_et.setError("이메일을 입력하세요");
            return false;
        }else{

            String email = email_et.getText().toString();
//            boolean flag = Pattern.matches("^[a-zA-Z0-9]+@[a-zA-Z0-9]*$", email);

/*
            if(!flag){
                email_et.setError("이메일형식에 맞춰");
                return false;
            }
*/

            email_et.setError(null);
        }

        if(TextUtils.isEmpty(password_et.getText().toString())){

            password_et.setError("비밀번호를 입력하세요");
            return false;
        }else{
            if(password_et.length()<6){
                password_et.setError("6자");
                return false;
            }
            password_et.setError(null);
        }
        return true;
    }
}
