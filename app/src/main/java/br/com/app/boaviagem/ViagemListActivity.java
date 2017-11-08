package br.com.app.boaviagem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 16254861 on 09/10/2017.
 */

public class ViagemListActivity extends ListActivity implements DialogInterface.OnClickListener, AdapterView.OnItemClickListener {

    private List<Map<String, Object>> viagens;
    private AlertDialog alertDialog;
    private int viagemSelecionada;
    private AlertDialog dialogConfirmacao;

    private DatabaseHelper helper;
    private SimpleDateFormat dateFormat;
    private Double valorLimite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new DatabaseHelper(this);
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        String valor = preferences.getString("valor_limite", "-1");
        valorLimite = Double.valueOf(valor);

        String[] de = {"imagem", "destino", "data", "total", "barraProgresso"};
        int[] para = {R.id.tipoViagem, R.id.destino, R.id.data, R.id.valor, R.id.barraProgresso};

        SimpleAdapter adapter =
                new SimpleAdapter(this, listarViagens(),
                        R.layout.lista_viagem, de, para);

        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
        adapter.setViewBinder(new ViagemViewBinder());

        registerForContextMenu(getListView());
        this.alertDialog = criarAlertDialog();
        this.dialogConfirmacao = criaDialogConfirmacao();

    }

    private List<Map<String, Object>>  listarViagens(){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery("SELECT _id, tipo_viagem, destino," +
                        "data_chegada, data_saida, orcamento FROM viagem;", null);

        cursor.moveToFirst();

        viagens = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < cursor.getCount(); i++){

            Map<String, Object> item = new HashMap<String, Object>();

            String id = cursor.getString(0);
            int tipoViagem = cursor.getInt(1);
            String destino = cursor.getString(2);
            long dataChegada = cursor.getLong(3);
            long dataSaida = cursor.getLong(4);
            double orcamento = cursor.getDouble(5);

            item.put("id", id);

            if (tipoViagem == Constantes.VIAGEM_LAZER){
                item.put("imagem", R.drawable.lazer);
            }else{
                item.put("imagem", R.drawable.negocios);
            }

            item.put("destino", destino);

            Date dataChegadaDate = new Date(dataChegada);
            Date dataSaidaDate = new Date(dataSaida);

            String periodo = dateFormat.format(dataChegadaDate) +
                    " a " + dateFormat.format(dataSaidaDate);

            double totalGasto = calcularTotalGasto(db, id);

            item.put("total", "Gasto total R$" + totalGasto);

            double alerta = orcamento * valorLimite / 100;
            Double [] valores = new Double[] {orcamento, alerta, totalGasto};

            item.put("barraProgresso", valores);

            viagens.add(item);

            cursor.moveToNext();

        }
        cursor.close();

        return viagens;

        //Código antigo (Sem banco) ------------------------

//        item.put("imagem", R.drawable.negocios);
//        item.put("destino", "São Paulo");
//        item.put("data", "02/02/2012 a 04/02/2012");
//        item.put("total", "Gasto total R$ 314,98");
//        item.put("barraProgresso",
//                new Double[]{500.0, 450.0, 314.98});
//        viagens.add(item);
//
//        item = new HashMap<String, Object>();
//        item.put("imagem", R.drawable.lazer);
//        item.put("destino", "Maceió");
//        item.put("data", "14/05/2012 a 22/05/2012");
//        item.put("total","Gasto total R$ 25834,67");
//        item.put("barraProgresso",
//                new Double[]{500.0, 490.0, 300.98});
//        viagens.add(item);
//
//        return viagens;
    }

    private double calcularTotalGasto(SQLiteDatabase db, String id){
        Cursor cursor = db.rawQuery(
                "SELECT SUM(valor) FROM gasto WHERE viagem_id = ?",
                new String[]{id});
        cursor.moveToFirst();
        double total = cursor.getDouble(0);
        cursor.close();
        return total;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        this.viagemSelecionada = i;
        alertDialog.show();
//        Map<String, Object> map = viagens.get(i);
//        String destino = (String) map.get("destino");
//        String mensagem = "Viagem selecinoada: "+ destino;
//        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
//        startActivity(new Intent(this, GastoListActivity.class));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gastos, menu);
    }

    private AlertDialog criarAlertDialog(){
        final CharSequence[] items = {
                getString(R.string.editar),
                getString(R.string.novo_gasto),
                getString(R.string.gastos_realizados),
                getString(R.string.remover)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.opcoes);
        builder.setItems(items, this);

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        Intent intent;
        String id =
                (String) viagens.get(viagemSelecionada).get("id");

        switch (i){
            case 0:
                intent = new Intent(this, ViagemActivity.class);
                intent.putExtra(Constantes.VIAGEM_ID, id);
                break;
            case 1:
                startActivity(new Intent(this, GastoActivity.class));
                break;
            case 2:
                startActivity(new Intent(this, GastoListActivity.class));
                break;
            case 3:
                dialogConfirmacao.show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                viagens.remove(viagemSelecionada);
                removerViagem(id);
                getListView().invalidateViews();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                dialogConfirmacao.dismiss();
                break;
        }

    }

    private AlertDialog criaDialogConfirmacao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmacao_exclusao_viagem);
        builder.setPositiveButton(getString(R.string.sim),this);
        builder.setNegativeButton(getString(R.string.nao),this);
        return builder.create();
    }

    private class ViagemViewBinder implements SimpleAdapter.ViewBinder{

        @Override
        public boolean setViewValue(View view, Object o, String s) {

            if(view.getId() == R.id.barraProgresso){
                Double valores[]= (Double[]) o;
                ProgressBar progressBar = (ProgressBar) view;
                progressBar.setMax(valores[0].intValue());
                progressBar.setSecondaryProgress(
                        valores[1].intValue());
                progressBar.setProgress(valores[2].intValue());
                return true;
            }

            return false;

        }
    }

    private void removerViagem(String id){
        SQLiteDatabase db = helper.getWritableDatabase();
        String where [] = new String[] {id};
        db.delete("gasto", "viagem_id = ?", where);
        db.delete("viagem", "_id = ?", where);
    }

}
