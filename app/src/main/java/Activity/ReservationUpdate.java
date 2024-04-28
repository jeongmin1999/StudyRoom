package Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import kr.ac.yeonsung.ksj.ex1.R;

public class ReservationUpdate extends AppCompatActivity {


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
   
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private DatabaseReference mDatabase;
    private DatabaseReference mRef;
    private String room_id;
    String str;
    private HashMap<String, String> reservation = new HashMap<String, String>();
    private TextView date_text;
    private HashMap<String, Object> user_map = new HashMap<>();
    private HashMap<String, HashMap<String, Object>> hash = new HashMap<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TextView room_num_text;

    private DatePicker datePicker;
    
    private List<String> data = new ArrayList<>();
    
    private LinearLayout layout;
    
    private TextView reservation_info;
    private Button reservation_time_go;
    private String reservation_day;
    private String start_time;
    private String room_name;
    private String date_str = "";
    private HashMap insertUser = new HashMap();
    private String reservation_id;
    List<String > times = new ArrayList<>();
    private int[] btnarray = {R.id.time1, R.id.time2,R.id.time3,R.id.time4,R.id.time5,R.id.time6,
            R.id.time7,R.id.time8,R.id.time9,R.id.time10,R.id.time11,R.id.time12,R.id.time13,
            R.id.time14,R.id.time15,R.id.time16,R.id.time17,R.id.time18};
    private Button timeSetBtn[] = new Button[btnarray.length];

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_update);
        date_text = findViewById(R.id.date_text);
        Intent intent = getIntent();
        day = new Date();
        date_text.setText(dateFormat.format(day));
        now_date = dateFormat2.format(day2);
        select_date = dateFormat2.format(day);
        reservation_id = intent.getStringExtra("reservation_id");
        room_id = intent.getStringExtra("room_id");
        reservation_day = intent.getStringExtra("reservation_day");
        start_time = intent.getStringExtra("start_time");
        room_name = intent.getStringExtra("room_name");
        mRef =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://studyroom-project-default-rtdb.firebaseio.com");

        layout = findViewById(R.id.layout);
        reservation_info = findViewById(R.id.reservation_info);
        room_num_text = findViewById(R.id.room_num_text);
        reservation_time_go = findViewById(R.id.reservation_time_go);
        datePicker = findViewById(R.id.date_picker);

        btn_enable();
        setReservationInfo();
        setTimeBtn();
        reservation_time_go.setOnClickListener(reservationTimeListener);
        datePicker.setOnDateChangedListener(dateListener);
        System.out.println("멤버 수 " + group_people);


    }
    public void setReservationInfo(){
        str = "예약 번호 : " + reservation_id + "\n" +
                     "스터디룸 : " + room_name + "\n" +
                     "날짜 : " + reservation_day + "\n" +
                     "예약 시간 : " + start_time + "\n" ;
        reservation_info.setText(str);
    }
    public void setTimeBtn(){
        for(int i=0; i<btnarray.length; i++){
            timeSetBtn[i]=findViewById(btnarray[i]);
            timeSetBtn[i].setOnClickListener(timebtnClickListener);
        }
    }

    View.OnClickListener reservationTimeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            HashMap dayMap = new HashMap();
            HashMap timeMap = new HashMap();
            if(times.size()==0) {
                Toast.makeText(ReservationUpdate.this, "예약하실 시간을 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
            else {
                mDatabase = FirebaseDatabase.getInstance().getReference();

                DatabaseReference myRef = database.getReference("reservation").child(room_id).child(String.valueOf(reservation_id));
                dayMap.put("day", dateFormat.format(day));
                timeMap.put("start_time", String.valueOf(times));

                myRef.updateChildren(dayMap);
                myRef.updateChildren(timeMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Toast.makeText(ReservationUpdate.this, "예약 수정 완료", Toast.LENGTH_SHORT).show();
                    }
                });

                myStartActivity();
            }

        }
    };
    View.OnClickListener timebtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (Integer.parseInt(now_date) > Integer.parseInt(select_date)) {
                Toast.makeText(ReservationUpdate.this, "지난 날짜는 예약할 수 없습니다.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ReservationUpdate.this, "버튼은 4개까지 선택 가능합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    System.out.println(times);
                }
            }
        }
    };
    private void myStartActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    DatePicker.OnDateChangedListener dateListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year -= 1900;
            day = new Date(year, monthOfYear, dayOfMonth);
            times.clear();
            date_text.setText(dateFormat.format(day));

            now_date = dateFormat2.format(day2);
            select_date = dateFormat2.format(day);

            System.out.println("현재 날짜 : " + now_date);
            System.out.println("선택한 날짜 : " + select_date);
            btn_enable();
            System.out.println("타임즈 : " + times);
        }
    };
//
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


}
