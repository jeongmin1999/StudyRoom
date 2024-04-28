package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Admin.AdminActivity;
import kr.ac.yeonsung.ksj.ex1.R;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();


        if (user != null) {
            myStartActivity(MainActivity.class);
        } else {
            findViewById(R.id.login_btn).setOnClickListener(onClickListener);
            findViewById(R.id.go_to_Join).setOnClickListener(onClickListener);
        }
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.login_btn:
                    getLevel();
                    break;

                case R.id.go_to_Join:
                    myStartActivity(JoinActivity.class);
                    break;
            }
        }
    };

    private void Login_User() {
        String email = ((EditText) findViewById(R.id.user_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.user_Password)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인에 성공했습니다.");
                                myStartActivity(MainActivity.class);

                            } else {
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                }
                            }
                        }
                    });

        } else {
            startToast("이메일 또는 비밀번호를 입력하세요.");
        }
    }
    private void Login_Admin() {
        String email = ((EditText) findViewById(R.id.user_email)).getText().toString();
        String password = ((EditText) findViewById(R.id.user_Password)).getText().toString();

        if (email.length() > 0 && password.length() > 0) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("로그인에 성공했습니다.");
                                myStartActivity(AdminActivity.class);

                            } else {
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                }
                            }
                        }
                    });

        } else {
            startToast("이메일 또는 비밀번호를 입력하세요.");
        }
    }
    private void startToast(String msg){
        Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
    public void getLevel(){
        String userMail = ((EditText) findViewById(R.id.user_email)).getText().toString();

        db.collection("users").document(userMail).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document = task.getResult();
                                level = document.getData().get("level").toString();

                                if(level.equals("1")){
                                    Login_Admin();
                                }
                                else{
                                    Login_User();
                                }
                            }
                    }
                }
        );
    }
}