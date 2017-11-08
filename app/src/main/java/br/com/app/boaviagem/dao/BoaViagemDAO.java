package br.com.app.boaviagem.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import br.com.app.boaviagem.DatabaseHelper;

/**
 * Created by Lápis Lazulo on 17/10/2017.
 */

public class BoaViagemDAO {

    private DatabaseHelper helper;
    private SQLiteDatabase db;

    public BoaViagemDAO(Context context){
        helper = new DatabaseHelper(context);
    }

    private SQLiteDatabase getDb(){
        if (db == null){
            db = helper.getWritableDatabase();
        }
        return db;
    }

    public void close(){
        helper.close();
    }

}
