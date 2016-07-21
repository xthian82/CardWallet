package itconsultores.com.py.testlogin;

import java.io.Serializable;

/**
 * @author jerry
 */
public class UserData implements Serializable {
    private String cardId;
    private String cardName;
    private String cardPhone;

    public UserData() {
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardPhone() {
        return cardPhone;
    }

    public void setCardPhone(String cardPhone) {
        this.cardPhone = cardPhone;
    }
}
