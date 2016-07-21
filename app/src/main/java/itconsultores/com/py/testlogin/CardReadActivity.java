package itconsultores.com.py.testlogin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.acs.audiojack.AudioJackReader;
import com.acs.audiojack.DukptReceiver;
import com.acs.audiojack.Result;

public class CardReadActivity extends AppCompatActivity {
    public static final String USER_DATA_TAG = "userData";
    private static final String TAG = "CardReadActivity" ;
    private ReadingCardTask mAuthTask = null;
    private AudioManager mAudioManager;
    private AudioJackReader mReader;
    private Object mResponseEvent = new Object();
    private DukptReceiver mDukptReceiver = new DukptReceiver();
    private boolean mFirmwareVersionReady;
    private String mFirmwareVersion;
    private Button mButton;
    private boolean mResultReady;
    private boolean mDeviceIdReady;
    private byte[] mDeviceId;
    private Result mResult;

    private final BroadcastReceiver mHeadsetPlugReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {

                boolean plugged = (intent.getIntExtra("state", 0) == 1);

                /* Mute the audio output if the reader is unplugged. */
                mReader.setMute(!plugged);
            }
        }
    };

    private class OnGetFirmwareClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            /* Check the reset volume. */
            if (!checkResetVolume()) {
                return;
            }

            /* Reset the reader. */
            Log.d("/*/*/*/*/**", "readerReset");
            mReader.reset(new OnResetCompleteListener());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_read);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mButton = (Button)findViewById(R.id.button_read);

        mReader = new AudioJackReader(mAudioManager, true);



        /* Register the headset plug receiver. */
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mHeadsetPlugReceiver, filter);


        //checkResetVolume();

        mAuthTask = new ReadingCardTask( );
        mReader.reset();

        /* Set the "Get firmware version" preference click callback. */
        mButton.setOnClickListener(new OnGetFirmwareClickListener());

        /* Set the result callback. */
        //mReader.setOnResultAvailableListener(new OnResultAvailableListener());

        /* Set the firmware version callback. */
        mReader.setOnFirmwareVersionAvailableListener(new OnFirmwareVersionAvailableListener());




//        /* Set the status callback. */
//        mReader.setOnStatusAvailableListener(new OnStatusAvailableListener());
//
//        /* Set the track data notification callback. */
//        mReader.setOnTrackDataNotificationListener(new OnTrackDataNotificationListener());
//
//        /* Set the track data callback. */
//        mReader.setOnTrackDataAvailableListener(new OnTrackDataAvailableListener());
//
//        /* Set the raw data callback. */
//        mReader.setOnRawDataAvailableListener(new OnRawDataAvailableListener());
//
//        /* Set the custom ID callback. */
//        mReader.setOnCustomIdAvailableListener(new OnCustomIdAvailableListener());
//
        /* Set the device ID callback. */
        mReader.setOnDeviceIdAvailableListener(new OnDeviceIdAvailableListener());
