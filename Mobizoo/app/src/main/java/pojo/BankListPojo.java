package pojo;

/**
 * Created by mailt on 3/19/2017.
 */

public class BankListPojo {
    String holder_name, bank_name, account_no, id, resource_url, user;
    int account_state;

    public BankListPojo(String holder_name, String bank_name, String account_no, int account_state, String id, String resource_url, String user) {
        this.holder_name = holder_name;
        this.bank_name = bank_name;
        this.account_no = account_no;
        this.account_state = account_state;
        this.id = id;
        this.resource_url = resource_url;
        this.user = user;
    }

    public BankListPojo() {
    }

    public String getHolder_name() {
        return holder_name;
    }

    public void setHolder_name(String holder_name) {
        this.holder_name = holder_name;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public int isAccount_state() {
        return account_state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResource_url() {
        return resource_url;
    }

    public void setResource_url(String resource_url) {
        this.resource_url = resource_url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setAccount_state(int account_state) {
        this.account_state = account_state;
    }
}
