package taca.com.recycleviewtest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import taca.com.recycleviewtest.model.ChatMessage;

public class ChatActivity extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat);
        //onbasic();

        // 1. 파베디비 객체 획득
        firebaseDatabase = FirebaseDatabase.getInstance();
        // 2. DB를 access 할수있는 참조 root 획득
        databaseReference = firebaseDatabase.getReference();



        // 3. 적절한 경로( 가지를 하나 만들어서 ) 에다 메세지 입력
        databaseReference.child("chat").push().setValue(new ChatMessage("삼다수", "하이"))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("CHAT", "등록완료");
                        } else {
                            Log.i("CHAT", "등록실패");
                        }
                    }
                });
        // 4. slect 등록된 데이터 가져오기
        /*databaseReference.child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //Log.i("CHAT", dataSnapshot.toString() + "/" + s);

                // 데이터 파싱
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);

                Log.i("CHATTING", chatMessage.toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

    }

    public void onbasic(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
