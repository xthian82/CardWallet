package itconsultores.com.py.testlogin;

/**
 * Created by jerry on 23/07/2016.
 */
public class APDUCommands {
    public static final String LOAD_DATA = "BE 26";
    public static final String GET_ATR = "FF CA 00 00 00";
    public static final String READ_MEMORY_CARD = "FF B0";
    public static final String LOAD_PICC = "00 84 00 16 FF";
    public static final String LOAD_PICC_RF = "07 85 85 85 85 85 85 85 85 69 69 69 69 69 69 69 69 3F 3F";
}
