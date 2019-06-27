package dbms.smms.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dbms.smms.MySQL.Goods;
import dbms.smms.R;

public class GoodsAdapter extends BaseAdapter {
    private List<Goods> myList = new ArrayList<>();

    static class ViewHolder{
        TextView goodsId, goodsName, goodsCount, goodsPrice;

        public ViewHolder(View view){
            goodsId = view.findViewById(R.id.goodsId);
            goodsName = view.findViewById(R.id.goodsName);
            goodsCount = view.findViewById(R.id.goodsCount);
            goodsPrice = view.findViewById(R.id.goodsPrice);
        }
    }

    public GoodsAdapter(){
        super();
    }

    public GoodsAdapter(List<Goods> list){
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
    public Goods getItem(int i){
        if (myList == null)
            return null;
        return  myList.get(i);
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        // 当view为空时才加载布局，否则，直接修改内容
        if (convertView == null) {
            // 通过inflate的方法加载布局，context需要在使用这个Adapter的Activity中传入。
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.goods_item, null);
            viewHolder = new ViewHolder(convertView);
            //绑定ViewHolder对象
            convertView.setTag(viewHolder);
        } else {
            //取出ViewHolder对象
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Goods goods = myList.get(i);
        //设置TextView显示的内容
        viewHolder.goodsId.setText(""+goods.getGid());
        viewHolder.goodsName.setText(goods.getGname());
        viewHolder.goodsCount.setText("×"+goods.getCount());
        viewHolder.goodsPrice.setText("¥" + goods.getPrice());

        return convertView;
    }

    public void add(Goods goods){
        myList.add(goods);
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