//
//        /* Set the DUKPT option callback. */
//        mReader.setOnDukptOptionAvailableListener(new OnDukptOptionAvailableListener());
//
//        /* Set the track data option callback. */
//        mReader.setOnTrackDataOptionAvailableListener(new OnTrackDataOptionAvailableListener());
//
//        /* Set the PICC ATR callback. */
//        mReader.setOnPiccAtrAvailableListener(new OnPiccAtrAvailableListener());
//
//        /* Set the PICC response APDU callback. */
//        mReader.setOnPiccResponseApduAvailableListener(new OnPiccResponseApduAvailableListener());
//
//        /* Set the key serial number. */
//        mDukptReceiver.setKeySerialNumber(mIksn);
//
//        /* Load the initial key. */
//        mDukptReceiver.loadInitialKey(mIpek);

    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        /* Unregister the headset plug receiver. */
        unregisterReceiver(mHeadsetPlugReceiver);

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart");

        super.onStart();

        mReader.start();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        mReader.start();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
        mReader.stop();
    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
        mReader.stop();
    }

    // Tarea asincrona para leer la tarjeta.

    public class ReadingCardTask extends AsyncTask<Void, Void, UserData> {


        public ReadingCardTask( ) {
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
            mAuthTask = null;
            //mReader.stop();
           // showProgress(false);

            if (user != null) {
                Intent dataInputIntent = new Intent(getApplicationContext(), DataInputActivity.class);
                dataInputIntent.putExtra(USER_DATA_TAG, user);
                startActivity(dataInputIntent);
            } else {
                Toast.makeText(CardReadActivity.this, "Problema al leer la tarjeta ...",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

            //mReader.stop();
            //showProgress(false);
        }




    }

    /**
     * Checks the reset volume.
     *
     * @return true if current volume is equal to maximum volume.
     */
    private boolean checkResetVolume() {

        boolean ret = true;

        int currentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);

        int maxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        if (currentVolume < maxVolume) {

            //showMessageDialog(R.string.info, R.string.message_reset_info_volume);
            Toast.makeText(getApplicationContext(), R.string.message_reset_info_volume,
                    Toast.LENGTH_LONG).show();
            ret = false;
        }

        return ret;
    }


    private class OnFirmwareVersionAvailableListener implements AudioJackReader.OnFirmwareVersionAvailableListener {

        @Override
        public void onFirmwareVersionAvailable(AudioJackReader reader,
                                               String firmwareVersion) {

            synchronized (mResponseEvent) {

                /* Store the firmware version. */
                mFirmwareVersion = firmwareVersion;

                /* Trigger the response event. */
                mFirmwareVersionReady = true;
                mResponseEvent.notifyAll();
            }
        }
    }

    public class OnResetCompleteListener implements AudioJackReader.OnResetCompleteListener {

        public OnResetCompleteListener() {
            Log.d("/*/*/*/","OnResetCompleteListener");
        }
        @Override
        public void onResetComplete(AudioJackReader reader) {

                /* Get the firmware version. */
            mFirmwareVersionReady = false;
            mResultReady = false;
            if (!reader.getFirmwareVersion()) {

                /* Show the request queue error. */
                Log.e("/*/*/*/*", "The request cannot be queued.");
                /* Show the request queue error. */
                Toast.makeText(getApplicationContext(), "The request cannot be queued.",
                        Toast.LENGTH_LONG).show();

            } else {

                /* Show the firmware version. */
                Log.d("*/*/*/*/*/*/", "showFirmVer");
                showFirmwareVersion();
            }
        }


    }

    /**
     * Shows the firmware version.
     */
    private void showFirmwareVersion() {
        Log.d("/*/*//","enter showFirmwareVersion");
        synchronized (mResponseEvent) {

            /* Wait for the firmware version. */
            while (!mFirmwareVersionReady && !mResultReady) {

                try {
                    mResponseEvent.wait(10000);
                } catch (InterruptedException e) {
                }

                break;
            }

            if (mFirmwareVersionReady) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        /* Show the timeout. */
                        Toast.makeText(
                                getApplicationContext(),
                                mFirmwareVersion,
                                Toast.LENGTH_LONG).show();
                    }
                });


            } else if (mResultReady) {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        /* Show the timeout. */
                        Toast.makeText(
                                getApplicationContext(),
                                toErrorCodeString(mResult.getErrorCode()),
                                Toast.LENGTH_LONG).show();
                    }
                });


            } else {
                    /* Show the timeout. */

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        /* Show the timeout. */
                        Toast.makeText(
                                getApplicationContext(),
                                "The operation timed out.",
                                Toast.LENGTH_LONG).show();
                    }
                });

            }

            mFirmwareVersionReady = false;
            mResultReady = false;
        }
    }

    private String toErrorCodeString(int errorCode) {
        String errorCodeString = null;

        switch (errorCode) {
            case Result.ERROR_SUCCESS:
                errorCodeString = "The operation completed successfully.";
                break;
            case Result.ERROR_INVALID_COMMAND:
                errorCodeString = "The command is invalid.";
                break;
            case Result.ERROR_INVALID_PARAMETER:
                errorCodeString = "The parameter is invalid.";
                break;
            case Result.ERROR_INVALID_CHECKSUM:
                errorCodeString = "The checksum is invalid.";
                break;
            case Result.ERROR_INVALID_START_BYTE:
                errorCodeString = "The start byte is invalid.";
                break;
            case Result.ERROR_UNKNOWN:
                errorCodeString = "The error is unknown.";
                break;
            case Result.ERROR_DUKPT_OPERATION_CEASED:
                errorCodeString = "The DUKPT operation is ceased.";
                break;
            case Result.ERROR_DUKPT_DATA_CORRUPTED:
                errorCodeString = "The DUKPT data is corrupted.";
                break;
            case Result.ERROR_FLASH_DATA_CORRUPTED:
                errorCodeString = "The flash data is corrupted.";
                break;
            case Result.ERROR_VERIFICATION_FAILED:
                errorCodeString = "The verification is failed.";
                break;
            case Result.ERROR_PICC_NO_CARD:
                errorCodeString = "No card in PICC slot.";
                break;
            default:
                errorCodeString = "Error communicating with reader.";
                break;
        }

        return errorCodeString;
    }
    private class OnDeviceIdAvailableListener implements
            AudioJackReader.OnDeviceIdAvailableListener {

        @Override
        public void onDeviceIdAvailable(AudioJackReader reader, byte[] deviceId) {

            synchronized (mResponseEvent) {

                /* Store the custom ID. */
                mDeviceId = new byte[deviceId.length];
                System.arraycopy(deviceId, 0, mDeviceId, 0, deviceId.length);

                /* Trigger the response event. */
                mDeviceIdReady = true;
                mResponseEvent.notifyAll();
            }
        }
    }
}
