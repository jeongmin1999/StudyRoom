package Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import Info.My_GroupInfo;
import adapter1.My_GroupAdapter;
import kr.ac.yeonsung.ksj.ex1.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link My_GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class My_GroupFragment extends Fragment {
    private static final String TAG = "My_Group_Activity";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<My_GroupInfo> arrayList_my_group = new ArrayList<>();
    private HashMap<String, HashMap<String, Object>> hash = new HashMap<>();
    private FirebaseFirestore db;
    private String group_id;
    private Long max_people;
    private Long now_people;
    private String group_name;
    private String group_text;
    private Spinner spinner;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefresh;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public My_GroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeamFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static My_GroupFragment newInstance(String param1, String param2) {
        My_GroupFragment fragment = new My_GroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_my_group, container, false);
        recyclerView = rootView.findViewById(R.id.recyclerView_my_group);
        recyclerView.setHasFixedSize(true);
        searchView = rootView.findViewById(R.id.searchView);
        swipeRefresh = rootView.findViewById(R.id.swipeRefresh);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        spinner = rootView.findViewById(R.id.filter);
        db = FirebaseFirestore.getInstance();
        searchView.setOnQueryTextListener(searchListener);
        filter();
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                db.collection("group")
                        .orderBy("reg_date", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    arrayList_my_group.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        group_id = document.getId();
                                        group_name = (String)document.get("group_name");
                                        group_text = (String)document.get("group_text");
                                        max_people = (Long)document.get("max_people");
                                        now_people = (Long)document.get("now_people");
                                        hash = (HashMap<String, HashMap<String, Object>>) document.getData().get("users");
                                        if(hash.containsKey(user.getEmail())==true) {
                                            My_GroupInfo my_groupInfo = new My_GroupInfo(group_name, max_people, now_people, group_id, group_text);
                                            arrayList_my_group.add(my_groupInfo);
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });
                adapter = new My_GroupAdapter(arrayList_my_group,getActivity());
                recyclerView.setAdapter(adapter);
                swipeRefresh.setRefreshing(false);
            }
        });

        return rootView;
    }
    SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (s.isEmpty() || s.equals("")) {
                filter("reg_date","DESCENDING");
            } else {
                db.collection("group")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    arrayList_my_group.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String str = "";
                                        group_id = document.getId();
                                        group_name = (String) document.get("group_name");
                                        max_people = (Long) document.get("max_people");
                                        now_people = (Long) document.get("now_people");
                                        group_text = (String)document.get("group_text");
                                        hash = (HashMap<String, HashMap<String, Object>>) document.getData().get("users");
                                        if(hash.containsKey(user.getEmail())==true) {
                                            My_GroupInfo my_groupInfo = new My_GroupInfo(group_name, max_people, now_people, group_id, group_text);
                                            char[] char2 = document.getData().get("group_name").toString().toCharArray();
                                            for(int i=0; i<char2.length;i++){
                                                str += char2[i];
                                                if(s.equals(str)) {
                                                    arrayList_my_group.add(my_groupInfo);
                                                }
                                            }
                                        }
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        });
                adapter = new My_GroupAdapter(arrayList_my_group, getActivity());
                recyclerView.setAdapter(adapter);
            }
            return true;
        }
    };
    public void filter(String key, String order){
        db.collection("group")
                .orderBy(key, Query.Direction.valueOf(order))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            arrayList_my_group.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                group_id = document.getId();
                                group_name = (String)document.get("group_name");
                                group_text = (String)document.get("group_text");
                                max_people = (Long)document.get("max_people");
                                now_people = (Long)document.get("now_people");
                                hash = (HashMap<String, HashMap<String, Object>>) document.getData().get("users");
                                if(hash.containsKey(user.getEmail())==true) {
                                    My_GroupInfo my_groupInfo = new My_GroupInfo(group_name, max_people, now_people, group_id, group_text);
                                    arrayList_my_group.add(my_groupInfo);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        adapter = new My_GroupAdapter(arrayList_my_group,getActivity());
        recyclerView.setAdapter(adapter);
    }
    public void filter(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                switch(adapterView.getSelectedItem().toString()){
                    case "최근":
                        filter("reg_date","DESCENDING");
                        break;
                    case "오래된":
                        filter("reg_date","ASCENDING");
                        break;
                    case "이름-오름차순":
                        filter("group_name","ASCENDING");
                        break;
                    case "이름-내림차순":
                        filter("group_name","DESCENDING");
                        break;
                    default:
                        filter("reg_date","DESCENDING");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}