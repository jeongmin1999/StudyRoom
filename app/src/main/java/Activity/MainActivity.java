package Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import Fragment.GroupFragment;
import Fragment.MyReservationFragment;
import Fragment.My_GroupFragment;
import Fragment.RoomFragment;
import kr.ac.yeonsung.ksj.ex1.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private GroupFragment fragmentGroup = new GroupFragment();
    private RoomFragment fragmentReservation = new RoomFragment();
    private MyReservationFragment fragmentCheck = new MyReservationFragment();
    private My_GroupFragment fragmentTeam = new My_GroupFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.menu_frame_layout, fragmentGroup).commitAllowingStateLoss();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(new ItemSelectedListener());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();



    if(user == null){
        myActivity(LoginActivity.class);
    }else{
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        }
    }

    public void myActivity(Class c) {
        Intent intent = new Intent(this, c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch (menuItem.getItemId()) {
                case R.id.menu_group:
                    transaction.replace(R.id.menu_frame_layout, fragmentGroup).commitAllowingStateLoss();
                    break;
                case R.id.menu_reservation:
                    transaction.replace(R.id.menu_frame_layout, fragmentReservation).commitAllowingStateLoss();
                    break;
                case R.id.menu_my_reservation:
                    transaction.replace(R.id.menu_frame_layout, fragmentCheck).commitAllowingStateLoss();
                    break;
                case R.id.menu_my_group:
                    transaction.replace(R.id.menu_frame_layout, fragmentTeam).commitAllowingStateLoss();
                    break;
            }

            return true;
        }
    }

}