package Activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Info.Memberlist;
import adapter1.MemberListAdapter;
import kr.ac.yeonsung.ksj.ex1.R;

public class ReservationView extends AppCompatActivity {
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
    private TextView room_name;
    private TextView reservation_text;
    private TextView reg_date;
    private LinearLayout memberListName;
    private Button group_join;
    private Button group_out;
    private Button reservation_delete;
    private Button reservation_update;
    private Button group_enable;
    private Button chat_btn;
    private String group_id;
    private String reservation_id;
    private String room_id;
    private String reservation_day;
    private String start_time;
    private Long max_people;
    private Long min_people;
    private Long now_people;
    private String text = "예약 상세";
    private DatabaseReference mRef;
    private DatabaseReference mDatabase;
    private static final String TAG = "GroupView : ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_view);
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        group_id = "1";
        reservation_id = intent.getStringExtra("reservation_id");
        room_id = intent.getStringExtra("room_id");
        reservation_day = intent.getStringExtra("reservation_day");
        start_time = intent.getStringExtra("start_time");

        System.out.println(reservation_id +" = "+ room_id);
        group_enable = findViewById(R.id.group_enable);
        mRef =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://studyroom-project-default-rtdb.firebaseio.com");

        mDatabase = mRef.child("reservation");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,data);

        room_name = findViewById(R.id.room_name);
        reservation_text = findViewById(R.id.reservation_text);
        reg_date = findViewById(R.id.reg_date);
        chat_btn = findViewById(R.id.chat_btn);
        group_join = findViewById(R.id.group_join);
        group_out = findViewById(R.id.group_out);
        reservation_delete = findViewById(R.id.reservation_delete);
        reservation_update = findViewById(R.id.reservation_update);
        memberName = findViewById(R.id.memberName);
        listView = findViewById(R.id.listview);
        memberListName = findViewById(R.id.memberListName);

        member_list();
        reservation_info();

        group_join.setOnClickListener(btnLisener);
        group_out.setOnClickListener(btnLisener);
        reservation_delete.setOnClickListener(btnLisener);
        reservation_update.setOnClickListener(btnLisener);
        group_enable.setOnClickListener(btnLisener);
        chat_btn.setOnClickListener(btnLisener);


    }


    View.OnClickListener btnLisener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.group_join:

                    break;
                case R.id.group_out:
                    group_out();
                    break;
                case R.id.reservation_delete:
                    reservation_delete();
                    break;
                case R.id.reservation_update:
                    reservation_update();
                    break;
                case R.id.group_enable:

                    break;
                case R.id.chat_btn:

                    break;
            }

        }
    };
    private void myStartActivity(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void reservation_update(){
        Intent intent = new Intent(getApplicationContext(), ReservationUpdate.class);
        intent.putExtra("reservation_id",reservation_id);
        intent.putExtra("room_id",room_id);
        intent.putExtra("reservation_day",reservation_day);
        intent.putExtra("start_time",start_time);
        intent.putExtra("room_name",room_name.getText());
        startActivity(intent);
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

                                Toast.makeText(ReservationView.this, "탈퇴 성공", Toast.LENGTH_SHORT).show();
                                 myStartActivity(MainActivity.class);
                            }
                        }
                    }
                });
    }
    public void reservation_delete(){
        mDatabase.child(room_id).child(reservation_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(ReservationView.this, "취소 완료", Toast.LENGTH_SHORT).show();
                myStartActivity(MainActivity.class);
            }
        });

    }
    public void member_list(){  //view 안에 회원목록

        mDatabase.child(room_id)
            .child(reservation_id)
                .child("users")
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                String name = "미입력";

                String phone ="미입력";
                DataSnapshot data = task.getResult();
                now_people = data.getChildrenCount();
                for (DataSnapshot data1: data.getChildren()) {
                    if(data1.child("name").getValue() != null || data1.child("phoneNumber").getValue() != null){
                        name = data1.child("name").getValue().toString();
                        phone =  data1.child("phoneNumber").getValue().toString();
                    }
                    System.out.println(data1.getValue());


                    member_list.add(new Memberlist("이름: " + name + "   phone: "+phone+"\n"));
                    memberAdapter = new MemberListAdapter(member_list);
                    listView.setAdapter(memberAdapter);
                }


            }
        });

    }
    public void reservation_info() {

        firebaseFirestore.collection("rooms").document(room_id)
            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(task.isSuccessful()){
                    room_name.setText(doc.get("room_num").toString());
                    max_people = (Long) doc.get("max_people");
                    min_people = (Long) doc.get("min_people");
                    System.out.println(max_people + " " + min_people);
                }
                text = "예약번호 : " + reservation_id + "\n" +
                        "날짜 : " + reservation_day + "\n" +
                        "시간 : " + start_time + "\n" +
                        "최대 수용 인원 : " + max_people + "\n"+
                        "최소 수용 인원 : " + min_people + "\n"+
                        "현재 인원 : " + now_people;
                reservation_text.setText(text);
            }
        });


    }
}

