package it.torvergata.mp.activity.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseInterface extends SQLiteOpenHelper {

	public static final	String TABLE_ORDINE="Ordine";
	public static final	String TABLE_ORDINE_COLUMN_ID="idOrdine";
	public static final	String TABLE_ORDINE_COLUMN_QUANTITATIVE="QuantitaTotale";
	public static final	String TABLE_ORDINE_COLUMN_TOTAL_PRICE="Totale";
	public static final	String TABLE_ORDINE_COLUMN_DATE="Data";
	public static final	String TABLE_ORDINE_COLUMN_TIME="Orario";
	public static final	String TABLE_ORDINE_COLUMN_STATE="StatoOrdine";
	public static final	String TABLE_ORDINE_COLUMN_HASH="HashOrdine";
	
	public static final	String TABLE_CONTIENE="Contiene";
	public static final	String TABLE_CONTIENE_COLUMN_ID_ORDINE="Ordine_idOrdine";
	public static final	String TABLE_CONTIENE_COLUMN_ID_PRODOTTO="Prodotto_idProdotto";
	public static final	String TABLE_CONTIENE_COLUMN_QUANTITATIVE="Quantita";

	public static final	String TABLE_PRODOTTO="Prodotto";
	public static final	String TABLE_PRODOTTO_COLUMN_ID="idProdotto";
	public static final	String TABLE_PRODOTTO_COLUMN_NAME="Nome";
	public static final	String TABLE_PRODOTTO_COLUMN_PRICE="Prezzo";
	public static final	String TABLE_PRODOTTO_COLUMN_EXPIRE_DATE="Scadenza";
	public static final	String TABLE_PRODOTTO_COLUMN_QUANTITATIVE="Quantita";
	public static final	String TABLE_PRODOTTO_COLUMN_DESCRIPTION="Descrizione";
	public static final	String TABLE_PRODOTTO_COLUMN_IMAGE_FILE="FileImmagine";
	
	private static final int DATABASE_VERSION = 1;
	
	private static String DB_PATH = "data/data/it.torvergata.mp/databases/";
	private static String DB_NAME ="dbApplication.db";
	
	private SQLiteDatabase mDataBase;
	private Context mContext;
	
	//Costruttore
	public DatabaseInterface(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
		this.mContext=context;
		boolean dbexist = checkdatabase();
        
		//Se esiste do nothing altrimenti crealo
		if (dbexist) {
			//DoNothing
        } else {
            Log.i("DATABASE INTERFACE:", "Database doesn't exist");
            try {
                createdatabase();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
		
	}

	//Create Database lo Crea se non esiste e subito dopo effettua la copia del Database da Assets
	public void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        if (dbexist) {
        	//Do Nothing
        } else {
            this.getReadableDatabase();
            try {
                copydatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

	//CheckDatabase verifica l'esistenza del database in /data/databases
    private boolean checkdatabase() {
        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch (SQLiteException e) {
            System.out.println("Database doesn't exist");
        }

        return checkdb;
    }

    //CopyDatabase effettua la copia del Database da assets a /data/databases
    private void copydatabase() throws IOException {

        
        InputStream myinput = mContext.getAssets().open(DB_NAME);

        @SuppressWarnings("unused")
        String outfilename = DB_PATH + DB_NAME;

        OutputStream myoutput = new FileOutputStream(DB_PATH+DB_NAME);
              

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer)) > 0) {
            myoutput.write(buffer, 0, length);
        }

        myoutput.flush();
        myoutput.close();
        myinput.close();

    }

    
    public void open() {
        String mypath = DB_PATH + DB_NAME;
        mDataBase = SQLiteDatabase.openDatabase(mypath, null,
                SQLiteDatabase.OPEN_READWRITE);

    }

    public synchronized void close() {
        mDataBase.close();
        super.close();
    }
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		//Vecchia create Table per la creazione del Database
		/*String newTableQueryString =
				"create table " +TABLE_BELFIORE+" ("+
				COLUMN_ID + " integer primary " +"key autoincrement not null," +
				COLUMN_CODE + " text not null," +
				COLUMN_COUNTRY + " text not null" +");";
				db.execSQL(newTableQueryString);
	*/}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
