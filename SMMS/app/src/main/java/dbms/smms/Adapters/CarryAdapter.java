package dbms.smms.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dbms.smms.MySQL.Carry;
import dbms.smms.R;

public class CarryAdapter extends BaseAdapter {
    private List<Carry> myList = new ArrayList<>();

    static class ViewHolder {
        TextView carryId, carryDate, carryType, carryName, carryNum, carryPrice;

        public ViewHolder(View view){
            carryId = view.findViewById(R.id.carryId);
            carryDate = view.findViewById(R.id.carryDate);
            carryType = view.findViewById(R.id.carryType);
            carryName = view.findViewById(R.id.carryName);
            carryNum = view.findViewById(R.id.carryNum);
            carryPrice = view.findViewById(R.id.carryPrice);
        }
    }

    public CarryAdapter(){
        super();
    }
    public CarryAdapter(List<Carry> list){
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
    public Carry getItem(int i){
        if (myList == null)
            return null;
        return  myList.get(i);
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup){
        ViewHolder viewHolder = null;
        // 当view为空时才加载布局，否则，直接修改内容
        if (convertView == null){
            // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.carry_item, null);
            viewHolder = new ViewHolder(convertView);
            //绑定ViewHolder对象
            convertView.setTag(viewHolder);
        }
        else {
            //取出ViewHolder对象
            viewHolder =(ViewHolder)convertView.getTag();
        }

        Carry carry = myList.get(i);
        //设置TextView显示的内容
        viewHolder.carryId.setText("操作员："+carry.getUid());
        viewHolder.carryDate.setText("日期："+carry.getDate());
        viewHolder.carryType.setText(carry.getType());
        viewHolder.carryName.setText(carry.getGname());
        viewHolder.carryNum.setText("×"+carry.getCount());
        viewHolder.carryPrice.setText("¥" + carry.getPrice());

        return convertView;
    }

    public void add(Carry carry){
        myList.add(carry);
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
