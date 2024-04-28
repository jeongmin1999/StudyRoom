package Admin;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import Info.RoomInfo;
import kr.ac.yeonsung.ksj.ex1.R;

public class RoomManageView extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private TextView managed_study_room;
    private String room_id;
    private EditText edit_study_room_num,edit_study_room_max_people,edit_study_room_min_people;
    private ImageView room_image;
//    private ProgressBar progressBar;
    private Button uploadBtn,updateBtn ,deleteBtn;
    private String TAG;
    private Uri imageUri;
    private String room_num;
    private DatabaseReference mRef;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase2;

    private final StorageReference reference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_room_manage_view);

        mRef =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://studyroom-project-default-rtdb.firebaseio.com");

        edit_study_room_num = (EditText) findViewById(R.id.edit_study_room_num);
        edit_study_room_max_people = (EditText) findViewById(R.id.edit_study_room_max_people);
        edit_study_room_min_people = (EditText) findViewById(R.id.edit_study_room_min_people);
        room_image =(ImageView) findViewById(R.id.room_image);

        uploadBtn = (Button) findViewById(R.id.upload_btn);
        managed_study_room = findViewById(R.id.managed_study_room);
        updateBtn = (Button) findViewById(R.id.btn_room_edit);
        deleteBtn = (Button) findViewById(R.id.btn_room_delete);


        Intent intent = getIntent();
        room_id = intent.getStringExtra("room_id");
        room_num = intent.getStringExtra("room_num");


        managed_study_room.setText(room_num+" 정보 관리");


        StorageReference pathReference = reference.child(room_id+".jpg");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(room_image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "이미지 로드 실패..", Toast.LENGTH_SHORT).show();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase = mRef.child("reservation").child(room_id);
                AlertDialog.Builder alert = new AlertDialog.Builder(RoomManageView.this);
                alert.setTitle("삭제하기");
                alert.setMessage("정말로 "+room_num+"을(를) 삭제 하시겠습니까?");

                alert.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        });
                alert.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseFirestore.collection("rooms").document(room_id).delete(); //해당 룸 삭제
                                mDatabase.removeValue(); // 해당 룸 예약까지 삭제.
                                Toast.makeText(RoomManageView.this,"삭제 완료",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), StudyRoomManageActivity.class);
                                startActivity(intent);
                            }
                        });
                alert.show();

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_study_room_num.getText().equals("") || edit_study_room_num.getText().length()<=0 ||
                edit_study_room_max_people.getText().equals("") || edit_study_room_max_people.getText().length()<=0 ||
                edit_study_room_min_people.getText().equals("") || edit_study_room_min_people.getText().length()<=0){
                    Toast.makeText(RoomManageView.this, "입력이 안된 사항이 있습니다.", Toast.LENGTH_SHORT).show();
                }else {
                    if(Long.parseLong(edit_study_room_max_people.getText().toString()) >= Long.parseLong(edit_study_room_min_people.getText().toString())) {
                        firebaseFirestore.collection("rooms").document(room_id).update("room_num", edit_study_room_num.getText().toString());
                        firebaseFirestore.collection("rooms").document(room_id).update("max_people", Long.parseLong(edit_study_room_max_people.getText().toString()));
                        firebaseFirestore.collection("rooms").document(room_id).update("min_people", Long.parseLong(edit_study_room_min_people.getText().toString()));
                        mDatabase = mRef.child("reservation").child(room_id);

                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                    String str = dataSnapshot.getKey().toString();
                                    if(dataSnapshot.child("room_name").getValue().toString().equals(room_num)){
                                        mDatabase.child(str).child("room_name").setValue(edit_study_room_num.getText().toString());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        if (imageUri != null) {
                            uploadToFirebase(imageUri);
                        }

                        Toast.makeText(RoomManageView.this, "업로드 완료", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), StudyRoomManageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RoomManageView.this, "최소 인원은 최대 인원을 초과할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("rooms").document(room_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    edit_study_room_num.setHint(document.getString("room_num"));
                    edit_study_room_min_people.setHint(document.getLong("min_people").toString());
                    edit_study_room_max_people.setHint(document.getLong("max_people").toString());
                    edit_study_room_num.setText(document.getString("room_num"));
                    edit_study_room_max_people.setText(document.getLong("max_people").toString());
                    edit_study_room_min_people.setText(document.getLong("min_people").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }


    ActivityResultLauncher<Intent> activityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode()== RESULT_OK && result.getData() != null){
                        imageUri = result.getData().getData();
                        room_image.setImageURI(imageUri);
                    }
                }
            }
    );

    private void uploadToFirebase(Uri uri) {
        StorageReference fileRef = reference.child(room_id+"."+ getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        RoomInfo model = new RoomInfo(uri.toString());
                        firebaseFirestore.collection("rooms").document(room_id).update("room_image",uri.toString());
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                progressBar.setVisibility(View.VISIBLE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(RoomManageView.this,"업로드 실패",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  String getFileExtension(Uri uri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

}