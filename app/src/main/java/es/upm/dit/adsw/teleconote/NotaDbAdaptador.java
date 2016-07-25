package es.upm.dit.adsw.teleconote;

/**
 * @author cif
 * @version 20130426
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotaDbAdaptador {

    public static int boolean2int(boolean b) { return  b ? 0 : 1; }
	public static boolean i2b (int i) { return i == 0; }

	public static final String COL_TITULO = "titulo";
	public static final String COL_CONTENIDO = "contenido";
	public static final String COL_CATEGORIA = "etiquetas";
	public static final String COL_CIFRADO = "cifrado";
	public static final String COL_ID = "_id";

	private static final String TAG = NotaDbAdaptador.class.getSimpleName();
	private DBHelper dbHelper;
	private SQLiteDatabase db;

	private static final String DATABASE_NAME = "album_notas";
	private static final String DATABASE_TABLE = "notas";
	
	private static final String DATABASE_CREATE = "create table " + DATABASE_TABLE 
			                    + " (_id integer primary key autoincrement, "
			                    + COL_TITULO + " text not null, " 
			                    + COL_CONTENIDO + " text, "
			                    + COL_CIFRADO + " integer,"
			                    + COL_CATEGORIA + " text"
			                    + ");";

	
	private static final int DATABASE_VERSION = 1;

	private final Context mCtx;

	private static class DBHelper extends SQLiteOpenHelper {

		DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "Creando base de datos");
			db.execSQL(DATABASE_CREATE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int versionAnterior, int versionNueva) {
			Log.w(TAG, "Actualizando base de datos de la versión " + versionAnterior + " a "
					+ versionNueva + ", lo que destruirá todos los datos existentes");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db); // en desarrollo, vuelvo a crear BD en vez de cambiar de versión
		}
	}
	  /**
     * Constructor - recibe el contexto de la base de datos que va a ser
     * abierta o creada. Abre la base de datos. Si no se puede abrir, intenta crear una nueva
     * instancia de la base de datos. Si no se puede crear, lanza una excepción
     * para alertar del fallo
     * 
     * @param ctx contexto con el que trabajar
     * @throws SQLException si la base de datos no estuviera ni abierta ni creada
     */
    public NotaDbAdaptador(Context ctx) throws SQLException {
        this.mCtx = ctx;
        dbHelper = new DBHelper(mCtx);
    }
    
    public void open() {
        db = dbHelper.getWritableDatabase();    	
    }
    
    public void close() {
        dbHelper.close();
    }


	
	
	
    public long creaNota(Nota nota) {
    	ContentValues valoresIniciales = new ContentValues();
        valoresIniciales.put(COL_TITULO, nota.getTitulo());
        valoresIniciales.put(COL_CONTENIDO, nota.getContenido());
        valoresIniciales.put(COL_CIFRADO, boolean2int(nota.isCifrado()));
        valoresIniciales.put(COL_CATEGORIA, nota.getCategoria());

        return db.insert(DATABASE_TABLE, null, valoresIniciales);
    }

   
    
    public boolean borraNota(long id) {
    	Log.i(TAG, "borra nota id " + id);
        return db.delete(DATABASE_TABLE, COL_ID + "=" + id, null) > 0;
    }

     
    public  String [] recuperaCategorias(){
    	Cursor c = db.query(DATABASE_TABLE, new String[] {COL_CATEGORIA}, null, null, null, null, null);
    	if (c == null) {
    		return new String[0];
    	}
    	int indiceCategoria = c.getColumnIndexOrThrow(NotaDbAdaptador.COL_CATEGORIA);
    	c.moveToFirst();
    	String [] categorias =	new String [c.getCount()];
    	for (int i = 0; i < c.getCount(); i++) {
    		categorias[i] = c.getString(indiceCategoria);
    		c.moveToNext();
    	}
    	return categorias;
    }
    public Cursor recuperaTodasLasNotas() {

        return db.query(DATABASE_TABLE, new String[] {COL_ID, COL_TITULO,
                COL_CONTENIDO, COL_CIFRADO, COL_CATEGORIA}, null, null, null, null, null);
    }
     
    public Cursor recuperaNota(long id) throws SQLException {

        Cursor mCursor =

            db.query(true, DATABASE_TABLE, new String[] {COL_ID,
                    COL_TITULO, COL_CONTENIDO, COL_CIFRADO, COL_CATEGORIA}, COL_ID + "=" + id, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
    
    
    public void borraTodasLasNotas () {
    	db.delete(DATABASE_TABLE, null, null);
    }

    public boolean actualizaNota(long id, Nota nota) {
    	Log.i(TAG, "Actualiza nota " + id + " " + nota);
        ContentValues args = new ContentValues();
        args.put(COL_TITULO, nota.getTitulo());
        args.put(COL_CONTENIDO, nota.getContenido());
        args.put(COL_CIFRADO, boolean2int(nota.isCifrado()));
        args.put(COL_CATEGORIA, nota.getCategoria());
        return db.update(DATABASE_TABLE, args, COL_ID + "=" + id, null) > 0;
    }

}
