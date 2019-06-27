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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dbms.smms.Adapters.CarryAdapter;
import dbms.smms.MySQL.Carry;
import dbms.smms.MySQL.MySQL;
import dbms.smms.R;

public class ShowCarry extends Fragment {
    List<Carry> carryList = new ArrayList<>();
    CarryAdapter carryAdapter;
    EditText editId, editDate;
    Button search;
    ListView listView;
    MySQL mySQL;
    String id, date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.actor_table, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        listView = getView().findViewById(R.id.a_t_list);
        mySQL = new MySQL(this.getActivity());

        myQuery();
        myListView();
    }

    public void myQuery(){
        editId = getView().findViewById(R.id.a_t_id);
        editDate = getView().findViewById(R.id.a_t_date);
        search = getView().findViewById(R.id.a_t_search);

        //先列出所有结果
        queryCarry("", "");

        //查询事件
        search.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                id = editId.getText().toString();
                date = editDate.getText().toString();
                queryCarry(id, date);
            }
        });
    }

    //对uid和操作时间进行查询
    public void queryCarry(String id, String date) {
        carryList = mySQL.queryCarry(id, date);
        //返回结果为空，则先清空当前的listView，然后提示
        if (carryList == null || carryList.isEmpty()) {
            if (listView.getCount() > 0){
                carryAdapter.clear();
                listView.setAdapter(carryAdapter);
            }
            Toast.makeText(getContext(), "无查询结果!", Toast.LENGTH_SHORT).show();
        }
        else {
            carryAdapter = new CarryAdapter(carryList);
            listView.setAdapter(carryAdapter);
            Toast.makeText(getContext(), "查询成功！", Toast.LENGTH_SHORT).show();
        }
    }

    //listView的长按时间，删除对应条目
    public void myListView(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                dialog.setTitle("删除").setMessage("是否删除出入库记录?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //先删除数据库中条目，再删除listView的条目
                        mySQL.deleteCarry(position+1);
                        carryAdapter.remove(position);
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
