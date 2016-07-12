package itconsultores.com.py.testlogin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class CardReadActivity extends AppCompatActivity {
    private ReadingCardTask mAuthTask = null;
    private View mProgressView;
    private View mCardReadFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_read);

        mCardReadFormView = findViewById(R.id.read_layout);
        mProgressView = findViewById(R.id.card_read_progress);


        createTask();

    }

    private void createTask() {
        showProgress(true);
        mAuthTask = new ReadingCardTask( );
        mAuthTask.execute((Void) null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        Toast.makeText(CardReadActivity.this, "Leyendo la tarjeta ...",Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mCardReadFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mCardReadFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCardReadFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mCardReadFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    // Tarea asincrona para leer la tarjeta.

    public class ReadingCardTask extends AsyncTask<Void, Void, Boolean> {


        ReadingCardTask( ) {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: autenticar sobre un ws

            try {
                // Simular latencia de red
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                return false;
            }

            //TODO: usar el api MCR35 para leer los datos de la tarjeta
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent dataInputIntent = new Intent(getApplicationContext(), DataInputActivity.class);
                startActivity(dataInputIntent);
            } else {
                Toast.makeText(CardReadActivity.this, "Problema al leer la tarjeta ...",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
