package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

import Info.ChatInfo;
import adapter1.ChatAdapter;
import kr.ac.yeonsung.ksj.ex1.R;

public class ChatView extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String group_id;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<ChatInfo> arrayList = new ArrayList<>();
    private Button chat_send;
    private EditText edit;
    private String Myname;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerView_chat);
        chat_send = findViewById(R.id.chat_send);
        adapter = new ChatAdapter(arrayList,Myname);
        recyclerView.setAdapter(adapter);
        edit = findViewById(R.id.edit);
        Intent intent = getIntent();
        group_id = intent.getStringExtra("group_id");
        recyclerView.setHasFixedSize(true);
        getName();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DatabaseReference ref = database.getReference("message").child(group_id);
        ref.addChildEventListener(childEventListener);
        chat_send.setOnClickListener(sendListener);

    }

    View.OnClickListener sendListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String text = edit.getText().toString();
            Toast.makeText(ChatView.this, text, Toast.LENGTH_SHORT).show();


            Calendar c = Calendar.getInstance();
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateformat2 = new SimpleDateFormat("HH:mm");
            String date = dateformat.format(c.getTime());
            String date2 = dateformat2.format(c.getTime());
        if(!edit.getText().equals("")){
            DatabaseReference myRef = database.getReference("message").child(group_id).child(date);
            Hashtable<String, String> message = new Hashtable<String, String>();
            message.put("chat_name",Myname);
            message.put("chat_text",text);
            message.put("chat_date",date2);
            message.put("chat_email",user.getEmail());
            myRef.setValue(message);
            edit.setText("");}
        else{
            Toast.makeText(ChatView.this, "메세지를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        }
    };
    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            ChatInfo chatInfo = snapshot.getValue(ChatInfo.class);
            String commentKey = snapshot.getKey();
            arrayList.add(chatInfo);
            recyclerView.scrollToPosition(arrayList.size()-1);
            adapter.notifyDataSetChanged();

        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    public void getName(){
        db.collection("users").document(user.getEmail()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            Myname = document.getData().get("name").toString();
                        }
                    }
                }
        );
    }

}