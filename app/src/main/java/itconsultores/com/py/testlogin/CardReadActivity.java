package itconsultores.com.py.testlogin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.acs.audiojack.AudioJackReader;
import com.acs.audiojack.Result;

public class CardReadActivity extends AppCompatActivity {
    public static final String TAG = "CardReadActivity" ;
    public static final String USER_DATA_TAG = "userData";

    private AudioManager mAudioManager;
    private AudioJackReader mReader;
    private Object mResponseEvent = new Object();
    private Button mButton;
    private boolean mResultReady;
    private boolean mPiccAtrReady;
    private byte[] mPiccAtr;
    private Result mResult;

    private boolean mPiccResponseApduReady;
    private byte[] mPiccResponseApdu;

    private int mPiccTimeout = 3;
    private int mPiccCardType = AudioJackReader.PICC_CARD_TYPE_ISO14443_TYPE_A
                                | AudioJackReader.PICC_CARD_TYPE_ISO14443_TYPE_B
                                | AudioJackReader.PICC_CARD_TYPE_FELICA_212KBPS
                                | AudioJackReader.PICC_CARD_TYPE_FELICA_424KBPS
                                | AudioJackReader.PICC_CARD_TYPE_AUTO_RATS;
    private byte[] mPiccCommandApdu;


    private final BroadcastReceiver mHeadsetPlugReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {

                boolean plugged = (intent.getIntExtra("state", 0) == 1);

                // Mute the audio output if the reader is unplugged.
                mReader.setMute(!plugged);
            }
        }
    };

    /**
     * Clase principal que maneja la lectura del contacless card ...
     *
     * Implementa un theread para realizar el proceso de lectura del dispositivo.
     * Pasos
     *    1) AudioJackReader.piccPowerOn => prender el jack manager (onPiccAtrAvailable al estar disponible).
     *    2) AudioJackReader.piccTransmit => iniciar el proceso de lectura (onPiccResponseApduAvailable al leer todo)
     *    3) AudioJackReader.piccPowerOff => Apagar el el jack manager
     *    4) AudioJackReader.sleep() => entrar en sleep para posterior wakeup
     */
    private class Transmit implements Runnable {
        boolean killMe = false;
        int itersWithoutResponse = 0;
        boolean readerConnected = false;

        public void kill(){
            Log.d(TAG, "OnPiccTransmitPreferenceClickListener kill...");
            killMe = true;
        }

        @Override
        public void run() {
            mPiccAtrReady = false;
            while (!killMe) {
                // para reintantar conexion
                if(!readerConnected){
                    itersWithoutResponse++;
                    Log.d(TAG, "OnPiccTransmitPreferenceClickListener !readerConnected...");
                }

                if(itersWithoutResponse == 4) {
                    Log.d(TAG, "Transmit itersWithoutResponse == 4...");
                    kill();
                } else{
                    Log.d(TAG, "OnPiccTransmitPreferenceClickListener mReader.piccPowerOn(mPiccTimeout, mPiccCardType) ...");
                    if (!mReader.piccPowerOn(mPiccTimeout, mPiccCardType)) {

                        showRequestQueueError();

                    } else {
                        showPiccAtr();

                        if (mPiccAtrReady) {
                            readerConnected = true;
                            Log.d(TAG, "mReader.piccTransmit(mPiccTimeout, mPiccCommandApdu)...");
                            if (mReader.piccTransmit(mPiccTimeout, mPiccCommandApdu)) {
                                showPiccResponseApdu();
                                kill();
                            }
                        }
                    }
                }
            }
            Log.d(TAG, "OnPiccTransmitPreferenceClickListener piccPowerOff...");
            mReader.piccPowerOff();
            Log.d(TAG, "OnPiccTransmitPreferenceClickListener sleep...");
            mReader.sleep();
        }
    }

    private class OnPiccTransmitPreferenceClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if (!checkResetVolume()) {
                return;
            }

            mReader.reset( new AudioJackReader.OnResetCompleteListener () {
                public void onResetComplete(AudioJackReader reader) {
                    Log.d(TAG, "OnPiccTransmitPreferenceClickListener::onClick::onResetComplete");
                    // esperamos medio segundo
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplication().getString(R.string.reading_card), Toast.LENGTH_SHORT).show();
                        }
                    });
                    new Thread(new Transmit()).start();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_read);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mButton = (Button)findViewById(R.id.button_read);

        mReader = new AudioJackReader(mAudioManager, true);

        // Registrar headset plug receiver.
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mHeadsetPlugReceiver, filter);

        mPiccCommandApdu = Utils.toByteArray(APDUCommands.GET_ATR);

        // PICC "Transmit" preference click callback.
        mButton.setOnClickListener(new OnPiccTransmitPreferenceClickListener());

        // PICC response APDU callback.
        mReader.setOnPiccResponseApduAvailableListener(new OnPiccResponseApduAvailableListener());

        // PICC ATR callback.
        mReader.setOnPiccAtrAvailableListener(new OnPiccAtrAvailableListener());
    }

    @Override
    protected void onDestroy() {
        // Quitar headset plug receiver.
        unregisterReceiver(mHeadsetPlugReceiver);

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mReader.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReader.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mReader.stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mReader.stop();
    }

    /**
     * Verificar el volumen, debe estar al maximo
     *
     * @return true si el volumen esta al maximo.
     */
    private boolean checkResetVolume() {

        boolean ret = true;

        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        if (currentVolume < maxVolume) {

            Toast.makeText(getApplicationContext(), R.string.message_reset_info_volume, Toast.LENGTH_LONG).show();
            ret = false;
        }

        return ret;
    }

    private class OnPiccResponseApduAvailableListener implements AudioJackReader.OnPiccResponseApduAvailableListener {

        @Override
        public void onPiccResponseApduAvailable(AudioJackReader reader, byte[] responseApdu) {
            Log.d(TAG, "OnPiccResponseApduAvailableListener::onPiccResponseApduAvailable");
            synchronized (mResponseEvent) {

                 /* Wait for the PICC response APDU. */
                /*while (!mPiccResponseApduReady && !mResultReady) {

                    try {
                        mResponseEvent.wait(3000);
                    } catch (InterruptedException e) {
                    }

                    break;
                } */

                // guardar la respuesta APDU.
                mPiccResponseApdu = new byte[responseApdu.length];
                System.arraycopy(responseApdu, 0, mPiccResponseApdu, 0, responseApdu.length);

                // Trigger the response event.
                mPiccResponseApduReady = true;
                mResultReady = true;
                //Log.d(TAG,  Utils.toHexString(responseApdu));
                mResponseEvent.notifyAll();

            }
            //mPiccResponseApduReady = false;
            //mResultReady = false;
        }
    }

    private void showRequestQueueError() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Toast.makeText(getApplicationContext(), "The request cannot be queued.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showPiccResponseApdu() {
        Log.d(TAG, "showPiccResponseApdu");
        UserData user = null;

        synchronized (mResponseEvent) {

            // Wait for the PICC response APDU.
            while (!mPiccResponseApduReady && !mResultReady) {

                try {
                    mResponseEvent.wait(5000);
                } catch (InterruptedException e) {
                }

                break;
            }

            if (mPiccResponseApduReady) {
                String data = Utils.toHexString(mPiccResponseApdu);
                Log.d(TAG, "Leido =>" + data);
                user = getUserData(data);

                /*runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), Utils.toHexString(mPiccResponseApdu), Toast.LENGTH_LONG).show();
                    }
                });*/

            } else if (mResultReady) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(getApplicationContext(),
                                Utils.toErrorCodeString(mResult.getErrorCode()),
                                Toast.LENGTH_LONG).show();
                    }
                });

            } else {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(getApplicationContext(), "The operation timed out.",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            mPiccResponseApduReady = false;
            mResultReady = false;

            Log.d(TAG, "comparing user");
            if (user != null) {
                Intent dataInputIntent = new Intent(getApplicationContext(), DataInputActivity.class);
                dataInputIntent.putExtra(CardReadActivity.USER_DATA_TAG, user);
                startActivity(dataInputIntent);
            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.user_not_found), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    private UserData getUserData(String s) {
        UserData data = null;

        if ("04 B1 89 7A 29 49 80 90 00".equals(s)) {
            data = new UserData();

            data.setCardId("23235325");
            data.setCardName("Fulando Mengano");
            data.setCardPhone("0981555555");
        } else if ("04 E0 56 BA 51 42 80 90 00".equals(s)) {
            data = new UserData();

            data.setCardId("23235300");
            data.setCardName("Fulando2 Mengano2");
            data.setCardPhone("0983777777");
        }

        return data;
    }

    private void showPiccAtr() {
        Log.d(TAG, "showPiccAtr");
        synchronized (mResponseEvent) {

            // Esperar PICC ATR.
            while (!mPiccAtrReady && !mResultReady) {

                try {
                    mResponseEvent.wait(10000);
                } catch (InterruptedException e) {
                }

                break;
            }

            if (mPiccAtrReady) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Log.d(TAG, Utils.toHexString(mPiccAtr));
                    }
                });

            } else if (mResultReady) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), Utils.toErrorCodeString(mResult.getErrorCode()), Toast.LENGTH_LONG).show();
                    }
                });

            } else {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "The operation timed out.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            mResultReady = false;
        }
    }

    private class OnPiccAtrAvailableListener implements AudioJackReader.OnPiccAtrAvailableListener {

        @Override
        public void onPiccAtrAvailable(AudioJackReader reader, byte[] atr) {
            Log.d(TAG, "OnPiccAtrAvailableListener::onPiccAtrAvailable");
            synchronized (mResponseEvent) {

                // Guardar PICC ATR.
                mPiccAtr = new byte[atr.length];
                System.arraycopy(atr, 0, mPiccAtr, 0, atr.length);

                // Disparar evento de respuesta.
                mPiccAtrReady = true;
                mResponseEvent.notifyAll();
            }
        }
    }
}
