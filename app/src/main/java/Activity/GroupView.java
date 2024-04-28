package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter1.MemberListAdapter;
import Info.Memberlist;
import kr.ac.yeonsung.ksj.ex1.R;

public class GroupView extends AppCompatActivity {
    private ListView listView;

    private List<String> data = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MemberListAdapter memberAdapter;
    private HashMap<String, HashMap<String, Object>> hash = new HashMap<>();
    private HashMap<String, Object> user_map = new HashMap<>();
    private List<Memberlist> member_list = new ArrayList<>();
    private Long people_max;
    private int people_now;
    private TextView memberName;
    ArrayAdapter<String> adapter;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TextView group_name;
    private TextView group_text;
    private TextView reg_date;
    private LinearLayout memberListName;
    private Button group_join;
    private Button group_out;
    private Button group_delete;
    private Button group_update;
    private Button group_enable;
    private Button chat_btn;
    private String group_id;
    private static final String TAG = "GroupView : ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        group_id = intent.getStringExtra("group_id");
        group_enable = findViewById(R.id.group_enable);

        btn();
        btntext();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,data);
        member_list();
        group_name = findViewById(R.id.group_name);
        group_text = findViewById(R.id.group_text);
        reg_date = findViewById(R.id.reg_date);
        chat_btn = findViewById(R.id.chat_btn);
        group_join = findViewById(R.id.group_join);
        group_out = findViewById(R.id.group_out);
        group_delete = findViewById(R.id.group_delete);
        group_update = findViewById(R.id.group_update);
        memberName = findViewById(R.id.memberName);
        listView = findViewById(R.id.listview);
        memberListName = findViewById(R.id.memberListName);
        group_info();

        group_join.setOnClickListener(btnLisener);
        group_out.setOnClickListener(btnLisener);
        group_delete.setOnClickListener(btnLisener);
        group_update.setOnClickListener(btnLisener);
        group_enable.setOnClickListener(btnLisener);
        chat_btn.setOnClickListener(btnLisener);


    }


    View.OnClickListener btnLisener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.group_join:
                    group_join();
                    break;
                case R.id.group_out:
                    group_out();
                    break;
                case R.id.group_delete:
                    group_delete();
                    break;
                case R.id.group_update:
                    group_update();
                    break;
                case R.id.group_enable:
                    group_enable();
                    break;
                case R.id.chat_btn:
                    chat();
                    break;
            }

        }
    };
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void group_join(){

        firebaseFirestore.collection("users")
                .document(user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            user_map = (HashMap<String, Object>) document.getData();
                            System.out.println("user_map 1 "+user_map);
                        }
                    }
                });
        group_join2();

    }
