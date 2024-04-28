package adapter1;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import Info.GroupInfo;
import kr.ac.yeonsung.ksj.ex1.R;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.CustomViewHolder> {

    private ArrayList<GroupInfo> arrayList_group;
    private Context context;
    String master_Email;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference docRef;
    public GroupAdapter(ArrayList<GroupInfo> arrayList_group, Context context) {
        this.arrayList_group = arrayList_group;
        this.context = context;
    }



    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_group, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.group_name.setText(arrayList_group.get(position).getGroup_name());
        holder.group_text.setText(arrayList_group.get(position).getGroup_text());
        holder.now_people.setText(arrayList_group.get(position).getNow_people().toString());
        holder.max_people.setText(arrayList_group.get(position).getMax_people().toString());
        holder.group_id.setText(arrayList_group.get(position).getGroup_id());
        if(Long.parseLong((String)holder.now_people.getText()) > Long.parseLong((String)holder.max_people.getText())/2){
            holder.now_people.setTextColor(Color.RED);
        }else{
            holder.now_people.setTextColor(androidx.browser.R.color.browser_actions_text_color);
        }
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

        return (arrayList_group != null ? arrayList_group.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        LinearLayout group_view;
        TextView group_name;
        TextView group_text;
        TextView max_people;
        TextView now_people;
        TextView group_id;
        TextView group_master;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.group_master = itemView.findViewById(R.id.group_master);
            this.group_view = itemView.findViewById(R.id.group_view);
            this.group_id = itemView.findViewById(R.id.group_id);
            this.group_name = itemView.findViewById(R.id.group_name);
            this.group_text = itemView.findViewById(R.id.group_text);
            this.max_people = itemView.findViewById(R.id.max_people);
            this.now_people = itemView.findViewById(R.id.now_people);

            group_view.setOnClickListener(new View.OnClickListener() {
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
