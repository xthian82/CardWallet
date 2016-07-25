package itconsultores.com.py.testlogin;

import java.io.Serializable;

/**
 * Created by jerry on 24/07/2016.
 */
public class Payment implements Serializable {

    private UserData userData;
    private double amount;

    public Payment(UserData userData) {
        this.userData = userData;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {

        return amount;
    }

    public UserData getUserData() {
        return userData;
    }
}
