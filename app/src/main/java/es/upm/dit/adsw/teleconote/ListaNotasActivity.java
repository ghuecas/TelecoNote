package es.upm.dit.adsw.teleconote;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

/**
 * @author -- Gabriel Huecas --
 * @version 20130501
 */

public class ListaNotasActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private final String TAG = ListaNotasActivity.class.getSimpleName();

    public static final String LISTA_CATEGORIAS= "LISTA_CATEGORIAS";
    public static final String REQUEST_CODE = "requestCode";
    public static final int CREA_NOTA = 0;
    public static final int MODIFICA_NOTA = 1;

    private ListView listaNotasV;

    private NotaDbAdaptador notaDbAdaptador;
    private Cursor notasCursor;
    private SimpleCursorAdapter cursorAdapter;

    private int indiceTitulo;
    private int indiceContenido;
    private int indiceCategoria;
    private int indiceCifrado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.lista_notas);


        notaDbAdaptador= new NotaDbAdaptador(this);
        notaDbAdaptador.open();

//		notaDbAdaptador.creaNota(new Nota("examen ADSW", "primer parcial", "Examen", false));
//		notaDbAdaptador.creaNota(new Nota("examen ADSW", "segundo parcial", "Examen", false));
//		notaDbAdaptador.creaNota(new Nota("practica ADSW", "practica 2", "Practica", false));
//		notaDbAdaptador.creaNota(new Nota("ADSW", "prueba", "Control", false));

        notasCursor= notaDbAdaptador.recuperaTodasLasNotas();

        startManagingCursor(notasCursor);

        indiceTitulo = notasCursor
                .getColumnIndexOrThrow(NotaDbAdaptador.COL_TITULO);
        indiceContenido = notasCursor
                .getColumnIndexOrThrow(NotaDbAdaptador.COL_CONTENIDO);
        indiceCategoria = notasCursor
                .getColumnIndexOrThrow(NotaDbAdaptador.COL_CATEGORIA);
        indiceCifrado = notasCursor
                .getColumnIndexOrThrow(NotaDbAdaptador.COL_CIFRADO);

        // Crea un array para indicar los campos que queremos mostrar en la lista
        String[] from = new String[] { NotaDbAdaptador.COL_TITULO,
                NotaDbAdaptador.COL_CATEGORIA };

        // y un array con los campos de la plantilla que queremos asignarles

        int[] to = new int[] {R.id.row_titulo, R.id.row_categoria};

        // Creamos un SimpleCursorAdaptor y escogemos una plantilla de android para mostrar 2 campos
        cursorAdapter = new SimpleCursorAdapter(this,
                R.layout.nota_row, // Podría usarse android.R.layout.simple_list_item_2
                notasCursor, from, to);

        listaNotasV= (ListView)findViewById(R.id.listaNotasView);
        listaNotasV.setAdapter(cursorAdapter);
        listaNotasV.setOnItemClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }


    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lista, menu);

        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_annadir:
                Intent intent = new Intent(this, DetalleNotaActivity.class);
                intent.putExtra(REQUEST_CODE, CREA_NOTA);
                intent.putExtra(LISTA_CATEGORIAS, notaDbAdaptador.recuperaCategorias());
                startActivityForResult(intent, CREA_NOTA);
                return true;
            case R.id.menu_borrar_lista:
                borrar_lista();
                return true;
            case R.id.menu_ayuda:
                Toast.makeText(this, "Práctica 2, TELECONOTE\npor Gabriel Huecas", Toast.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor c = notasCursor;
        c.moveToPosition(position);
        Nota nota= new Nota(c.getString(indiceTitulo),
                c.getString(indiceContenido),
                c.getString(indiceCategoria),
                NotaDbAdaptador.i2b(c.getInt(indiceCifrado))
        );
        Log.i(TAG, "(BORRA ESTO) crifrado= " + c.getInt(indiceCifrado));
        Intent intent = new Intent(this, DetalleNotaActivity.class);
        Bundle extras = new Bundle();
        extras.putLong(NotaDbAdaptador.COL_ID, id);
        extras.putSerializable(Nota.NOTA, nota);
        extras.putInt(REQUEST_CODE, MODIFICA_NOTA);
        intent.putExtras(extras);
        intent.putExtra(LISTA_CATEGORIAS, notaDbAdaptador.recuperaCategorias());
        startActivityForResult(intent, MODIFICA_NOTA);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_CANCELED)
        {
            Log.i(TAG, "acci�n cancelada");
            return;
        }

        Bundle extras = intent.getExtras();
        if (extras == null)
        {
            Log.e(TAG, "intent sin extras IMPOSIBLE");

            return;
        }

        Nota nota = (Nota) extras.getSerializable(Nota.NOTA);

        switch (requestCode)
        {
            case CREA_NOTA:
                notaDbAdaptador.creaNota(nota);
                Log.i(TAG, "Nota CREADA: " + nota);
                break;
            case MODIFICA_NOTA:
                Long id = extras.getLong(NotaDbAdaptador.COL_ID);
                notaDbAdaptador.actualizaNota(id, nota);
                Log.i(TAG, "Nota " + id + " MODIFICADA: " + nota);
                break;
            default:
                Log.e(TAG, "requestCode IMPOSIBLE");
        }

        Log.i(TAG, "(BORRA ESTO) " + (nota.isCifrado() ? "SI" : "NO") + " esta cifrado");
        cursorAdapter.notifyDataSetChanged();
    }


    private void borrar_lista()
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setMessage(R.string.confirmar_borrar_lista)
                .setCancelable(false)
                .setPositiveButton(R.string.si, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Log.i(TAG, "¡¡Borramos toda la lista!!");
                        notaDbAdaptador.borraTodasLasNotas();
                        notasCursor.requery();
                        cursorAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Log.i(TAG, "NO borramos nada");
                        dialog.cancel();
                    }
                }); //.create().show();
        AlertDialog confirma = builder.create();
        confirma.show();
    }
}
