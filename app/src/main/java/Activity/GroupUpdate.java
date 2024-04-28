package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import kr.ac.yeonsung.ksj.ex1.R;

public class GroupUpdate extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private EditText group_name_update;
    private String group_name;
    private EditText group_text_update;
    private EditText max_people_update;
    private String group_text;
    private Button group_update;
    private String max_people;
    private String group_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_update);
        firebaseFirestore = FirebaseFirestore.getInstance();
        
        group();
        group_name_update = findViewById(R.id.group_name_update);
        group_text_update = findViewById(R.id.group_text_update);
        max_people_update = findViewById(R.id.max_people_update);
        group_name_update.setText(group_name);
        group_text_update.setText(group_text);
        max_people_update.setText(max_people);
        group_update = findViewById(R.id.group_update);
        group_update.setOnClickListener(updateListener);


}

    View.OnClickListener updateListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            update();
        }
    };
    public void update(){
        if(group_name_update.length() > 0 && group_text.length() > 0 && Long.parseLong(String.valueOf(max_people_update.getText()))>0){
        firebaseFirestore.collection("group").document(group_id).update("group_name", group_name_update.getText().toString());
        firebaseFirestore.collection("group").document(group_id).update("group_text", group_text_update.getText().toString());
        firebaseFirestore.collection("group").document(group_id).update("max_people", Long.parseLong(String.valueOf(max_people_update.getText())));
        Toast.makeText(this, "수정완료", Toast.LENGTH_SHORT).show();
        myStartActivity(MainActivity.class);}
        else{
            Toast.makeText(this, "정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void group(){
        Intent intent = getIntent();
        group_id = intent.getStringExtra("group_id");
        group_name = intent.getStringExtra("group_name");
        group_text = intent.getStringExtra("group_text");
        max_people = intent.getStringExtra("max_people");
    }

}
