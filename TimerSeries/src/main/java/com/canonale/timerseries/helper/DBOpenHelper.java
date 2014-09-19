package com.canonale.timerseries.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.canonale.timerseries.util.Constantes;

/**
 * Created by adrian on 31/07/13.
 */
public class DBOpenHelper extends SQLiteOpenHelper{

    private final static String DB_CREATE =
            "CREATE TABLE " + DBHelper.TABLE_NAME + " (id INTEGER PRIMARY KEY, nombre TEXT," +
                    " series INT, tiempo INTEGER, descanso INTEGER, auto BOOLEAN, seleccion BOOLEAN);";
    public DBOpenHelper(Context context) {
        super(context, DBHelper.DB_NAME, null, DBHelper.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL(DBOpenHelper.DB_CREATE);
        }catch (SQLiteException e){
            Log.e(Constantes.BASEDATOS, DBHelper.CLASSNAME, e);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase){
        super.onOpen(sqLiteDatabase);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXIST " + DBHelper.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}


