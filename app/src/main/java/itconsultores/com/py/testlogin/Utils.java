package itconsultores.com.py.testlogin;

import com.acs.audiojack.Result;

import java.util.Locale;

/**
 * Created by jerry on 22/07/2016.
 */
public class Utils {

    /**
     * Convertir byte[] a string HEX.
     *
     * @param buffer los bytes a convertir.
     * @return la salida en HEX.
     */
    public static String toHexString(byte[] buffer) {

        String bufferString = "";

        if (buffer != null) {

            for (int i = 0; i < buffer.length; i++) {

                String hexChar = Integer.toHexString(buffer[i] & 0xFF);
                if (hexChar.length() == 1) {
                    hexChar = "0" + hexChar;
                }

                bufferString += hexChar.toUpperCase(Locale.US) + " ";
            }
        }

        return bufferString;
    }

    /**
     *
     * @param errorCode Codigo de error obtenido
     * @return Mensaje digerido
     */
    public static String toErrorCodeString(int errorCode) {
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

    /**
     * battery level to string.
     *
     * @param batteryLevel nivel de bateria.
     * @return nivel a string.
     */
    private String toBatteryLevelString(int batteryLevel) {

        String batteryLevelString = null;

        switch (batteryLevel) {
            case 0:
                batteryLevelString = ">= 3.00V";
                break;
            case 1:
                batteryLevelString = "2.90V - 2.99V";
                break;
            case 2:
                batteryLevelString = "2.80V - 2.89V";
                break;
            case 3:
                batteryLevelString = "2.70V - 2.79V";
                break;
            case 4:
                batteryLevelString = "2.60V - 2.69V";
                break;
            case 5:
                batteryLevelString = "2.50V - 2.59V";
                break;
            case 6:
                batteryLevelString = "2.40V - 2.49V";
                break;
            case 7:
                batteryLevelString = "2.30V - 2.39V";
                break;
            case 8:
                batteryLevelString = "< 2.30V";
                break;
            default:
                batteryLevelString = "Unknown";
                break;
        }

        return batteryLevelString;
    }

    /**
     * Convertir HEX string a byte[].
     *
     * @param hexString cadena hexadecimal.
     * @return byte array.
     */
    public static byte[] toByteArray(String hexString) {

        byte[] byteArray = null;
        int count = 0;
        char c = 0;
        int i = 0;

        boolean first = true;
        int length = 0;
        int value = 0;

        // Count number of hex characters
        for (i = 0; i < hexString.length(); i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a'
                    && c <= 'f') {
                count++;
            }
        }

        byteArray = new byte[(count + 1) / 2];
        for (i = 0; i < hexString.length(); i++) {

            c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                value = c - '0';
            } else if (c >= 'A' && c <= 'F') {
                value = c - 'A' + 10;
            } else if (c >= 'a' && c <= 'f') {
                value = c - 'a' + 10;
            } else {
                value = -1;
            }

            if (value >= 0) {

                if (first) {

                    byteArray[length] = (byte) (value << 4);

                } else {

                    byteArray[length] |= value;
                    length++;
                }

                first = !first;
            }
        }

        return byteArray;
    }
}
