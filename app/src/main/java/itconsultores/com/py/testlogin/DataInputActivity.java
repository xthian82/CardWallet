package itconsultores.com.py.testlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DataInputActivity extends AppCompatActivity {
    public static final String PAY_DATA_TAG = "paymentData";
    private UserData mUserData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_input);

        Button button = (Button)findViewById(R.id.button_payment);
        mUserData = (UserData) getIntent().getSerializableExtra(CardReadActivity.USER_DATA_TAG);
        TextView txUserCardId = (TextView) findViewById(R.id.user_card_id);
        TextView txUserName = (TextView) findViewById(R.id.user_data_name);
        TextView txUserCell = (TextView) findViewById(R.id.user_data_cell);
        final EditText amountText = (EditText)findViewById(R.id.amount_charge);

        txUserCardId.setText("CardID: " + mUserData.getCardId());
        txUserName.setText("USER  : " + mUserData.getCardName());
        txUserCell.setText("LINEA : " + mUserData.getCardPhone());


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amountText.getText() == null || "".equals(amountText.getText())) {
                    return;
                }
                double charge = Double.parseDouble(amountText.getText().toString());
                confirmPayment(charge);
            }
        });
    }

    private void confirmPayment(double charge) {
        Payment payment = new Payment(mUserData);
        payment.setAmount(charge);

        Intent confirmIntent = new Intent(this, ConfirmActivity.class);
        confirmIntent.putExtra(DataInputActivity.PAY_DATA_TAG, payment);
        startActivity(confirmIntent);
    }
}
