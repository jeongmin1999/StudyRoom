package adapter1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Activity.ReservationAdd;
import Info.RoomInfo;
import kr.ac.yeonsung.ksj.ex1.R;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.CustomViewHolder> {

    private ArrayList<RoomInfo> arrayList;
    private Context context;
    private FirebaseFirestore firebaseFirestore;

    public RoomAdapter(ArrayList<RoomInfo> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }



    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_room, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(arrayList.get(position).getRoom_image())
                .into(holder.room_image);
        holder.room_num.setText(arrayList.get(position).getRoom_num());
        holder.max_people.setText(arrayList.get(position).getMax_people().toString());
        holder.min_people.setText(arrayList.get(position).getMin_people().toString());
        holder.room_id.setText(arrayList.get(position).getRoom_id());
    }

    @Override
    public int getItemCount() {

        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView room_image;
        TextView room_num;
        TextView max_people;
        TextView min_people;
        TextView room_id;
        Button reservation_btn;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);;
            this.reservation_btn = itemView.findViewById(R.id.reservation_btn);
            this.room_image = itemView.findViewById(R.id.room_image);
            this.room_num = itemView.findViewById(R.id.room_num);
            this.max_people = itemView.findViewById(R.id.max_people);
            this.min_people = itemView.findViewById(R.id.min_people);
            this.room_id = itemView.findViewById(R.id.room_id);

            reservation_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {reservation(); }
            });
        }
        public void reservation(){
            Intent intent = new Intent(context.getApplicationContext(), ReservationAdd.class);
            intent.putExtra("room_num",(String)room_num.getText());
            intent.putExtra("room_id",(String)room_id.getText());
            context.startActivity(intent);
        }
    }
}
