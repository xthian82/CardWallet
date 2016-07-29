package itconsultores.com.py.testlogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = getIntent();

        TextView txMessage = (TextView) findViewById(R.id.message);
        String message = intent.getStringExtra("message");
        txMessage.setText(message);

        Button newTransaction = (Button) findViewById(R.id.new_transaction);
        newTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cardReadIntent = new Intent(getApplicationContext(), CardReadActivity.class);
                startActivity(cardReadIntent);
            }
        });


    }
}
