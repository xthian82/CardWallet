package itconsultores.com.py.testlogin;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ConfirmActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        TextView textView = (TextView)findViewById(R.id.textView);

        Payment mPayment = (Payment) getIntent().getSerializableExtra(DataInputActivity.PAY_DATA_TAG);
        textView.setText( "Confirmar pago de " + mPayment.getAmount() + " a " + mPayment.getUserData().getCardName() + "?");
    }
}
