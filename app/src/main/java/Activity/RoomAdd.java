package Activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import Admin.StudyRoomManageActivity;
import Info.RoomInfo;
import kr.ac.yeonsung.ksj.ex1.R;

public class RoomAdd extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private TextView max_people,min_people,room_name;
    private Button room_create,upload_btn;
    private ImageView room_image;
    private Uri imageUri;
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_add);

        max_people = findViewById(R.id.edit_study_room_max_people);
        min_people = findViewById(R.id.edit_study_room_min_people);
        room_name = findViewById(R.id.edit_study_room_num);
        room_create = findViewById(R.id.btn_room_create);
        room_image = findViewById(R.id.room_image);
        upload_btn = findViewById(R.id.upload_btn);

        upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/");
                activityResult.launch(galleryIntent);
            }
        });

        room_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(room_image.getDrawable()==null){
                    Toast.makeText(RoomAdd.this,"업로드 버튼을 클릭하여 이미지를 등록해 주세요",Toast.LENGTH_SHORT).show();
                }else if(room_name.getText().toString().getBytes().length <=0 || max_people.getText().toString().getBytes().length <=0|| min_people.getText().toString().getBytes().length <=0){
                    Toast.makeText(RoomAdd.this,"룸 정보를 입력해 주세요",Toast.LENGTH_SHORT).show();
                } else if(Integer.parseInt(max_people.getText().toString()) < Integer.parseInt(min_people.getText().toString())){
                    Toast.makeText(RoomAdd.this, "최소 인원은 최대 인원을 초과할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    Map<String,Object> docData = new HashMap<>();

                    docData.put("max_people",Long.parseLong(max_people.getText().toString()));
                    docData.put("min_people",Long.parseLong(min_people.getText().toString()));
                    docData.put("room_num",room_name.getText().toString());
                    docData.put("room_image","이미지 임시데이터");

                    firebaseFirestore.collection("rooms").add(docData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            if(imageUri != null){
                                uploadToFirebase(imageUri,documentReference.getId());
                            }
                        }
                    });
                    Intent intent = new Intent(getApplicationContext(), StudyRoomManageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
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

    private void uploadToFirebase(Uri uri , String room_id) {
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
                Toast.makeText(RoomAdd.this,"업로드 실패",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  String getFileExtension(Uri uri){

        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}