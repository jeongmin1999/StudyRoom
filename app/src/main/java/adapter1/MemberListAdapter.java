package adapter1;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import Info.Memberlist;
import kr.ac.yeonsung.ksj.ex1.R;

public class MemberListAdapter extends BaseAdapter {

    private List<Memberlist> member_list;
    public MemberListAdapter(List<Memberlist> member_list) {
        this.member_list = member_list;

    }
    @NonNull
    @Override
    public int getCount() {

        return member_list.size();
    }
    @Override
    public Object getItem(int i){
        return member_list.get(i);
    }
    @Override
    public long getItemId(int i){
        return i;
    }
    @Override
    public View getView(int i, View ConvertView, ViewGroup viewGroup) {
        ConvertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_member, viewGroup, false);
        TextView memberName = ConvertView.findViewById(R.id.memberName);
        Memberlist memberlist = member_list.get(i);
        memberName.setText(memberlist.getMemberName());

        return ConvertView;
    }

}
