package itconsultores.com.py.testlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class DataInputActivity extends AppCompatActivity {

    public static final String PAY_DATA_TAG = "paymentData";
    private UserData mUserData = null;
    public static final String AMOUNT_CHARGE_TAG = "amountCharger";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_input);

        setTitle(getResources().getString(R.string.app_name)+" - ****753");

        Button button = (Button)findViewById(R.id.button_payment);
        mUserData = (UserData) getIntent().getSerializableExtra(CardReadActivity.USER_DATA_TAG);
        //TextView txUserCardId = (TextView) findViewById(R.id.user_card_id);
        //TextView txUserName = (TextView) findViewById(R.id.user_data_name);
        //TextView txUserCell = (TextView) findViewById(R.id.user_data_cell);
        final EditText amountText = (EditText)findViewById(R.id.amount_charge);

        //txUserCardId.setText("CardID: " + mUserData.getCardId());
        //txUserName.setText("USER  : " + mUserData.getCardName());
        //txUserCell.setText("LINEA : " + mUserData.getCardPhone());


        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amountText.getText() == null || "".equals(amountText.getText())) {
                    return;
                }
                double charge = Double.parseDouble(amountText.getText().toString());
                confirmPayment(charge);
            }
        });*/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPayment();
            }
        });

        final TextView txAmount_charge = (TextView) findViewById(R.id.amount_charge);
        txAmount_charge.setFocusable(false);

        ((Button) findViewById(R.id.Btn1_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed(txAmount_charge, 1);
            }
        });
        ((Button) findViewById(R.id.Btn2_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed(txAmount_charge,2);
            }
        });
        ((Button) findViewById(R.id.Btn3_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed(txAmount_charge,3);
            }
        });
        ((Button) findViewById(R.id.Btn4_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed(txAmount_charge,4);
            }
        });
        ((Button) findViewById(R.id.Btn5_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed(txAmount_charge,5);
            }
        });
        ((Button) findViewById(R.id.Btn6_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed(txAmount_charge,6);
            }
        });
        ((Button) findViewById(R.id.Btn7_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed(txAmount_charge,7);
            }
        });
        ((Button) findViewById(R.id.Btn8_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed(txAmount_charge,8);
            }
        });
        ((Button) findViewById(R.id.Btn9_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed(txAmount_charge,9);
            }
        });
        ((Button) findViewById(R.id.Btnzero_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed(txAmount_charge, 0);
            }
        });
        ((Button) findViewById(R.id.Btn3zero_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed(txAmount_charge, 999);
            }
        });

        ((Button) findViewById(R.id.Btnclear_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txAmount_charge.setText("");
            }
        });
    }

    private void keyPressed(TextView tv,int key){

        String current=tv.getText().toString();
        String newAmount="";

        if(key==999){
            newAmount=current.replace(".","") + "000";
        }else{
            newAmount=current.replace(".","") + String.valueOf(key);
        }


        if(newAmount.length()==0)
        {
            return;
        }

        try {

            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator(',');
            symbols.setGroupingSeparator('.');
            double amount = Double.parseDouble(newAmount);
            DecimalFormat formatter = new DecimalFormat("#,###",symbols);
            newAmount=formatter.format(amount);

        }catch (Exception e){
            Log.e("", e.getMessage());
        }

        tv.setText(newAmount);

    }

    private void confirmPayment() {

        TextView txAmount_charge = (TextView) findViewById(R.id.amount_charge);

        String amount=txAmount_charge.getText().toString();
        amount=amount.replace(".","");

        if(amount.length()==0 || Double.parseDouble(amount)==0){
                Toast.makeText(getApplicationContext(), "Ingrese monto correcto", Toast.LENGTH_LONG).show();
                return;
        }

        Intent confirmIntent = new Intent(getApplicationContext(), ConfirmActivity.class);
        confirmIntent.putExtra(AMOUNT_CHARGE_TAG, txAmount_charge.getText().toString());
        startActivity(confirmIntent);

    }

    /*private void confirmPayment(double charge) {
        Payment payment = new Payment(mUserData);
        payment.setAmount(charge);

        Intent confirmIntent = new Intent(this, ConfirmActivity.class);
        confirmIntent.putExtra(DataInputActivity.PAY_DATA_TAG, payment);
        startActivity(confirmIntent);
    }*/
}
