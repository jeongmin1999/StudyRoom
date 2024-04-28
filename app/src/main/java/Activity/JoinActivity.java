package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import kr.ac.yeonsung.ksj.ex1.R;
import Info.UserInfo;

public class JoinActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "JoinActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.join_btn).setOnClickListener(joinListener);
        findViewById(R.id.go_to_Login).setOnClickListener(joinListener);

    }
    View.OnClickListener joinListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()) {
                case R.id.join_btn:
                    Join();
                    break;
                case R.id.go_to_Login:
                    startLoginActivity();
                    break;
            }
        }
    };

private void Join(){
    String email = ((EditText)findViewById(R.id.user_email)).getText().toString();
    String password = ((EditText)findViewById(R.id.user_Password)).getText().toString();
    String passwordcheck = ((EditText)findViewById(R.id.user_PasswordCheck)).getText().toString();
    if(email.length() > 0 && password.length() >0 && passwordcheck.length()>0) {
        if (password.equals(passwordcheck)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                startToast("회원가입을 성공했습니다.");
                                user_Info();
                                //UI
                            } else {
                                if (task.getException() != null) {
                                    startToast(task.getException().toString());
                                }
                            }
                        }
                    });
        } else {
            startToast("비밀번호가 일치하지 않습니다.");
        }
    }else{
        startToast("이메일 또는 비밀번호를 입력하세요.");
    }
}

private void user_Info(){
    String email = ((EditText)findViewById(R.id.user_email)).getText().toString();
    String name = ((EditText) findViewById(R.id.user_Name)).getText().toString();
    String phoneNumber = ((EditText) findViewById(R.id.user_Phone)).getText().toString();
    String level = ((TextView)findViewById(R.id.user_level)).getText().toString();

    if (name.length() > 0 && phoneNumber.length() > 0) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        UserInfo userInfo = new UserInfo(name, phoneNumber, level);

            db.collection("users").document(email).set(userInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startLoginActivity();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            startToast("회원정보 등록에 실패하였습니다.");
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
    } else {
        startToast("회원 정보를 입력하세요.");
    }
}
private void startToast(String msg){
    Toast.makeText(this, msg,Toast.LENGTH_SHORT).show();
}
private void startLoginActivity(){
    Intent intent = new Intent(this, LoginActivity.class);
    startActivity(intent);
}
}