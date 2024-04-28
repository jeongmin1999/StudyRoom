package adapter1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import Info.ChatInfo;
import kr.ac.yeonsung.ksj.ex1.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.CustomViewHolder> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<ChatInfo> arrayList;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String Myname;
    public ChatAdapter(ArrayList<ChatInfo> arrayList, String Myname) {
        this.arrayList = arrayList;
        this.Myname = Myname;
    }



    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
        }

        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }
    @Override
    public int getItemViewType(int position){
        Myname = user.getEmail();
            if(arrayList.get(position).getChat_email().equals(Myname)){
                return 1;
            } else {
                return 2;
            }
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        holder.chat_name.setText(arrayList.get(position).getChat_name());
        holder.chat_text.setText(arrayList.get(position).getChat_text());
        holder.chat_date.setText(arrayList.get(position).getChat_date());
        holder.chat_email.setText(arrayList.get(position).getChat_email());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView chat_name;
        TextView chat_text;
        TextView chat_date;
        TextView chat_email;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.chat_name = itemView.findViewById(R.id.chat_name);
            this.chat_text = itemView.findViewById(R.id.chat_text);
            this.chat_date = itemView.findViewById(R.id.chat_date);
            this.chat_email = itemView.findViewById(R.id.chat_email);


        }
    }

}
