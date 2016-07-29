package itconsultores.com.py.testlogin;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class ConfirmActivity extends AppCompatActivity {

    public TextView txAmountCharge;
    String amountCharge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        txAmountCharge = (TextView) findViewById(R.id.amount_charge);

        Intent intent = getIntent();

        txAmountCharge = (TextView) findViewById(R.id.amount_charge);
        amountCharge = intent.getStringExtra(DataInputActivity.AMOUNT_CHARGE_TAG);
        txAmountCharge.setText("Cobrar "+amountCharge+"?");

        ((Button) findViewById(R.id.button_confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyPressed();
            }
        });
    }

    private void keyPressed(){

        HttpRequestTask request = new HttpRequestTask();
        Toast.makeText(getApplicationContext(), "Procesando cobranza", Toast.LENGTH_LONG).show();
        request.execute(amountCharge);

    }

    private class HttpRequestTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            try {

                String amount=params[0];

                /// GET
                /*String url = "http://www.itconsultores.com.py:8080/api-sst/v2/getexchangerates";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                int responseCode = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONObject jsonObj = new JSONObject(response.toString());
                return jsonObj;*/

                String url = "http://www.itconsultores.com.py:8080/api-sst/v2/sendsms";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent","Mozilla/5.0");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                String to=URLEncoder.encode(getResources().getString(R.string.demo_number),"UTF-8");

                String message=URLEncoder.encode("Desea autorizar el pago de Gs."+amount+"?\nPara autorizarlo responda con su Pin.","UTF-8");

                String urlParameters = "to="+to+"&message="+message;

                // Send post request
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();


                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonObj = new JSONObject(response.toString());
                return jsonObj;


            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try{
                if(result.getString("error").equalsIgnoreCase("N")){
                    Log.d("Respuesta", result.getString("descripcionRespuesta"));
                    Intent confirmIntent = new Intent(getApplicationContext(), MessageActivity.class);
                    confirmIntent.putExtra("message","Transacción Aprobada!");
                    Thread.sleep(7000);
                    startActivity(confirmIntent);
                }
            }catch (Exception e) {

            }
        }

    }
}
