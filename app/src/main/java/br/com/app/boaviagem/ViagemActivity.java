package br.com.app.boaviagem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 16254861 on 09/10/2017.
 */

public class ViagemActivity extends AppCompatActivity {

    private DatabaseHelper helper;
    private EditText destino , quantidadePessoas, orcamento;
    private RadioGroup radioGroup;
    private int ano, mes, dia;
    private Button dataSaidaButton, dataChegadaButton;
    private Date dataSaida, dataChegada;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nova_viagem);

        Calendar calendar = Calendar.getInstance();
        ano = calendar.get(Calendar.YEAR);
        mes = calendar.get(Calendar.MONTH);
        dia = calendar.get(Calendar.DAY_OF_MONTH);

        dataChegadaButton = (Button) findViewById(R.id.dataChegada);
        dataSaidaButton = (Button) findViewById(R.id.dataSaida);

        dataSaidaButton.setText(dia + "/" + (mes+1) + "/" + ano);
        dataChegadaButton.setText(dia + "/" + (mes+1) + "/" + ano);


        destino = (EditText) findViewById(R.id.destino);
        quantidadePessoas =
                (EditText) findViewById(R.id.quantidadePessoas);
        orcamento = (EditText) findViewById(R.id.orcamento);
        radioGroup = (RadioGroup) findViewById(R.id.tipoViagem);

        helper = new DatabaseHelper(this);

        id = getIntent().getStringExtra(Constantes.VIAGEM_ID);

        if (id != null){
            prepararEdicao();
        }

    }

    public void selecionarDataV (View view){
        showDialog(view.getId());
    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(R.id.dataSaida == id){
            return new DatePickerDialog(this, listener, ano, mes , dia);
        } else if(R.id.dataChegada == id){
            return new DatePickerDialog(this, listener2, ano, mes, dia);
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.viagem_menu, menu);
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

    public void salvarViagem(View view) {
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("destino", destino.getText().toString());
        values.put("data_chegada", dataChegadaButton.getDrawingTime());
        values.put("data_saida", dataSaidaButton.getDrawingTime());
        values.put("orcamento", orcamento.getText().toString());
        values.put("quantidade_pessoas", quantidadePessoas.getText().toString());

        int tipo = radioGroup.getCheckedRadioButtonId();

        if(tipo == R.id.lazer){
            values.put("tipo_viagem", Constantes.VIAGEM_LAZER);
        }else{
            values.put("tipo_viagem", Constantes.VIAGEM_NEGOCIOS);
        }

        long resultado = db.insert("viagem", null, values);

        if (resultado != -1){
            Toast.makeText(this, getString(R.string.registro_salvo), Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, getString(R.string.erro_salvar), Toast.LENGTH_SHORT).show();
        }

        if(id == null){
            resultado = db.insert("viagem", null, values);
        }else{
            resultado = db.update("viagem", values, "_id = ?", new String[]{id});
        }
    }
    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }

    private void prepararEdicao(){
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery(
                        "SELECT tipo_viagem, destino, data_chegada," +
                        "data_saida, quantidade_pessoas, orcamento" +
                        "FROM viagem WHERE _id = ?", new String[]{ id }
                );

        cursor.moveToFirst();

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy");

        if(cursor.getInt(0) == Constantes.VIAGEM_LAZER){
            radioGroup.check(R.id.lazer);
        } else {
            radioGroup.check(R.id.negocios);
        }

        destino.setText(cursor.getString(1));
        dataChegada = new Date(cursor.getLong(2));
        dataSaida = new Date(cursor.getLong(3));
        dataChegadaButton.setText(dateFormat.format(dataChegada));
        dataSaidaButton.setText(dateFormat.format(dataSaida));
        quantidadePessoas.setText(cursor.getString(4));
        orcamento.setText(cursor.getString(5));
        cursor.close();

    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth){
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataSaidaButton.setText(dia + "/" + (mes+1) + "/" + ano);

        }
    };

    private DatePickerDialog.OnDateSetListener listener2 = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth){
            ano = year;
            mes = monthOfYear;
            dia = dayOfMonth;
            dataChegadaButton.setText(dia + "/" + (mes+1) + "/" + ano);

        }
    };

}
