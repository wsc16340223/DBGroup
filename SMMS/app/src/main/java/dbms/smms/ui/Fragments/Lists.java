package dbms.smms.ui.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dbms.smms.Adapters.GoodsAdapter;
import dbms.smms.Adapters.UserAdapter;
import dbms.smms.MySQL.Goods;
import dbms.smms.MySQL.MySQL;
import dbms.smms.MySQL.User;
import dbms.smms.R;

public class Lists extends Fragment {
    List<User> userList = new ArrayList<>();
    List<Goods> goodsList = new ArrayList<>();
    GoodsAdapter goodsAdapter = new GoodsAdapter();
    UserAdapter userAdapter = new UserAdapter();
    ListView listView;
    MySQL mySQL;
    RadioGroup radioGroup;
    EditText editText;
    Button listSearch;

    String name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        toDo();
        myListView();
    }

    public void init(){
        radioGroup = getView().findViewById(R.id.radioGroupInList);
        editText = getView().findViewById(R.id.searchName);
        listSearch = getView().findViewById(R.id.listSearch);
        listView = getView().findViewById(R.id.listView);
        mySQL = new MySQL(this.getActivity());
    }

    public void toDo(){
        //刚进入页面时，需要先查询物品显示以下，不然第一次进入页面不会显示结果
        doQueryGoods();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.chooseGoods:
                        doQueryGoods();
                        break;
                    case R.id.chooseUser:
                        doQueryUser();
                        break;
                }
            }
        });
    }

    public void doQueryGoods(){
        editText.setText("");
        queryGoods("");

        listSearch.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                name = editText.getText().toString();
                queryGoods(name);
            }
        });
    }

    public void doQueryUser(){
        editText.setText("");
        queryUser("");

        listSearch.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                name = editText.getText().toString();
                queryUser(name);
            }
        });
    }

    //对goods表进行查询
    public void queryGoods(String name){
        goodsList = mySQL.queryGoodsName(name);
        if (goodsList == null || goodsList.isEmpty()){
            if (listView.getCount() > 0){
                goodsAdapter.clear();
                listView.setAdapter(goodsAdapter);
            }
            Toast.makeText(getContext(), "无查询结果!", Toast.LENGTH_SHORT).show();
        }
        else {
            goodsAdapter = new GoodsAdapter(goodsList);
            listView.setAdapter(goodsAdapter);
            Toast.makeText(getContext(), "查询成功！", Toast.LENGTH_SHORT).show();
        }
    }

    //对user表进行查询
    public void queryUser(String name) {
        userList = mySQL.queryUserName(name);
        if (userList == null || userList.isEmpty()){
            if (listView.getCount() > 0){
                userAdapter.clear();
                listView.setAdapter(userAdapter);
            }
            Toast.makeText(getContext(), "无查询结果!", Toast.LENGTH_SHORT).show();
        }
        else {
            userAdapter = new UserAdapter(userList);
            listView.setAdapter(userAdapter);
            Toast.makeText(getContext(), "查询成功！", Toast.LENGTH_SHORT).show();
        }
    }

    public void myListView(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this.getActivity());
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                dialog.setTitle("删除").setMessage("是否删除?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(radioGroup.getCheckedRadioButtonId()){
                            case R.id.chooseGoods:
                                mySQL.deleteGoods(position+1);
                                goodsAdapter.remove(position);
                                break;
                            case R.id.chooseUser:
                                mySQL.deleteUser(position+1);
                                userAdapter.remove(position);
                                break;
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
                return true;
            }
        });
    }

}
