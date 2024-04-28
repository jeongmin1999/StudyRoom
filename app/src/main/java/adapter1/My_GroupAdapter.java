package adapter1;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Activity.GroupView;
import Info.My_GroupInfo;
import kr.ac.yeonsung.ksj.ex1.R;

public class My_GroupAdapter extends RecyclerView.Adapter<My_GroupAdapter.CustomViewHolder> {

    private ArrayList<My_GroupInfo> arrayList_my_group;
    private Context context;
    String master_Email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;
    public My_GroupAdapter(ArrayList<My_GroupInfo> arrayList_my_group, Context context) {
        this.arrayList_my_group = arrayList_my_group;
        this.context = context;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_my_group, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.my_group_name.setText(arrayList_my_group.get(position).getGroup_name());
        holder.group_text.setText(arrayList_my_group.get(position).getGroup_text());
        holder.now_people.setText(arrayList_my_group.get(position).getNow_people().toString());
        holder.max_people.setText(arrayList_my_group.get(position).getMax_people().toString());
        holder.group_id.setText(arrayList_my_group.get(position).getGroup_id());
        docRef = db.collection("group").document(holder.group_id.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document1 = task.getResult();
                    master_Email = document1.getData().get("group_master").toString();
                    System.out.println(master_Email);
                    db.collection("users").document(master_Email).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot document2 = task.getResult();
                                holder.group_master.setText(document2.getData().get("name").toString());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public int getItemCount() {

        return (arrayList_my_group != null ? arrayList_my_group.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView my_group_name;
        TextView max_people;
        TextView now_people;
        TextView group_text;
        TextView group_id;
        TextView group_master;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.group_id = itemView.findViewById(R.id.group_id);
            this.group_master = itemView.findViewById(R.id.group_master);
            this.my_group_name = itemView.findViewById(R.id.my_group_name);
            this.max_people = itemView.findViewById(R.id.max_people);
            this.now_people = itemView.findViewById(R.id.now_people);
            this.group_text = itemView.findViewById(R.id.group_text);
            itemView.findViewById(R.id.group_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    group_view();

                }

            });

        }
        public void group_view(){
            Intent intent = new Intent(context.getApplicationContext(), GroupView.class);
            intent.putExtra("group_id",(String)group_id.getText());
            context.startActivity(intent);
        }
    }
}
