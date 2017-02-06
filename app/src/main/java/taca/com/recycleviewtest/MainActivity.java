package taca.com.recycleviewtest;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import taca.com.recycleviewtest.model.ChatMessage;

public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    AutoCompleteTextView msg_input;

//    int index[] = new int[1000];

    MyAdapter myAdapter = new MyAdapter();
    LinearLayoutManager linearLayoutManager;

    GridLayoutManager gridLayoutManager;

    StaggeredGridLayoutManager staggeredGridLayoutManager;


    ArrayList<ChatMessage> arrayList = new ArrayList<ChatMessage>();

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // 1. 파베디비 객체 획득
        firebaseDatabase = FirebaseDatabase.getInstance();
        // 2. DB를 access 할수있는 참조 root 획득
        databaseReference = firebaseDatabase.getReference();


        // 3. 적절한 경로( 가지를 하나 만들어서 ) 에다 메세지 입력
        /*databaseReference.child("chat").push().setValue(new ChatMessage("삼다수", "하이"))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("CHAT", "등록완료");
                        } else {
                            Log.i("CHAT", "등록실패");
                        }
                    }
                });*/
        // 4. slect 등록된 데이터 가져오기
        databaseReference.child("chat").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //Log.i("CHAT", dataSnapshot.toString() + "/" + s);

                // 데이터 파싱
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                arrayList.add(chatMessage);
                linearLayoutManager.scrollToPosition(arrayList.size() - 1);
                myAdapter.notifyDataSetChanged();
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
        });


        /*
        for(String s:data){
            arrayList.add(s);
        }*/

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        msg_input = (AutoCompleteTextView) findViewById(R.id.msg_input);

        // 자동완성
        msg_input.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, autoKeyword
        ));

        // 뷰의 스타일 (매니저) 를 정의 - 선형, 그리드형, 높이가 불규칙한 그리드형
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setStackFromEnd(true);


        gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);


        // 가변그리드
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, OrientationHelper.VERTICAL);
        //recyclerView.setLayoutManager(staggeredGridLayoutManager);
        //채팅관련 데이터를 뒤집어서  표현할때(최신순), 서버에서 온 데이터가 최신순이라면 필요없음
        // FIREBase 로 데이터를 받아올때는 최신순 처리가 곤란하므로 유용함
        staggeredGridLayoutManager.setReverseLayout(true);
        //데이터가 동적으로 바뀌면 적용이 안됨, 새로 세팅해야함
        // 특정위치로 맞추기
        staggeredGridLayoutManager.scrollToPosition(arrayList.size()-1);


        //recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.setLayoutManager(staggeredGridLayoutManager);
        linearLayoutManager.scrollToPosition(arrayList.size() - 1);

        recyclerView.setAdapter(myAdapter);

    }

    public void onSend(View view) {

        // 1. 입력데이터 추출
        String msg = msg_input.getText().toString();
        // 2. 서버 전송 ( 여기서는 직접 추가 )
        /*ChatMessage temp = new ChatMessage();
        temp.setMessage(msg);
        temp.setUsername("나다임마~");
        arrayList.add(temp);*/

        databaseReference.child("chat").push().setValue(new ChatMessage("삼다수", msg))
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

        // 3. 화면갱신
        //myAdapter.notifyDataSetChanged();

        // 4. 리스트 가장마지막으로 갱신
        //staggeredGridLayoutManager.scrollToPosition(arrayList.size() - 1);


        // 5. 입력값 제거
        msg_input.setText("");

        // 6. 키보드 내리기
        //closeKeyboard(this, msg_input);
    }

    public void closeKeyboard(Context context, EditText editText) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

    }

    class MyAdapter extends RecyclerView.Adapter {

        // 뷰홀더를 생성한다
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // xml -> view
            View itemView = LayoutInflater.from(parent.getContext())//.inflate(R.layout.cell_cardview_layout, parent, false);
                    .inflate(R.layout.sendbird_view_group_user_message, parent, false);
            return new PostHolder(itemView);
        }


        // 뷰홀더에 데이터로 설정(바인딩) 한다.
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ((PostHolder) holder).bindOnPost(arrayList.get(position), 1);
        }

        // 아이템 갯수
        @Override
        public int getItemCount() {
            return arrayList.size();
        }
    }

    String autoKeyword[] =
            {
                    "이상해씨",
                    "이상해풀",
                    "이상해꽃",
                    "파이리",
                    "리자드",
                    "리자몽"
            };
}
