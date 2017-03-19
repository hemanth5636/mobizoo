package pojo;

/**
 * Created by mailt on 3/19/2017.
 */

public class BankListPojo {
    String holder_name, bank_name, account_no;
    boolean account_state;

    public BankListPojo(String holder_name, String bank_name, String account_no, boolean account_state) {
        this.holder_name = holder_name;
        this.bank_name = bank_name;
        this.account_no = account_no;
        this.account_state = account_state;
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

    public boolean isAccount_state() {
        return account_state;
    }

    public void setAccount_state(boolean account_state) {
        this.account_state = account_state;
    }
}
