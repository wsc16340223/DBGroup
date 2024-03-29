package dbms.smms.MySQL;

public class User {
    private int uid;
    private String uname;

    public User(int uid, String uname){
        this.uid = uid;
        this.uname = uname;
    }

    public User(String name) {this.uname = name;}

    public int getUid() {
        return uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }
}
