package adapter1;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import Info.Memberlist;
import kr.ac.yeonsung.ksj.ex1.R;

public class MemberListAdapter2 extends RecyclerView.Adapter<MemberListAdapter2.CustomViewHolder> {
    private ArrayList<Memberlist> member_list;
    private ArrayList<HashMap<String,Object>> ma = new ArrayList<>();
    private Context context;
    public MemberListAdapter2(ArrayList<Memberlist> member_list, Context context) {
        this.member_list = member_list;
        this.context = context;
    }



    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_member2, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.member_name.setText(member_list.get(position).getMemberName());

    }

    @Override
    public int getItemCount() {

        return (member_list != null ? member_list.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView member_name;
        CheckBox checkBox;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.member_name = itemView.findViewById(R.id.memberName);
            this.checkBox = itemView.findViewById(R.id.check);

        }

    }
}
