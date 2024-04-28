package adapter1;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Activity.ReservationView;
import Info.MyReservationInfo;
import kr.ac.yeonsung.ksj.ex1.R;

public class My_ReservationAdapter extends RecyclerView.Adapter<My_ReservationAdapter.CustomViewHolder> {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<MyReservationInfo> arrayList_my_reservaion;
    private Context context;

    public My_ReservationAdapter(ArrayList<MyReservationInfo> arrayList_my_reservaion, Context context) {
        this.arrayList_my_reservaion = arrayList_my_reservaion;
        this.context = context;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_my_reservation, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.room_id.setText(arrayList_my_reservaion.get(position).getRoomId());
        holder.reservation_id.setText(arrayList_my_reservaion.get(position).getReservationId());
        holder.reservation_day.setText(arrayList_my_reservaion.get(position).getDay());
        holder.start_time.setText(arrayList_my_reservaion.get(position).getStartTime());
        holder.reservation_room.setText(arrayList_my_reservaion.get(position).getRoom_name());

    }

    @Override
    public int getItemCount() {

        return (arrayList_my_reservaion != null ? arrayList_my_reservaion.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView reservation_room;
        TextView reservation_id;
        TextView start_time;
        TextView reservation_day;
        TextView room_id;
        LinearLayout reservation_view;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.reservation_room = itemView.findViewById(R.id.reservation_room);
            this.reservation_id = itemView.findViewById(R.id.reservation_id);
            this.reservation_day = itemView.findViewById(R.id.reservation_day);
            this.start_time = itemView.findViewById(R.id.start_time);
            this.room_id = itemView.findViewById(R.id.room_id);
            this.reservation_view = itemView.findViewById(R.id.reservation_view);
            reservation_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reservationGo();
                }
            });


        }
        public void reservationGo() {
            Intent intent = new Intent(context.getApplicationContext(), ReservationView.class);
            intent.putExtra("reservation_id",(String)reservation_id.getText());
            intent.putExtra("room_id",(String)room_id.getText());
            intent.putExtra("reservation_day", (String) reservation_day.getText());
            intent.putExtra("start_time", (String) start_time.getText());
            intent.putExtra("room_name",(String)reservation_room.getText());
            context.startActivity(intent);
        }
    }
}
