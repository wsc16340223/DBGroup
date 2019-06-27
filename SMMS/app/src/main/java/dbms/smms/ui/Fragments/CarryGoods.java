package dbms.smms.ui.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.TimeZone;

import dbms.smms.MySQL.Carry;
import dbms.smms.MySQL.Goods;
import dbms.smms.MySQL.MySQL;
import dbms.smms.MySQL.User;
import dbms.smms.R;

public class CarryGoods extends Fragment {
    static final String INPUT = "入库";
    static final String OUTPUT = "出库";
    private static DecimalFormat df = new DecimalFormat("#0.00");

    EditText editId, editName, editCount, editPrice;
    RadioGroup radioGroup, inOut;
    TextView textView;
    RadioButton  chooseIn, chooseOut;
    Button commit, clear;
    MySQL mySQL;
    LinearLayout addId, addCount, addPrice;

    String name, date, type;
    Integer id, count;
    Float price;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.actor, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        //因为使用的监听事件只在checkid变换的时候才起作用，所以在第一次进入页面的时候，
        //做出入库操作不会相应，因此先添加一个出入库的操作事件
        doCarry();
        toDO();
    }

    public void init(){
        mySQL = new MySQL(this.getActivity());
        editId = getView().findViewById(R.id.actor_id);
        editName = getView().findViewById(R.id.actor_name);
        editCount = getView().findViewById(R.id.actor_count);
        editPrice = getView().findViewById(R.id.actor_price);
        radioGroup = getView().findViewById(R.id.radioGroup);
        inOut = getView().findViewById(R.id.inOut_actor);
        textView = getView().findViewById(R.id.textView);
        chooseIn = getView().findViewById(R.id.input_goods);
        chooseOut = getView().findViewById(R.id.output_goods);
        commit = getView().findViewById(R.id.actor_commit);
        clear = getView().findViewById(R.id.actor_clear);
        addId = getView().findViewById(R.id.addIdLinear);
        addCount = getView().findViewById(R.id.addCountLinear);
        addPrice = getView().findViewById(R.id.addPriceLinear);

        //对金额的输入规范化
        editPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String money = s.toString();
                try {
                    if (TextUtils.isEmpty(money)) {
                        money = "0.00";
                    } else {
                        money = df.format(Double.valueOf(money));
                    }
                } catch (NumberFormatException e) {
                    //避免输入多余的小数点
                    editPrice.setText(money.substring(0, money.length() - 1));
                    editPrice.setSelection(editPrice.length());
                }
                if (s.toString().contains(".")) {
                    if (s.toString().indexOf(".") > 9) {
                        s = s.toString().subSequence(0, 9) + s.toString().substring(s.toString()
                                .indexOf("."));
                        editPrice.setText(s);
                        editPrice.setSelection(9);
                    }
                } else {
                    if (s.toString().length() > 9) {
                        s = s.toString().subSequence(0, 9);
                        editPrice.setText(s);
                        editPrice.setSelection(9);
                    }
                }
                // 判断小数点后只能输入两位
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editPrice.setText(s);
                        editPrice.setSelection(s.length());
                    }
                }
                //如果第一个数字为0，第二个不为点，就不允许输入
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editPrice.setText(s.subSequence(0, 1));
                        editPrice.setSelection(1);
                    }
                }
                //如果第一个输入的为点，自动在前面加0 要不会闪退
                if (s.toString().startsWith(".")) {
                    editPrice.setText("0.");
                    editPrice.setSelection(2);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editPrice.getText().toString().trim().equals("")) {
                    if (editPrice.getText().toString().trim().substring(0, 1).equals(".")) {
                        editPrice.setText(String.format("0%s", editPrice.getText().toString().trim
                                ()));
                        editPrice.setSelection(1);
                    }
                }
            }
        });
    }

    public void toDO(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.add_carry:
                        doCarry();
                        break;
                    case R.id.add_goods:
                        doAddGoods();
                        break;
                    case R.id.add_user:
                        doAddUser();
                        break;
                }
            }
        });
    }

    public void doCarry(){
        textView.setText("出入库操作");
        addId.setVisibility(View.VISIBLE);
        addCount.setVisibility(View.VISIBLE);
        addPrice.setVisibility(View.VISIBLE);
        inOut.setVisibility(View.VISIBLE);

        commit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                if (!editId.getText().toString().equals(""))
                    id = Integer.parseInt(editId.getText().toString());
                name = editName.getText().toString();
                if (!editCount.getText().toString().equals(""))
                    count = Integer.parseInt(editCount.getText().toString());
                if (!editPrice.getText().toString().equals(""))
                    price = Float.parseFloat(editPrice.getText().toString());
                date = getTime();
                if (chooseIn.isChecked())
                    type = INPUT;
                else
                    type = OUTPUT;

                //判断需要输入的项是否有空，有则提示
                if (textIsEmptyForCarry(editId.getText().toString(), name, editCount.getText().toString(), editPrice.getText().toString())){
                    Toast.makeText(getContext(), "有信息为空，请填写完毕！", Toast.LENGTH_SHORT).show();
                }
                else {
                    Carry carry = new Carry(id, name, count, price, date, type);
                    if (mySQL.insertToCarry(carry)){
                        Toast.makeText(getContext(), "操作成功！", Toast.LENGTH_SHORT).show();
                        textClear();
                    }
                    else
                        Toast.makeText(getContext(), "操作失败，请检查！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clear.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                textClear();
            }
        });

    }

    public void doAddGoods(){
        textView.setText("添加物品操作");
        addId.setVisibility(View.INVISIBLE);
        inOut.setVisibility(View.INVISIBLE);
        addCount.setVisibility(View.VISIBLE);
        addPrice.setVisibility(View.VISIBLE);

        commit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                name = editName.getText().toString();
                if (!editCount.getText().toString().equals(""))
                    count = Integer.parseInt(editCount.getText().toString());
                if (!editPrice.getText().toString().equals(""))
                    price = Float.parseFloat(editPrice.getText().toString());

                //判断需要输入的项是否有空，有则提示
                if (textIsEmptyForGoods(name, editCount.getText().toString(), editPrice.getText().toString())){
                    Toast.makeText(getContext(), "有信息为空，请填写完毕！", Toast.LENGTH_SHORT).show();
                }
                else {
                    Goods goods = new Goods(name, count, price);
                    if (mySQL.insert2Goods(goods)){
                        Toast.makeText(getContext(), "添加成功！", Toast.LENGTH_SHORT).show();
                        textClear();
                    }
                    else{
                        Toast.makeText(getContext(), "添加失败！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        clear.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                textClear();
            }
        });
    }

    public void doAddUser(){
        textView.setText("添加用户操作");
        addId.setVisibility(View.INVISIBLE);
        addCount.setVisibility(View.INVISIBLE);
        addPrice.setVisibility(View.INVISIBLE);
        inOut.setVisibility(View.INVISIBLE);

        commit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                name = editName.getText().toString();
                //判断name是否为空，有则提示
                if (name.equals("")){
                    Toast.makeText(getContext(), "有信息为空，请填写完毕！", Toast.LENGTH_SHORT).show();
                }
                else {
                    mySQL.insert2User(name);
                    Toast.makeText(getContext(), "添加成功！", Toast.LENGTH_SHORT).show();
                    textClear();
                }
            }
        });

        clear.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                textClear();
            }
        });
    }

    boolean textIsEmptyForCarry(String id, String name, String count, String price){
        if (id.isEmpty() || name.isEmpty() || count.isEmpty() || price.isEmpty())
            return true;
        else
            return false;
    }

    boolean textIsEmptyForGoods(String name, String count, String price){
        if (name.isEmpty() || count.isEmpty() || price.isEmpty())
            return true;
        else
            return false;
    }

    public void textClear(){
        editId.setText("");
        editName.setText("");
        editCount.setText("");
        editPrice.setText("");
    }

    //获取当前日期
    public String getTime(){
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String year = String.valueOf(cal.get(Calendar.YEAR));
        String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
        if (month.length() == 1)
            month = "0" + month;
        String day = String.valueOf(cal.get(Calendar.DATE));
        String time = year + "-" + month + "-" + day;
        return time;
    }
}
