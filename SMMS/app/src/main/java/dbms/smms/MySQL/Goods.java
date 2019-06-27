package dbms.smms.MySQL;

public class Goods {
    private int gid, count;
    private String gname;
    private float price;

    public Goods(int gid, String gname, int count, float price){
        this.gid = gid;
        this.gname = gname;
        this.count = count;
        this.price = price;
    }

    public Goods(String gname, int count, float price){
        this.gname = gname;
        this.count = count;
        this.price = price;
    }

    public int getGid(){return gid;}
    public String getGname(){return gname;}
    public int getCount(){return count;}
    public float getPrice(){return price;}

    public void setGid(int gid) { this.gid = gid; }
    public void setGname(String gname){this.gname = gname;}
    public void setCount(int count) {this.count = count;}
    public void setPrice(float price){this.price = price;}
}
