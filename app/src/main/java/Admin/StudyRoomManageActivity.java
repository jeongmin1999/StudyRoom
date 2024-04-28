package Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import Activity.RoomAdd;
import Info.RoomInfo;
import kr.ac.yeonsung.ksj.ex1.R;

public class StudyRoomManageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<RoomInfo> arrayList;
    private FirebaseFirestore db;
    private String room_id ,room_image,room_num;
    private Long max_people, min_people;
    private FloatingActionButton roomAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room_manage);

        roomAddBtn = findViewById(R.id.room_add);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList  = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        db.collection("rooms")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            arrayList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                room_id = document.getId();
                                max_people = (Long) document.get("max_people");
                                min_people = (Long) document.get("min_people");
                                room_image = (String) document.get("room_image");
                                room_num = (String) document.get("room_num");

                                RoomInfo roomInfo = new RoomInfo(room_image,  room_num,  max_people,  min_people, room_id);
                                arrayList.add(roomInfo);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                        }
                    }
                });
        adapter = new RoomManageAdapter(arrayList,this);
        recyclerView.setAdapter(adapter);

        roomAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RoomAdd.class);
                startActivity(intent);
            }
        });

    }

}




