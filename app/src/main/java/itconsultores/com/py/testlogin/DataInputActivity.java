package itconsultores.com.py.testlogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class DataInputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_input);

        Button button = (Button)findViewById(R.id.button_payment);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPayment();
            }
        });
    }

    private void confirmPayment() {
        Intent confirmIntent = new Intent(this, ConfirmActivity.class);
        startActivity(confirmIntent);
    }
}
