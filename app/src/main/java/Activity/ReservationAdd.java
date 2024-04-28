package Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Info.Memberlist;
import adapter1.MemberListAdapter2;
import kr.ac.yeonsung.ksj.ex1.R;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ReservationAdd extends AppCompatActivity {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
    List<String> c = new ArrayList<>();
    Date day;
    Date day2 = new Date();
    String s = null;
    String[] d = null;
    String now_date;
    String select_date;
    int max_people, min_people;
    int group_people = 0;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    RadioButton check_group, check_personal;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private DatabaseReference mDatabase;
    private DatabaseReference mRef;
    private String room_num, room_id;
    private HashMap<String, String> reservation = new HashMap<String, String>();
    private TextView date_text;
    String member_name;
    private ArrayAdapter<String> adapter;
    private HashMap member = new HashMap();
    private ArrayList<String> groups = new ArrayList<>();
    private String group_id;
    private HashMap<String, Object> user_map = new HashMap<>();
    private HashMap<String, HashMap<String, Object>> hash = new HashMap<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TextView room_num_text;
    private RadioGroup group_or_personal;
    private DatePicker datePicker;
    private ListView roomListView;
    private ArrayList<Memberlist> member_list = new ArrayList<>();
    private List<String> roomInfo = new ArrayList<>();
    private LinearLayout layout;
    private ArrayAdapter<String> room_adapter;
    private Spinner group_select;
    private Button reservation_time_go;
    private HashMap insertUser = new HashMap();
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter memberadapter;
    private int reservation_id;
    List<String > times = new ArrayList<>();
    private int[] btnarray = {R.id.time1, R.id.time2,R.id.time3,R.id.time4,R.id.time5,R.id.time6,
            R.id.time7,R.id.time8,R.id.time9,R.id.time10,R.id.time11,R.id.time12,R.id.time13,
            R.id.time14,R.id.time15,R.id.time16,R.id.time17,R.id.time18};
    private Button timeSetBtn[] = new Button[btnarray.length];
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_reservation);
        date_text = findViewById(R.id.date_text);
        check_group = findViewById(R.id.check_group);
        check_personal = findViewById(R.id.check_personal);
        Intent intent = getIntent();
        recyclerView = findViewById(R.id.recyclerView_memberList);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        day = new Date();
        date_text.setText(dateFormat.format(day));
        now_date = dateFormat2.format(day2);
        select_date = dateFormat2.format(day);
        room_num = intent.getStringExtra("room_num");
        room_id = intent.getStringExtra("room_id");
        mRef =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://studyroom-project-default-rtdb.firebaseio.com");
        btn_enable();
        setGroup_id();
        getRoomInfo();
        setTimeBtn();
        room_name();
        layout = findViewById(R.id.layout);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groups);
        room_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,roomInfo);

        roomListView = findViewById(R.id.room_info);
        room_num_text = findViewById(R.id.room_num_text);
        reservation_time_go = findViewById(R.id.reservation_time_go);
        group_select = findViewById(R.id.group_select);
        group_or_personal = findViewById(R.id.group_or_personal);
        datePicker = findViewById(R.id.date_picker);
        datePicker.setOnDateChangedListener(dateListener);
        group_or_personal.setOnCheckedChangeListener(CheckedListener);
        group_select.setOnItemSelectedListener(SelectedListener);
        reservation_time_go.setOnClickListener(reservationTimeListener);
        room_num_text.setText(room_num);

        setReservation_id_list();

        System.out.println("멤버 수 " + group_people);


    }
    public void setTimeBtn(){
        for(int i=0; i<btnarray.length; i++){
            timeSetBtn[i]=findViewById(btnarray[i]);
            timeSetBtn[i].setOnClickListener(timebtnClickListener);
        }
    }
    public void setReservation_id_list(){

        mDatabase = mRef.child("reservation").child(room_id);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    reservation_id = Integer.parseInt(dataSnapshot.getKey());
                }
                reservation_id += 1;
                System.out.println(reservation_id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setGroup_id(){
        firebaseFirestore.collection("group").
            whereEqualTo("group_master",user.getEmail()).
                get().
                    addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot doc : task.getResult()){
                                    groups.add(doc.getId()+ " : "+doc.get("group_name").toString());
                                }
                            }

                        }
                    });
    }
    public void getPersonal_map(){
        firebaseFirestore.collection("users")
                .document(user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                hash.clear();
                                user_map.clear();
                                user_map = (HashMap<String, Object>) document.getData();
                                System.out.println(user_map);
                                hash.put(user.getEmail(), user_map);
                            }
                        }
                    }
                });
    }
    public void setMember_map(String id){
        firebaseFirestore.collection("group").
            document(id).
                get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        hash.clear();
                        DocumentSnapshot document = task.getResult();
                        hash = (HashMap<String, HashMap<String, Object>>) document.getData().get("users");
                        System.out.println(hash);
                    }
                });
    }
    public void getRoomInfo(){
        firebaseFirestore.collection("rooms").document(room_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                        roomListView.setAdapter(room_adapter);
                        if(task2.isSuccessful()){
                            DocumentSnapshot document2 = task2.getResult();
                            max_people = Integer.parseInt(document2.getData().get("max_people").toString());
                            min_people = Integer.parseInt(document2.getData().get("min_people").toString());
                            System.out.println("최대인원 : " + max_people);
                            roomInfo.add("최대 수용 인원 : "+document2.getData().get("max_people"));
                            roomInfo.add("최소 수용 인원 : " + document2.getData().get("min_people"));
                        }
                    }
                });
    }
    public void member_list(String id){  //view 안에 회원목록
        hash.clear();
        member_list.clear();
        firebaseFirestore.collection("group").document(id).get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        hash = (HashMap<String, HashMap<String, Object>>) document.getData().get("users");
                        group_people=0;
                        for(String key : hash.keySet()){
                            group_people++;
                            firebaseFirestore.collection("users").document(key).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                            if(task2.isSuccessful()){
                                                DocumentSnapshot document2 = task2.getResult();
                                                member_name = document2.getData().get("name").toString();
                                                Memberlist mb = new Memberlist(member_name);
                                                member_list.add(mb);
                                            }
                                            memberadapter.notifyDataSetChanged();
                                        }
                                    });
                            System.out.println("멤버수 " + group_people);
                        }
                    }
                }
            });
        memberadapter = new MemberListAdapter2(member_list,getApplicationContext());
        recyclerView.setAdapter(memberadapter);
    }
    View.OnClickListener reservationTimeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!check_group.isChecked() && !check_personal.isChecked()) {
                Toast.makeText(ReservationAdd.this, "그룹 or 개인 체크", Toast.LENGTH_SHORT).show();
            } else{
                if(times.size()==0) {
                    Toast.makeText(ReservationAdd.this, "예약하실 시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    if (max_people < group_people) {
                        Toast.makeText(ReservationAdd.this, "최대인원을 초과했습니다.", Toast.LENGTH_SHORT).show();
                    } else if(check_personal.isChecked()){
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        int i = 1;
                        for (String key : hash.keySet()) {

                            HashMap user_map2;
                            user_map2 = hash.get(key);
                            user_map2.put("email", key);

                            insertUser.put(String.valueOf(i), user_map2);

                            i++;
                        }
                        DatabaseReference myRef = database.getReference("reservation").child(room_id).child(String.valueOf(reservation_id));

                        reservation.put("day", dateFormat.format(day));
                        reservation.put("start_time", String.valueOf(times));

                        myRef.setValue(reservation);

                        mDatabase.child("reservation").child(room_id).child(String.valueOf(reservation_id)).child("users")
                                .updateChildren(insertUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ReservationAdd.this, "완료", Toast.LENGTH_SHORT).show();
                            }
                        });
                        myStartActivity();
                    }

                    else if(group_people < min_people){
                        Toast.makeText(ReservationAdd.this, "최소 인원 이상 사용 가능합니다.", Toast.LENGTH_SHORT).show();
                    }else {
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        int i = 1;

                        for (String key : hash.keySet()) {

                            HashMap user_map2 = new HashMap();
                            user_map2 = hash.get(key);
                            user_map2.put("email", key);

                            insertUser.put(String.valueOf(i), user_map2);

                            i++;
                        }
                        DatabaseReference myRef = database.getReference("reservation").child(room_id).child(String.valueOf(reservation_id));

                        reservation.put("day", dateFormat.format(day));
                        reservation.put("start_time", String.valueOf(times));

                        myRef.setValue(reservation);

                        mDatabase.child("reservation").child(room_id).child(String.valueOf(reservation_id)).child("users")
                                .updateChildren(insertUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ReservationAdd.this, "완료", Toast.LENGTH_SHORT).show();
                            }
                        });
                        myStartActivity();
                    }
                }
            }
        }
    };
    AdapterView.OnItemSelectedListener SelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            hash.clear();
            group_id = groups.get(i).split(" : ")[0];
            System.out.println("id : "+group_id);
            setMember_map(group_id);
            member_list(group_id);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            group_id = "선택";
        }
    };
    DatePicker.OnDateChangedListener dateListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year -= 1900;
            day = new Date(year, monthOfYear, dayOfMonth);
            times.clear();
            date_text.setText(dateFormat.format(day));
            setReservation_id_list();


            now_date = dateFormat2.format(day2);
            select_date = dateFormat2.format(day);

            System.out.println("현재 날짜 : " + now_date);
            System.out.println("선택한 날짜 : " + select_date);
            btn_enable();
            System.out.println("타임즈 : " + times);
        }
    };
    RadioGroup.OnCheckedChangeListener CheckedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            layout.setVisibility(View.VISIBLE);
            if(i == R.id.check_group){
                recyclerView.setVisibility(View.VISIBLE);
                group_select.setVisibility(View.VISIBLE);
                group_select.setAdapter(adapter);
            }
            if(i == R.id.check_personal){
                group_select.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                getPersonal_map();
            }
        }
    };

    private void myStartActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btn_enable() {
        mDatabase = mRef.child("reservation").child(room_id);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                c.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    s = dataSnapshot.child("start_time").getValue().toString();
                    d = s.substring(1,s.length()-1).split(", ");
                    if(dataSnapshot.child("day").getValue().toString().equals(date_text.getText().toString())){
                        for(int i=0; i <d.length; i++){
                            c.add(d[i]);
                        }
                        for(int i = 0; i<btnarray.length;i++) {
                            System.out.println(c);
                            System.out.println(timeSetBtn[i].getText());
                            if (c.contains(timeSetBtn[i].getText())){
                                System.out.println("성공");
                                timeSetBtn[i].setEnabled(false);
                                timeSetBtn[i].setBackgroundColor(Color.LTGRAY);
                                timeSetBtn[i].setTextColor(Color.BLACK);
                            }else{
                                timeSetBtn[i].setEnabled(true);
                                timeSetBtn[i].setBackgroundColor(Color.BLACK);
                                timeSetBtn[i].setTextColor(Color.WHITE);
                            }
                        }
                    } else if(!dataSnapshot.child("day").getValue().toString().equals(date_text.getText().toString()) && c.size() == 0){
                        for(int i=0; i<btnarray.length; i++){
                            timeSetBtn[i].setEnabled(true);
                            timeSetBtn[i].setBackgroundColor(Color.BLACK);
                            timeSetBtn[i].setTextColor(Color.WHITE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    View.OnClickListener timebtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (Integer.parseInt(now_date) > Integer.parseInt(select_date)) {
                Toast.makeText(ReservationAdd.this, "지난 날짜는 예약할 수 없습니다.", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < btnarray.length; i++) {
                    if (view.getId() == btnarray[i]) {
                        if (times.contains(timeSetBtn[i].getText())) {
                            times.remove(timeSetBtn[i].getText().toString());
                            timeSetBtn[i].setBackgroundColor(Color.BLACK);
                        } else if (times.size() <= 3) {
                            times.add(timeSetBtn[i].getText().toString());
                            timeSetBtn[i].setBackgroundColor(Color.CYAN);
                        } else {
                            Toast.makeText(ReservationAdd.this, "버튼은 4개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    System.out.println(times);
                }
            }
        }
    };
    public void room_name(){
        firebaseFirestore.collection("rooms").document(room_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    reservation.put("room_name",document.getData().get("room_num").toString());
                    System.out.println(document.getData().get("room_num").toString());
                    System.out.println(reservation);
                }
            }
        });
    }
}
