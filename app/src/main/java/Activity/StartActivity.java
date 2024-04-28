package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Admin.AdminActivity;
import kr.ac.yeonsung.ksj.ex1.R;

public class StartActivity extends AppCompatActivity {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String level;
    Handler h = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        if(user == null) {
            h.postDelayed(new splashHandler(), 2000);
        }else {
            getLevel();
        }

    }

    class splashHandler implements Runnable {

        public void run() {

            startActivity(new Intent (getApplication(),LoginActivity.class));

            StartActivity.this.finish();

        }
    }
    class splashHandler2 implements Runnable {

        public void run() {

            startActivity(new Intent (getApplication(),MainActivity.class));

            StartActivity.this.finish();

        }
    }
    class splashHandler3 implements Runnable {

        public void run() {

            startActivity(new Intent (getApplication(), AdminActivity.class));

            StartActivity.this.finish();

        }
    }
    public void getLevel(){
        db.collection("users").document(user.getEmail()).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            level = document.getData().get("level").toString();
                            System.out.println(level);

                                if(level.equals("1")){
                                    h.postDelayed(new splashHandler3(), 2000);
                                }else {
                                    h.postDelayed(new splashHandler2(), 2000);
                                }
                        }
                    }
                }
        );

    }
}
