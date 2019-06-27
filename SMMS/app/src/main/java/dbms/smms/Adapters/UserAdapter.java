package dbms.smms.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dbms.smms.MySQL.User;
import dbms.smms.R;

public class UserAdapter extends BaseAdapter {
    private List<User> myList = new ArrayList<>();

    static class ViewHolder {
        TextView userId, userName;
        public ViewHolder(View view){
            userId = view.findViewById(R.id.userId);
            userName = view.findViewById(R.id.userName);
        }
    }

    public UserAdapter(){
        super();
    }

    public UserAdapter(List<User> list){
        super();
        myList = list;
    }

    @Override
    public int getCount(){
        if (myList == null)
            return 0;
        return myList.size();
    }

    @Override
    public long getItemId(int i) {return i;}

    @Override
    public User getItem(int i){
        if (myList == null)
            return null;
        return  myList.get(i);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup){
        ViewHolder viewHolder = null;
        // 当view为空时才加载布局，否则，直接修改内容
        if (convertView == null){
            // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_item, null);
            viewHolder = new ViewHolder(convertView);
            //绑定ViewHolder对象
            convertView.setTag(viewHolder);
        }
        else {
            //取出ViewHolder对象
            viewHolder =(ViewHolder)convertView.getTag();
        }

        User user = myList.get(i);
        //设置TextView显示的内容
        viewHolder.userId.setText(""+user.getUid());
        viewHolder.userName.setText(user.getUname());

        return convertView;
    }

    public void add(User user){
        myList.add(user);
        notifyDataSetChanged();
    }

    public void remove(int i){
        myList.remove(i);
        notifyDataSetChanged();;
    }

    public void clear(){
        myList.clear();
        notifyDataSetChanged();
    }
}
