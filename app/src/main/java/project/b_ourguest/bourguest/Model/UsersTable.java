package project.b_ourguest.bourguest.Model;

/**
 * Created by Robbie on 30/01/2015.
 */
public class UsersTable {


    private String id;
    private String password;
    private boolean accountVerified;

    public UsersTable(String id,String p,boolean v)
    {
        this.id = id;
        this.password = p;
        this.accountVerified = v;
    }

    public boolean isAccountVerified() {
        return accountVerified;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
