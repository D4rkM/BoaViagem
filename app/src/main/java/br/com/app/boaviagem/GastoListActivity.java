package br.com.app.boaviagem;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static br.com.app.boaviagem.R.id.descricao;

/**
 * Created by 16254861 on 09/10/2017.
 */

public class GastoListActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private List<Map<String, Object>> gasto;
    private DatabaseHelper helper;
    private SimpleDateFormat dateFormat;
    private Double valorLimite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        helper = new DatabaseHelper(this);

        String[] de = {"data", "descricao", "valor", "categoria"};
        int[] para = {R.id.data, descricao, R.id.valor, R.id.categoria};

        SimpleAdapter adapter = new SimpleAdapter(this, listarGastos(), R.layout.lista_gasto, de, para);

        adapter.setViewBinder(new GastoViewBinder());

        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id){
        Map<String,Object> map = gasto.get(position);
        String descricao = (String) map.get("descricao");
        String mensagem = "Gasto selecionado: " + descricao;
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }

    private List<Map<String, Object>> listarGastos(){

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor =
                db.rawQuery("SELECT * FROM gasto;", null);

        cursor.moveToFirst();

        gasto = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < cursor.getCount(); i++){

            Map<String, Object> item = new HashMap<>();

            String id = cursor.getString(0);
            String categoria = cursor.getString(1);
            long data = cursor.getLong(2);
            Double valor = cursor.getDouble(3);
            String descricao = cursor.getString(4);
            String local = cursor.getString(5);
            int viagem_id = cursor.getInt(6);

            item.put("id", id);

            Date dataGasto = new Date(data);

//            double totalGasto = calcularTotalGasto(db, id);

            gasto.add(item);

            cursor.moveToNext();

        }
//        gastos = new ArrayList<Map<String,Object>>();
//
//        Map<String, Object> item = new HashMap<String, Object>();
//
//        item.put("data", "04/02/2012");
//        item.put("descricao", "Diária Hotel");
//        item.put("valor", "R$ 260,00");
//        item.put("categoria", R.color.categoria_hospedagem);
//        gastos.add(item);
        cursor.close();
        return gasto;

    }

    private String dataAnterior = "";

    private class GastoViewBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {

            if(view.getId() == R.id.data){
                if(!dataAnterior.equals(data)){
                    TextView textView = (TextView) view;
                    textView.setText(textRepresentation);
                    dataAnterior = textRepresentation;
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.GONE);
                }
                return true;
            }

            if(view.getId() == R.id.categoria){
                Integer id = (Integer) data;
                view.setBackgroundColor(getResources().getColor(id));
                return true;
            } else if(view.getId()==0) {
                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                return true;
            }
            return false;
        }
    }

}
