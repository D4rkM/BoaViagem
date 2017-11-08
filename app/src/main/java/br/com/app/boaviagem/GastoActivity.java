package br.com.app.boaviagem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by 16254861 on 09/10/2017.
 */

public class GastoActivity extends AppCompatActivity {

    private int ano, mes, dia;
    private Button dataGasto;
    private Spinner categoria;
    private EditText descricao, local, valor;
    private DatabaseHelper helper;
    private int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gasto);

        descricao = (EditText) findViewById(R.id.descricao);
        local = (EditText) findViewById(R.id.local);
        valor = (EditText) findViewById(R.id.valor);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        dataGasto = (Button) findViewById(R.id.data);
        dataGasto.setText(dia + "/" + (mes+1) + "/" + ano);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.categoria_gasto,
                android.R.layout.simple_spinner_item);
        categoria = (Spinner) findViewById(R.id.categoria);
        categoria.setAdapter(adapter);

        id = getIntent().getIntExtra(Constantes.VIAGEM_ID, 0);
        helper = new DatabaseHelper(this);
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(R.id.data == id){
            return new DatePickerDialog(this, listener, ano, mes ,dia);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataGasto.setText(dia + "/" + (mes+1) + "/" + ano);
        }
    };


    public void selecionarData(View view){
        showDialog(view.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gasto_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.novo_gasto) {
            startActivity(new Intent(this, GastoActivity.class));
            return true;
        }else if(id == R.id.remover_gasto){
            //Remover Viagem do banco
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void registrarGasto(View view) {

        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("categoria", categoria.getSelectedItemPosition());
        values.put("data", dataGasto.getDrawingTime());
        values.put("valor", valor.getText().toString());
        values.put("descricao", descricao.getText().toString());
        values.put("local", local.getText().toString());
        values.put("viagem_id", id);

        long resultado = db.insert("gasto", null, values);

        if (resultado != -1){
            Toast.makeText(this, "Gasto registrado com sucesso", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "Erro ao cadastrar gasto", Toast.LENGTH_SHORT).show();
        }



    }
}
