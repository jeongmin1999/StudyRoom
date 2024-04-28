package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.ac.yeonsung.ksj.ex1.R;

public class Groupadd extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    HashMap<String, Object> hash = new HashMap<>();
    private HashMap<String, Object> user_map = new HashMap<>();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText group_name;
    private EditText group_text;
    private Date date = new Date();
    private Button group_add;
    private String level;
    private String enable;
    private EditText max_people;
    private int document_id;
    private int now_people;
    private String group_master;
    private String reg_date;
    private ArrayList<Integer> group_id_list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_add);
        firebaseFirestore = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        reg_date = simpleDateFormat.format(date);
        level = "1";
        enable = "1";
        now_people = 1;
        group_name = findViewById(R.id.group_name);
        group_text = findViewById(R.id.group_text);
        group_add = findViewById(R.id.group_add);
        max_people = findViewById(R.id.max_people);
        group_add.setOnClickListener(addListener);
        setGroup_id_list();
        group_member();
    }
public void setGroup_id_list(){
    firebaseFirestore.collection("group")
        .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int i = Integer.parseInt(document.getId());
                            System.out.println(i);
                            group_id_list.add(i);
                        }
                    }
                }
            });
}

    View.OnClickListener addListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            firebaseFirestore.collection("group")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document_id = Collections.max(group_id_list);
                                    document_id++;
                                    hash.put(user.getEmail(),user_map);
                                    Map<String,Object> docData = new HashMap<>();
                                    if(group_name.length() >0 && group_text.length() > 0 && Long.parseLong(max_people.getText().toString())>0) {
                                        docData.put("group_name", group_name.getText().toString());
                                        docData.put("group_text", group_text.getText().toString());
                                        docData.put("reg_date", reg_date);
                                        docData.put("group_master", user.getEmail());
                                        docData.put("group_enable", enable);
                                        docData.put("max_people", Long.parseLong(max_people.getText().toString()));
                                        docData.put("now_people", now_people);
                                        docData.put("users", hash);

                                    System.out.println(hash);
                                    firebaseFirestore.collection("group").document(String.valueOf(document_id))
                                            .set(docData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Groupadd.this, "그룹이 생성되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });}
                                    else {
                                        Toast.makeText(Groupadd.this, "그룹 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                myStartActivity(MainActivity.class);
                            }
                        }
                    });
        }
    };
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void group_member(){
        firebaseFirestore.collection("users")
                .document(user.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                user_map = (HashMap<String, Object>) document.getData();
                            }
                        }
                    }
                });
    }

}
