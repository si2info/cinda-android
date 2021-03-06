package info.si2.iista.volunteernetworks.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 11/9/15
 * Project: Virde
 */
public class DataBase extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "Cinda.db";

    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String TYPE_INT = " INTEGER ";
    private static final String TYPE_LONG = " BIGINT ";
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_BOOLEAN = " BOOLEAN";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_SERVER =
            "CREATE TABLE " + DBServer.TABLE + " (" +
                    DBServer.ID + TYPE_INT + PRIMARY_KEY + COMMA_SEP +
                    DBServer.TYPE + TYPE_INT + COMMA_SEP +
                    DBServer.NAME + TYPE_INT + COMMA_SEP +
                    DBServer.DESC + TYPE_TEXT + COMMA_SEP +
                    DBServer.URL + TYPE_TEXT + COMMA_SEP +
                    DBServer.GOOGLE_MAPS_STATIC_KEY + TYPE_TEXT + COMMA_SEP +
                    DBServer.PARSE_API + TYPE_TEXT + COMMA_SEP +
                    DBServer.PARSE_KEY + TYPE_TEXT + COMMA_SEP +
                    DBServer.ACTIVE + TYPE_BOOLEAN +
                    ");";

    private static final String SQL_CREATE_CAMPAIGNS =
            "CREATE TABLE " + DBCampaign.TABLE + " (" +
                    DBCampaign.ID + TYPE_INT + PRIMARY_KEY + COMMA_SEP +
                    DBCampaign.ID_SERVER + TYPE_INT + COMMA_SEP +
                    DBCampaign.TYPE + TYPE_INT + COMMA_SEP +
                    DBCampaign.COLOR + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.TITLE + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.SHORT_DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.SCOPE + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.IMAGE + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.COVER + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.IS_SUSCRIBE + TYPE_BOOLEAN + COMMA_SEP +
                    DBCampaign.DATE_START + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.DATE_END + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.SERVER + TYPE_TEXT + COMMA_SEP +
                    DBCampaign.IS_ACTIVE + TYPE_BOOLEAN + COMMA_SEP +
                    DBCampaign.HAVE_TRACKING + TYPE_BOOLEAN + COMMA_SEP +
                    DBCampaign.GEOPOSITION + TYPE_TEXT + COMMA_SEP +
                    "FOREIGN KEY("+ DBCampaign.ID_SERVER + ") REFERENCES " + DBServer.TABLE + "(" + DBServer.ID + ")" +
                    " );";

    private static final String SQL_CREATE_MODEL =
            "CREATE TABLE " + DBModel.TABLE + " (" +
                    DBModel.ID + TYPE_INT + PRIMARY_KEY + COMMA_SEP +
                    DBModel.ID_CAMPAIGN + TYPE_INT + COMMA_SEP +
                    DBModel.POSITION + TYPE_INT + COMMA_SEP +
                    DBModel.LABEL + TYPE_TEXT + COMMA_SEP +
                    DBModel.NAME + TYPE_TEXT + COMMA_SEP +
                    DBModel.DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                    DBModel.TYPE + TYPE_TEXT + COMMA_SEP +
                    DBModel.REQUIRED + TYPE_BOOLEAN + COMMA_SEP +
                    DBModel.OPTIONS + TYPE_TEXT + COMMA_SEP +
                    "FOREIGN KEY("+ DBModel.ID + ") REFERENCES " + DBCampaign.TABLE + "(" + DBCampaign.ID + ")" +
                    " );";

    private static final String SQL_CREATE_MODEL_VALUE =
            "CREATE TABLE " + DBModelValue.TABLE + " (" +
                    DBModelValue.ID + TYPE_INT + COMMA_SEP +
                    DBModelValue.ID_CAMP + TYPE_INT + COMMA_SEP +
                    DBModelValue.FIELD + TYPE_TEXT + COMMA_SEP +
                    DBModelValue.VALUE + TYPE_TEXT + COMMA_SEP +
                    DBModelValue.FIELD_TYPE + TYPE_TEXT + COMMA_SEP +
                    DBModelValue.ORDER + TYPE_INT + COMMA_SEP +
                    DBModelValue.IS_SYNC + TYPE_BOOLEAN + COMMA_SEP +
                    PRIMARY_KEY + "(" + DBModelValue.ID + "," + DBModelValue.FIELD + ")" + COMMA_SEP +
                    "FOREIGN KEY("+ DBModelValue.ID_CAMP + ") REFERENCES " + DBModel.TABLE + "(" + DBModel.ID + ")" +
                    ");";

    private static final String SQL_CREATE_GPX =
            "CREATE TABLE " + DBGpxContribution.TABLE + " (" +
                    DBGpxContribution.ID + TYPE_TEXT + PRIMARY_KEY + COMMA_SEP +
                    DBGpxContribution.ID_SERVER + TYPE_INT + COMMA_SEP +
                    DBGpxContribution.ID_CAMPAIGN + TYPE_INT + COMMA_SEP +
                    DBGpxContribution.DIR + TYPE_TEXT + COMMA_SEP +
                    DBGpxContribution.DATE + TYPE_TEXT + COMMA_SEP +
                    DBGpxContribution.IS_SYNC + TYPE_BOOLEAN + COMMA_SEP +
                    "FOREIGN KEY("+ DBGpxContribution.ID_CAMPAIGN + ") REFERENCES " + DBModel.TABLE + "(" + DBModel.ID + ")" +
                    ");";

    private static final String SQL_CREATE_DICTIONARY =
            "CREATE TABLE " + DBDictionary.TABLE + " (" +
                    DBDictionary.ID_DICTIONARY + TYPE_INT + COMMA_SEP +
                    DBDictionary.ID_SERVER + TYPE_INT + COMMA_SEP +
                    DBDictionary.NAME + TYPE_TEXT + COMMA_SEP +
                    DBDictionary.DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                    "FOREIGN KEY("+ DBDictionary.ID_SERVER + ") REFERENCES " + DBCampaign.TABLE + "(" + DBCampaign.ID + ")" +
                    ");";

    private static final String SQL_CREATE_DICTIONARY_TERM =
            "CREATE TABLE " + DBDictionaryTerm.TABLE + " (" +
                    DBDictionaryTerm.ID_DICTIONARY + TYPE_INT + COMMA_SEP +
                    DBDictionaryTerm.NAME + TYPE_TEXT + COMMA_SEP +
                    DBDictionaryTerm.DESCRIPTION + TYPE_TEXT + COMMA_SEP +
                    DBDictionaryTerm.CODE + TYPE_INT + COMMA_SEP +
                    "FOREIGN KEY("+ DBDictionaryTerm.ID_DICTIONARY + ") REFERENCES " + DBDictionary.TABLE + "(" + DBDictionary.ID_DICTIONARY + ")" +
                    ");";

    private static final String SQL_DELETE_CAMPAIGNS =
            "DROP TABLE IF EXISTS " + DBCampaign.TABLE;

    private static final String SQL_DELETE_MODELS =
            "DROP TABLE IF EXISTS " + DBModel.TABLE;

    private static final String SQL_DELETE_MODELS_ITEM =
            "DROP TABLE IF EXISTS " + DBModelValue.TABLE;

    private static final String SQL_DELETE_SERVER =
            "DROP TABLE IF EXISTS " + DBServer.TABLE;

    private static final String SQL_DELETE_GPX =
            "DROP TABLE IF EXISTS " + DBGpxContribution.TABLE;

    private static final String SQL_DELETE_DICTIONARY =
            "DROP TABLE IF EXISTS " + DBDictionary.TABLE;

    private static final String SQL_DELETE_DICTIONARY_TERMS =
            "DROP TABLE IF EXISTS " + DBDictionaryTerm.TABLE;

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SERVER);
        db.execSQL(SQL_CREATE_CAMPAIGNS);
        db.execSQL(SQL_CREATE_MODEL);
        db.execSQL(SQL_CREATE_MODEL_VALUE);
        db.execSQL(SQL_CREATE_GPX);
        db.execSQL(SQL_CREATE_DICTIONARY);
        db.execSQL(SQL_CREATE_DICTIONARY_TERM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SERVER);
        db.execSQL(SQL_DELETE_CAMPAIGNS);
        db.execSQL(SQL_DELETE_MODELS);
        db.execSQL(SQL_DELETE_MODELS_ITEM);
        db.execSQL(SQL_DELETE_GPX);
        db.execSQL(SQL_DELETE_DICTIONARY);
        db.execSQL(SQL_DELETE_DICTIONARY_TERMS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
