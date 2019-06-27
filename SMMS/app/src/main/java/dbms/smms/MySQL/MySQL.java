package dbms.smms.MySQL;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MySQL extends SQLiteOpenHelper {
    private static final String DB_NAME = "SMMS.db";
    private static final int DB_VERSION = 1;
    private static final String SQL_CREATE_TABLE_USER = "create table user (uid integer primary key not null, uname text not null);";
    private static final String SQL_CREATE_TABLE_GOODS = "create table goods (gid integer primary key autoincrement not null, gname text not null , count integer not null, price text not null, unique(gname, price));";
    private static final String SQL_CREATE_TABLE_CARRY = "create table carry (cid integer primary key autoincrement not null, uid integer references user(uid), gname text not null, count integer not null ,price text not null, date text not null ,type text not null);";

    public MySQL(Context c){
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_TABLE_USER);
        db.execSQL(SQL_CREATE_TABLE_GOODS);
        db.execSQL(SQL_CREATE_TABLE_CARRY);

        initData(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void initData(SQLiteDatabase db){
        db.execSQL("insert into user values (null, '总经理')");
        db.execSQL("insert into user values (null, '主任')");
        db.execSQL("insert into user values (null, '小王')");
        db.execSQL("insert into user values (null, '小谢')");

        db.execSQL("insert into goods values (null, '苹果', 100, 6.0)");
        db.execSQL("insert into goods values (null, '橘子', 50, 3.5)");
        db.execSQL("insert into goods values (null, '香蕉', 30, 3.0)");
        db.execSQL("insert into goods values (null, '西瓜', 200, 4.0)");
        db.execSQL("insert into goods values (null, '哈密瓜', 100, 8.3)");

    }

    //删除goods表中对应记录
    public void deleteGoods(Integer position){
        SQLiteDatabase db = getWritableDatabase();
        String selection = "gid = ?";
        String[] selectionArgs = {position.toString()};
        db.delete("goods",selection,selectionArgs);

        Cursor c = db.rawQuery("select * from goods where gid > ?", new String[]{position.toString()});
        while (c.moveToNext()){
            int newId = c.getInt(c.getColumnIndex("gid")) - 1;
            String[] selectionArgs2 = {String.valueOf(c.getInt(c.getColumnIndex("gid")))};
            ContentValues values = new ContentValues();
            values.put("gid", newId);
            db.update("goods", values, selection, selectionArgs2);
        }
    }

    //删除user表中对应记录
    public void deleteUser(Integer position){
        SQLiteDatabase db = getWritableDatabase();
        String selection = "uid = ?";
        String[] selectionArgs = {position.toString()};
        db.delete("user",selection,selectionArgs);

        Cursor c = db.rawQuery("select * from user where uid > ?", new String[]{position.toString()});
        while (c.moveToNext()){
            int newId = c.getInt(c.getColumnIndex("uid")) - 1;
            String[] selectionArgs2 = {String.valueOf(c.getInt(c.getColumnIndex("uid")))};
            ContentValues values = new ContentValues();
            values.put("uid", newId);
            db.update("user", values, selection, selectionArgs2);
        }
    }

    //删除carry表中对应记录
    public void deleteCarry(Integer position){
        SQLiteDatabase db = getWritableDatabase();
        String selection = "cid = ?";
        String[] selectionArgs = {position.toString()};
        db.delete("carry",selection,selectionArgs);

        Cursor c = db.rawQuery("select * from carry where cid > ?", new String[]{position.toString()});
        while (c.moveToNext()){
            int newId = c.getInt(c.getColumnIndex("cid")) - 1;
            String[] selectionArgs2 = {String.valueOf(c.getInt(c.getColumnIndex("cid")))};
            ContentValues values = new ContentValues();
            values.put("cid", newId);
            db.update("carry", values, selection, selectionArgs2);
        }
    }

    //按名字搜索用户
    public ArrayList<User> queryUserName(String name){
        ArrayList<User> myUser = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("select * from user where uname like ?", new String[]{name + "%"});
        if (c.getCount() == 0){
            return null;
        }
        c.moveToFirst();
        while (!c.isAfterLast()){
            User item = new User(c.getInt(c.getColumnIndex("uid")), c.getString(c.getColumnIndex("uname")));
            myUser.add(item);
            c.moveToNext();
        }

        return myUser;
    }

    //按名字搜索物资
    public ArrayList<Goods> queryGoodsName(String name){
        ArrayList<Goods> myGoods = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("select * from goods where gname like ?", new String[]{name + "%"});
        if (c.getCount() == 0){
            return null;
        }
        c.moveToFirst();
        while (!c.isAfterLast()){
            Goods item = new Goods(c.getInt(c.getColumnIndex("gid")), c.getString(c.getColumnIndex("gname")), c.getInt(c.getColumnIndex("count")), Float.parseFloat(c.getString(c.getColumnIndex("price"))));
            myGoods.add(item);
            c.moveToNext();
        }

        return myGoods;
    }

    //按工号和物品名来搜索对应的进出货结果
    public ArrayList<Carry> queryCarry(String id, String date){
        ArrayList<Carry> myCarry = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = null;
        if (id.equals("") && date.equals("")){
            c = db.rawQuery("select * from carry", null);
        }
        else if (!id.equals("") && date.equals("")){
            c = db.rawQuery("select * from carry where uid = ?", new String[]{id});
        }
        else if (id.equals("") && !date.equals("")){
            c = db.rawQuery("select * from carry where date like ?", new String[]{date + "%"});
        }
        else {
            c = db.rawQuery("select * from carry where uid = ? and date like ?", new String[]{id, date + "%"});
        }

        if (c.getCount() == 0){
            return null;
        }
        c.moveToFirst();
        while(!c.isAfterLast()){
            Carry item = new Carry(c.getInt(c.getColumnIndex("uid")), c.getString(c.getColumnIndex("gname")), c.getInt(c.getColumnIndex("count")), Float.parseFloat(c.getString(c.getColumnIndex("price"))), c.getString(c.getColumnIndex("date")), c.getString(c.getColumnIndex("type")));
            myCarry.add(item);
            c.moveToNext();
        }

        return myCarry;
    }

    //通过name和price确定该物品是否在goods表内存在
    public int checkInGoods(String name, float price, SQLiteDatabase db){
        Cursor c = null;
        int id;
        String selection = "gname = ? and price = ?";
        String[] selectionArgs = {name, String.valueOf(price)};
        c = db.query("goods", null, selection, selectionArgs, null, null, null);

        //c = db.rawQuery("select * from goods where gname = ? and price = ?", new String[]{name, price});
        if (c.getCount() == 0)
            return -1;
        c.moveToFirst();
        id = c.getInt(c.getColumnIndex("gid"));

        return id;
    }

    //查询goods表中对应id物品的数量
    public int getGoodsCount(int id, SQLiteDatabase db){
        int count;
        Cursor c = db.rawQuery("select * from goods where gid = ?", new String[]{""+id});
        c.moveToFirst();
        count = c.getInt(c.getColumnIndex("count"));

        return count;
    }

    //goods表插入功能
    public boolean insert2Goods(Goods goods){
        SQLiteDatabase db = getWritableDatabase();
        if (goods.getCount() == 0){
            return false;
        }
        int id = checkInGoods(goods.getGname(), goods.getPrice(), db);
        //如果表中不存在对应物品，则新增一个条目
        if (id == -1){
            ContentValues cv = new ContentValues();
            Cursor c = db.rawQuery("select * from goods", null);
            int gid = c.getCount() + 1;
            cv.put("gid", gid);
            cv.put("gname", goods.getGname());
            cv.put("count", goods.getCount());
            cv.put("price", goods.getPrice());
            db.insert("goods", null, cv);
        }
        //表中已有该物品，增加数量
        else {
            ContentValues cv = new ContentValues();
            int num = getGoodsCount(id, db);
            cv.put("count", num + goods.getCount());
            db.update("goods", cv, "gid = ?", new String[]{"" + id});
        }
        db.close();
        return true;
    }

    //user表插入功能
    //因为user表是按照uid判断，name可能有重名的人，因此直接插入
    public void insert2User(String name){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("uname", name);
        db.insert("user",null,cv);
        db.close();
    }

    //carry表插入功能
    public void insert2Carry(Carry carry, SQLiteDatabase db){
        ContentValues cv2 = new ContentValues();
        cv2.put("uid", carry.getUid());
        cv2.put("gname", carry.getGname());
        cv2.put("count", carry.getCount());
        cv2.put("price", carry.getPrice());
        cv2.put("date", carry.getDate());
        cv2.put("type", carry.getType());
        db.insert("carry", null, cv2);
    }

    //carry插入操作
    public boolean insertToCarry(Carry carry){
        if (carry.getCount() == 0){
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        int id = checkInGoods(carry.getGname(), carry.getPrice(), db);
        //goods中无所需物品，拒绝出库
        if (id == -1){
            if (carry.getType().equals("出库")) {
                return false;
            }
            //新物品入库，先插入goods表，再插入carry表
            else {
                ContentValues cv1 = new ContentValues();
                Cursor c = db.rawQuery("select * from goods", null);
                int gid = c.getCount() + 1;
                cv1.put("gid", gid);
                cv1.put("gname", carry.getGname());
                cv1.put("count", carry.getCount());
                cv1.put("price", carry.getPrice());
                db.insert("goods", null, cv1);

                insert2Carry(carry, db);
//                db.execSQL("insert into goods values (null, ?, ?, ?)", new Object[]{gname, count, price});
//                db.execSQL("insert into carry values (?, ?, ?, ?, ?, ?)", new Object[]{uid, gname, count ,price, date, type});
                return true;
            }
        }
        //对于仓库中存在的物品的操作
        else {
            //出库操作
            if (carry.getType().equals("出库")) {
                int num = getGoodsCount(id, db);
                //物品数量不足，拒绝出库
                if (num < carry.getCount()){
                    return false;
                }
                //物品有余，先更新goods表数目，然后carry表插入操作记录，出库成功
                else if (num > carry.getCount()){
                    ContentValues cv = new ContentValues();
                    cv.put("count", num - carry.getCount());
                    db.update("goods", cv, "gid = ?", new String[]{"" + id});

                    insert2Carry(carry, db);
                    return true;
                }
                //物品数恰好，删除goods表对应条目，carry表记录操作，出库成功
                else {
                    db.delete("goods", "gid = ?", new String[]{"" + id});

                    insert2Carry(carry, db);
                    return true;
                }
            }
            //仓库有已有物品的入库，直接增加对应物品数量，carry表插入操作记录
            else {
                int num = getGoodsCount(id, db);
                ContentValues cv = new ContentValues();
                cv.put("count", num + carry.getCount());
                db.update("goods", cv, "gid = ?", new String[]{"" + id});

                insert2Carry(carry, db);
                return true;
            }
        }
    }
}
