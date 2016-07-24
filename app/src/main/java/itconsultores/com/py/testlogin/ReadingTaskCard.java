package itconsultores.com.py.testlogin;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by Jerry on 23/07/2016.
 */
public class ReadingTaskCard extends AsyncTask<Void, Void, UserData> {
    private Context mContext;


    public ReadingTaskCard(Context ctx ) {
        this.mContext = ctx;
    }


    @Override
    protected UserData doInBackground(Void... params) {
        //TODO: usar el api MCR35 para leer los datos de la tarjeta
        final UserData user = new UserData();

        try {


        } catch (Exception e) {
            return null;
        }
        user.setCardId("23235325");
        user.setCardName("Fulando Mengano");
        user.setCardPhone("0981555555");

        return user;
    }

    @Override
    protected void onPostExecute(final UserData user) {
        //mAuthTask = null;
        //mReader.stop();
        // showProgress(false);

        if (user != null) {
            Intent dataInputIntent = new Intent(mContext, DataInputActivity.class);
            dataInputIntent.putExtra(CardReadActivity.USER_DATA_TAG, user);
            //startActivity(dataInputIntent);
        } else {
            Toast.makeText(mContext, "Problema al leer la tarjeta ...",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        //mAuthTask = null;

        //mReader.stop();
        //showProgress(false);
    }
}