public void group_join2(){
    firebaseFirestore.collection("group").
            document(group_id).
            get().
            addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        people_max =  (Long)document.getData().get("max_people");

                        hash = (HashMap<String, HashMap<String, Object>>) document.getData().get("users");

                        System.out.println(people_max + " 2 " + people_now);
                        if(hash.containsKey(user.getEmail())){
                            Toast.makeText(getApplicationContext(), "이미 가입된 그룹입니다.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(people_max == people_now){
                                Toast.makeText(getApplicationContext(), "최대 인원이 초과되어 신청할 수 없는 그룹입니다", Toast.LENGTH_SHORT).show();
                                group_join.setEnabled(false);
                            }
                            else{
                                hash.put(user.getEmail(), user_map);
                                System.out.println("3 " + hash);
                                firebaseFirestore.collection("group").document(group_id).update("users", hash);
                                System.out.println("4  "+document.getData().get("users"));
                                people_now = hash.size();
                                Toast.makeText(getApplicationContext(), "참가 완료", Toast.LENGTH_SHORT).show();
                                myStartActivity(MainActivity.class);
                                firebaseFirestore.collection("group").document(group_id).update("now_people", Long.valueOf(people_now));
                            }

                        }
                    }
                }
            });
}
    public void group_out(){
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("group").
                document(group_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        final DocumentReference sfDocRef = firebaseFirestore.collection("group").document((String)group_id);
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {

                                //탈퇴버튼 클릭 시 group 컬렉션 -> 각 문서 -> 탈퇴 버튼 누른 회원 삭제
                                hash = (HashMap<String, HashMap<String, Object>>) document.getData().get("users");
                                hash.remove(user.getEmail());
                                people_now = hash.size();
                                firebaseFirestore.collection("group")
                                        .document(group_id)
                                        .update("users", hash);
                                firebaseFirestore.collection("group").document(group_id).update("now_people",Long.valueOf(people_now));

                                Toast.makeText(GroupView.this, "탈퇴 성공", Toast.LENGTH_SHORT).show();
                                 myStartActivity(MainActivity.class);
                            }
                        }
                    }
                });
    }
    public void group_delete(){
        firebaseFirestore.collection("group").document(group_id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GroupView.this, "해체 성공", Toast.LENGTH_SHORT).show();
                    myStartActivity(MainActivity.class);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
    public void group_info(){
        firebaseFirestore.collection("group")
                .document(group_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documnet = task.getResult();
                        group_name.setText((String)documnet.get("group_name"));
                        group_text.setText((String)documnet.get("group_text"));
                        reg_date.setText((String)documnet.get("reg_date"));
                    }
                });
    }
    public void btn(){
        firebaseFirestore.collection("group").document(group_id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            hash = (HashMap<String, HashMap<String, Object>>) document.getData().get("users");
                            if(document.getData().get("group_master").equals(user.getEmail())){
                                group_delete.setVisibility(View.VISIBLE);
                                group_update.setVisibility(View.VISIBLE);
                                group_enable.setVisibility(View.VISIBLE);
                                chat_btn.setVisibility(View.VISIBLE);
                            }
                            else if(hash.containsKey(user.getEmail())==false) {
                                group_join.setVisibility(View.VISIBLE);
                            }
                            else {
                                group_out.setVisibility(View.VISIBLE);
                                chat_btn.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }
    public void group_update(){
        firebaseFirestore.collection("group").document(group_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        Intent intent = new Intent(getApplicationContext(), GroupUpdate.class);
                        intent.putExtra("group_id",group_id);
                        intent.putExtra("group_name", document.get("group_name").toString());
                        intent.putExtra("group_text",document.get("group_text").toString());
                        intent.putExtra("max_people",document.get("max_people").toString());
                        startActivity(intent);
                    }
                });
    }
    public void group_enable(){
        if(group_enable.getText().equals("모집종료")){
            group_enable.setText("모집");
            firebaseFirestore.collection("group").document(group_id).update("group_enable","0");
            Toast.makeText(this, "모집을 종료합니다.", Toast.LENGTH_SHORT).show();
        }
        else{
            group_enable.setText("모집종료");
            firebaseFirestore.collection("group").document(group_id).update("group_enable","1");
            Toast.makeText(this, "모집을 시작합니다.", Toast.LENGTH_SHORT).show();
        }
    }
    public void btntext(){
        firebaseFirestore.collection("group").document(group_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if(document.getData().get("group_enable").equals("1")){
                    group_enable.setText("모집종료");
                }else{
                    group_enable.setText("모집");
                }
            }
        });

    }

public void member_list(){  //view 안에 회원목록

    firebaseFirestore.collection("group").document(group_id).get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        hash = (HashMap<String, HashMap<String, Object>>) document.getData().get("users");
                        for(String key : hash.keySet()){
                            db.collection("users").document(key).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task2) {

                                            if(task2.isSuccessful()){
                                                DocumentSnapshot document2 = task2.getResult();
                                                member_list.add(new Memberlist(document2.getData().get("name").toString()));
                                                memberAdapter = new MemberListAdapter(member_list);
                                                listView.setAdapter(memberAdapter);
                                            }

                                        }
                                    });
                        }

                    }
                }
            });
}
public void chat() {
    firebaseFirestore.collection("group").document(group_id).get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    Intent intent = new Intent(getApplicationContext(), ChatView.class);
                    intent.putExtra("group_id",group_id);
                    intent.putExtra("group_name", document.get("group_name").toString());
                    intent.putExtra("group_text",document.get("group_text").toString());
                    startActivity(intent);
                }
            });
}

}