package Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import Activity.LoginActivity;
import Info.MyReservationInfo;
import adapter1.My_ReservationAdapter;
import kr.ac.yeonsung.ksj.ex1.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyReservationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyReservationFragment extends Fragment {
    private static final String TAG = "MyReservationActivity";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    Button log_out;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<MyReservationInfo> arrayList = new ArrayList();
    private ArrayList<String> spinners = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner spinner;
    private DatabaseReference mRef;
    private DatabaseReference mDatabase;
    public MyReservationFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyReservationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyReservationFragment newInstance(String param1, String param2) {
        MyReservationFragment fragment = new MyReservationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_my_reservation, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView_reservation);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        spinner = rootView.findViewById(R.id.spinner);
        spinnerSet();
        log_out = rootView.findViewById(R.id.log_out);
        mRef =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://studyroom-project-default-rtdb.firebaseio.com");
        log_out.setOnClickListener(outListener);
        mDatabase = mRef.child("reservation");
        spinner.setOnItemSelectedListener(spinnerListener);
        return rootView;
    }
    public void log_out(){
        mAuth.signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    View.OnClickListener outListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            log_out();
        }
    };

    public void spinnerSet(){
        spinners.clear();
        db.collection("rooms").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    spinners.add("전체");
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        spinners.add(document.getData().get("room_num").toString());
                    }
                    spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinners);
                }

                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);
            }
        });
    }
    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            String str = adapterView.getItemAtPosition(i).toString();
            System.out.println("스피너 : " + str);
            Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
            filter(str);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    public void filter(String str){
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String day;
                String reservationId;
                String startTime;
                String roomId;
                String room_name;
                arrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                        for(DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()){
                            for(DataSnapshot dataSnapshot3 : dataSnapshot2.getChildren()){
                                System.out.println("예약자 이메일 : "+dataSnapshot3.child("email").getValue());
                                if(str.equals("전체")){
                                    if (dataSnapshot3.child("email").getValue().equals(user.getEmail())) {
                                        roomId = dataSnapshot.getKey();
                                        reservationId = dataSnapshot1.getKey();
                                        room_name = dataSnapshot1.child("room_name").getValue().toString();
                                        day = dataSnapshot1.child("day").getValue().toString();
                                        startTime = dataSnapshot1.child("start_time").getValue().toString();
                                        MyReservationInfo myReservationInfo = new MyReservationInfo(roomId, day, startTime, reservationId, room_name);
                                        arrayList.add(myReservationInfo);
                                    }
                                } else {
                                    if (dataSnapshot3.child("email").getValue().equals(user.getEmail()) && dataSnapshot1.child("room_name").getValue().toString().equals(str)) {
                                        roomId = dataSnapshot.getKey();
                                        reservationId = dataSnapshot1.getKey();
                                        room_name = dataSnapshot1.child("room_name").getValue().toString();
                                        day = dataSnapshot1.child("day").getValue().toString();
                                        startTime = dataSnapshot1.child("start_time").getValue().toString();
                                        MyReservationInfo myReservationInfo = new MyReservationInfo(roomId, day, startTime, reservationId, room_name);
                                        arrayList.add(myReservationInfo);
                                    }
                                }
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new My_ReservationAdapter(arrayList,getActivity());
        recyclerView.setAdapter(adapter);
    }
}