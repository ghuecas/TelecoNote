package es.upm.dit.adsw.teleconote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author -- Gabriel Huecas --
 * @version 20130501
 */

public class DetalleNotaActivity extends AppCompatActivity {
    private static final String TAG = DetalleNotaActivity.class.getSimpleName();

    private ArrayAdapter<String> adaptadorAutocomplete;
    private static final String []todasEtiquetas= {"Examen", "Clase", "Otros"};

    private Button guardar;
    private Button cifrar;
    private Button cancelar;

    private ProgressBar progreso;
    private EditText tituloV;
    private EditText contenidoV;
    private AutoCompleteTextView categoriaV;
    private ImageView estaCifradoV;

    private long id;
    private boolean estaCifrado;
    private String claveLeida= null;

    private Context ctx;

    private String []eliminaDuplicados(String []categorias)
    {
        Map<String, String> aux= new HashMap<String, String>();
        for (String s: categorias)
        {
            aux.put(s, s);
        }

        Object []objs= aux.values().toArray();
        String []retVal= new String[objs.length];
        for (int i= 0; i < objs.length; i++)
            retVal[i]= (String)objs[i];

        Log.i(TAG, "valores= " + Arrays.toString(retVal));

        return retVal;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate");
        setTheme(android.R.style.Theme);
        setContentView(R.layout.detalle_nota);

        ctx= this; // context for toast in listeners...

        guardar= (Button)findViewById(R.id.bottonAnadir);
        cifrar= (Button)findViewById(R.id.botonCifrar);
        cancelar= (Button)findViewById(R.id.botonCancelar);

        progreso= (ProgressBar)findViewById(R.id.progressBar2);
        progreso.setVisibility(View.INVISIBLE);
        tituloV= (EditText)findViewById(R.id.editTitulo);
        contenidoV= (EditText)findViewById(R.id.editContenido);
        categoriaV= (AutoCompleteTextView)findViewById(R.id.editCategoria);
        estaCifradoV= (ImageView)findViewById(R.id.imageCifrado);



        estaCifrado= false;

        Intent intent= getIntent();
        Bundle extras= intent.getExtras();
        if (extras == null)
        {
            Toast.makeText(this, "Bogus app", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
            finish();
        }
        int requestCode= extras.getInt(ListaNotasActivity.REQUEST_CODE);
        if (requestCode == ListaNotasActivity.MODIFICA_NOTA)
        {
            Nota nota= (Nota)extras.getSerializable(Nota.NOTA);

            id= extras.getLong(NotaDbAdaptador.COL_ID);
            tituloV.setText(nota.getTitulo());
            if (nota.getContenido() != null)
            {
                contenidoV.setText(nota.getContenido());
            }
            categoriaV.setText(nota.getCategoria());
            estaCifrado= nota.isCifrado();
        }
        String []categorias= extras.getStringArray(ListaNotasActivity.LISTA_CATEGORIAS);


        Log.i(TAG, "iniciaAutocomplete " + Arrays.toString(categorias));
        categorias= eliminaDuplicados(categorias);
        Log.i(TAG, "eliminando duplicados queda " + Arrays.toString(categorias));

        adaptadorAutocomplete = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, categorias);
        categoriaV.setAdapter(adaptadorAutocomplete);

        actualizaVista(estaCifrado);

        // manejo de botones
        guardar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                guardaNota();
            }
        });

        cifrar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (contenidoV.getText().length() == 0)
                {
                    Toast.makeText(ctx, "Contenido est� vac�o", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (claveLeida == null)
                {
                    pideClave(); // pide la clave y, si es v�lida, cifra/descifra
                }
                else
                {
                    // tengo clave, directamente pasamos a cifrar/descifrar
                    new cambiaCifrado().execute(claveLeida);
                }
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
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

    private void actualizaVista(boolean cifrado)
    {
        // candado abierto o cerrado
        int cifradoDrawable= cifrado ? R.drawable.ic_menu_cifrar
                : R.drawable.ic_menu_descifrar;
        estaCifradoV.setImageResource(cifradoDrawable);

        // texto del bot�n "cifrar"/"descifrar"
        int cifradoText= cifrado ? R.string.descifrar : R.string.cifrar;
        cifrar.setText(cifradoText);

        // permitir editar o no
        contenidoV.setEnabled( ! cifrado);
    }

    private void guardaNota()
    {
        if (tituloV.getText().length() <= 0)
        {
            Toast.makeText(this, "Introduce un titulo", Toast.LENGTH_LONG).show();
            return;
        }
        String titulo= tituloV.getText().toString();
        Log.i(TAG, "titulo= " + titulo);

        String contenido= "";
        if (contenidoV.getText().length() > 0)
        {
            contenido= contenidoV.getText().toString();
        }
        Log.i(TAG, "contenido= " + contenido);

        if (categoriaV.getText().length() <= 0)
        {
            Toast.makeText(this, "Introduce una categor�a", Toast.LENGTH_LONG).show();
            return;
        }
        String categoria= categoriaV.getText().toString();
        Log.i(TAG, "categoria= " + categoria);

        Nota nota= new Nota(titulo, contenido, categoria, estaCifrado);

        Intent intent = new Intent();
        if (false)
        {
            Bundle extras = new Bundle();
            extras.putLong(NotaDbAdaptador.COL_ID, id);
            extras.putSerializable(Nota.NOTA, nota);
            intent.putExtras(extras);
        }
        else
        {
            intent.putExtra(NotaDbAdaptador.COL_ID, id);
            intent.putExtra(Nota.NOTA, nota);
        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private void pideClave()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

//		alert.setTitle("Title");
        alert.setMessage("Introduce clave de cifrado");

        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.getText().length() == 0)
                {
                    Toast.makeText(ctx, "La clave no puede ser vac�a", Toast.LENGTH_SHORT).show();
                    return;
                }
                claveLeida= input.getText().toString();
                // Do something with value!
                new cambiaCifrado().execute(claveLeida);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private class cambiaCifrado extends AsyncTask<String, Integer, String>
    {

        private boolean cifrar;
        private String texto;

        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            cifrar = ! estaCifrado;
            texto= contenidoV.getText().toString();

            progreso.setProgress(0);
            progreso.setMax(texto.length());
            progreso.setVisibility(View.VISIBLE);

            contenidoV.setEnabled(false);
        }

        /* (non-Javadoc)
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected String doInBackground(String... params)
        {
            if (params.length == 0)
            {
                Log.i(TAG, "doInBackground: ERROR GRAVE: invocado sin par�metros");
                return null;
            }
            if (params.length > 1)
            {
                Log.i(TAG, "doInBackground: ERROR GRAVE: invocado con demasiados par�metros");
                return null;
            }

            Log.i(TAG, "tenemos clave de cifrado= " + params[0]);
            Cifrador cif= new Cifrador(params[0]); // clave

            String retVal= "";

            int totalLength= texto.length();
            Log.i(TAG, "doInBackground: hay que CIFRAR");
            for (int i= 0; i < totalLength; i++)
            {
                if (cifrar)
                {
                    retVal+= cif.cifraCaracter(texto.charAt(i));
                }
                else
                {
                    retVal+= cif.descifraCaracter(texto.charAt(i));
                }

                //publishProgress((int) ((i / (float) totalLength) * 100)); // relative
                publishProgress(i); // absolute, max= totalLength

                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    Log.w(TAG, "Tarea de cifrado/descifrado interrumpida");
                }
            }

            return retVal;
        }

        /* (non-Javadoc)
         * @see android.os.AsyncTask#onProgressUpdate(Progress[])
         */
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);

            progreso.setProgress(values[0]);
        }

        /* (non-Javadoc)
         * @see android.os.AsyncTask#onCancelled()
         */
        @Override
        protected void onCancelled()
        {
            super.onCancelled();
            Log.i(TAG, "Han cancelado el cifrado/descifrado");
            progreso.setVisibility(View.INVISIBLE);
        }

        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            Log.i(TAG, "Tenemos el texto devuelto" + result);

            if (! estaCifrado)
                contenidoV.setEnabled(true);

            contenidoV.setText(result);

            estaCifrado = ! estaCifrado;

            actualizaVista(estaCifrado);
            progreso.setVisibility(View.INVISIBLE);
        }
    }
}
