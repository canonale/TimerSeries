package com.canonale.timerseries.helper;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.canonale.timerseries.helper.DBOpenHelper;
import com.canonale.timerseries.util.Constantes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrian on 31/07/13.
 */
public class DBHelper {
    public static final String DB_NAME = "timer";
    public static final String TABLE_NAME = "series";
    public static final int VERSION = 1;
    public static final String CLASSNAME = DBHelper.class.getSimpleName();
    public static final String[] COLS = new String[]{"id", "nombre", "series", "tiempo", "descanso", "auto", "seleccion"};
    private SQLiteDatabase db;
    private  DBOpenHelper dbOpenHelper;

    public DBHelper(Context context){
        this.dbOpenHelper = new DBOpenHelper(context);
        establishDB();
    }

    private void establishDB(){
        if (this.db == null) this.db = this.dbOpenHelper.getWritableDatabase();
    }

    public void cleanUp(){
        if (this.db != null){
            this.db.close();
            this.db = null;
        }
    }
    public void insert(Cronometro cronometro){
        ContentValues contentValues = new ContentValues();
        contentValues.put("nombre", cronometro.getNombre());
        contentValues.put("series", cronometro.getSeries());
        contentValues.put("tiempo", cronometro.getTiempo());
        contentValues.put("descanso", cronometro.getDescanso());
        contentValues.put("auto", cronometro.getAuto());
        contentValues.put("seleccion", cronometro.getSeleccion());

        this.db.insert(this.TABLE_NAME, null, contentValues);
    }

    public void update(Cronometro cronometro){
        ContentValues contentValues = new ContentValues();
        contentValues.put("nombre", cronometro.getNombre());
        contentValues.put("series", cronometro.getSeries());
        contentValues.put("tiempo", cronometro.getTiempo());
        contentValues.put("descanso", cronometro.getDescanso());
        contentValues.put("auto", cronometro.getAuto());
        contentValues.put("seleccion", cronometro.getSeleccion());

        this.db.update(this.TABLE_NAME, contentValues, "id = " + cronometro.getId(), null);
    }

    public void delete(String nombre){
        this.db.delete(this.TABLE_NAME, "nombre = '" + nombre + "'", null);
    }

    public void delete(int id){
        this.db.delete(this.TABLE_NAME, "id=" + id, null);
    }

    public Cronometro get(String nombre){
        Cursor cursor = null;
        Cronometro cronometro = null;
        try{
            cursor = this.db.query(this.TABLE_NAME, this.COLS, "nombre='" + nombre + "'", null, null, null, null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                cronometro = new Cronometro();
                boolean auto = cursor.getInt(5)>0;
                boolean seleccion = cursor.getInt(6)>0;
                cronometro.setId(cursor.getLong(0));
                cronometro.setNombre(cursor.getString(1));
                cronometro.setSeries(cursor.getInt(2));
                cronometro.setTiempo(cursor.getLong(3));
                cronometro.setDescanso(cursor.getLong(4));
                cronometro.setAuto(auto);
                cronometro.setSeleccion(seleccion);
            }
        }catch (SQLiteException s){
            Log.e(Constantes.BASEDATOS, "Fallo al obtener datos", s);
        }finally {
            if ((cursor != null) && (!cursor.isClosed())) cursor.close();
        }
        return cronometro;
    }

    public int length(){
        Cursor c = null;
        c = this.db.query(this.TABLE_NAME, this.COLS, null, null, null, null, null);

        return c.getCount();

    }

    public Cronometro get(int id){
        Cursor cursor = null;
        Cronometro cronometro = null;
        try{
            cursor = this.db.query(this.TABLE_NAME, this.COLS, "id=" + id, null, null, null, null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                cronometro = new Cronometro();
                boolean auto = cursor.getInt(5)>0;
                boolean seleccion = cursor.getInt(6)>0;
                cronometro.setId(cursor.getLong(0));
                cronometro.setNombre(cursor.getString(1));
                cronometro.setSeries(cursor.getInt(2));
                cronometro.setTiempo(cursor.getLong(3));
                cronometro.setDescanso(cursor.getLong(4));
                cronometro.setAuto(auto);
                cronometro.setSeleccion(seleccion);
            }
        }catch (SQLiteException s){
            Log.e(Constantes.BASEDATOS, "Fallo al obtener datos", s);
        }finally {
            if ((cursor != null) && (!cursor.isClosed())) cursor.close();
        }
        return cronometro;
    }

    public ArrayList<Cronometro> getAll(){
        Cursor cursor = null;
        Cronometro cronometro = null;
        ArrayList<Cronometro> cronometros = new ArrayList<Cronometro>();
        try{
            cursor = this.db.query(this.TABLE_NAME, this.COLS, null, null, null, null, null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                int count = cursor.getCount();
                for (int i = 0; i < count; i++){
                    cronometro = new Cronometro();
                    boolean auto = cursor.getInt(5)>0;
                    boolean seleccion = cursor.getInt(6)>0;
                    cronometro.setId(cursor.getLong(0));
                    cronometro.setNombre(cursor.getString(1));
                    cronometro.setSeries(cursor.getInt(2));
                    cronometro.setTiempo(cursor.getLong(3));
                    cronometro.setDescanso(cursor.getLong(4));
                    cronometro.setAuto(auto);
                    cronometro.setSeleccion(seleccion);
                    cursor.moveToNext();
                    cronometros.add(cronometro);
                }
            }
        }catch (SQLiteException s){
            Log.e(Constantes.BASEDATOS, "Fallo al obtener datos", s);
        }finally {
            if ((cursor != null) && (!cursor.isClosed())) cursor.close();
        }
        return cronometros;
    }

    public Cronometro getLast(){
        Cursor cursor = null;
        Cronometro cronometro = null;
        try{
            //cursor = this.db.query(this.TABLE_NAME, this.COLS, "id=" + id, null, null, null, null);
            cursor = this.db.query(this.TABLE_NAME, this.COLS, null, null, null, null, "id DESC", "1");
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                cronometro = new Cronometro();
                boolean auto = cursor.getInt(5)>0;
                boolean seleccion = cursor.getInt(6)>0;
                cronometro.setId(cursor.getLong(0));
                cronometro.setNombre(cursor.getString(1));
                cronometro.setSeries(cursor.getInt(2));
                cronometro.setTiempo(cursor.getLong(3));
                cronometro.setDescanso(cursor.getLong(4));
                cronometro.setAuto(auto);
                cronometro.setSeleccion(seleccion);
            }
        }catch (SQLiteException s){
            Log.e(Constantes.BASEDATOS, "Fallo al obtener datos", s);
        }finally {
            if ((cursor != null) && (!cursor.isClosed())) cursor.close();
        }
        return cronometro;
    }

    public long getLastId(){
        Cursor cursor = null;
        long id = 0;
        try{
            //cursor = this.db.query(this.TABLE_NAME, this.COLS, "id=" + id, null, null, null, null);
            cursor = this.db.query(this.TABLE_NAME, this.COLS, null, null, null, null, "id DESC", "1");
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                id = cursor.getLong(0);
            }
        }catch (SQLiteException s){
            Log.e(Constantes.BASEDATOS, "Fallo al obtener datos", s);
        }finally {
            if ((cursor != null) && (!cursor.isClosed())) cursor.close();
        }
        return id;
    }

    public void clear(){
        this.db.delete(this.TABLE_NAME, "1", null);
    }

    public void usarSerie(int id){
        String todo_false = "UPDATE " +
                this.TABLE_NAME + " set seleccion = 0 where 1";
        String activar = "UPDATE " +
                this.TABLE_NAME + " set seleccion = 1 " +
                "where id = " + id;
        this.db.execSQL(todo_false);
        this.db.execSQL(activar);

    }

    public Cronometro getSeleccion(){
        Cursor cursor = null;
        Cronometro cronometro = null;
        try{
            cursor = this.db.query(this.TABLE_NAME, this.COLS, "seleccion = 1", null, null, null, null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                cronometro = new Cronometro();
                boolean auto = cursor.getInt(5)>0;
                boolean seleccion = cursor.getInt(6)>0;
                cronometro.setId(cursor.getLong(0));
                cronometro.setNombre(cursor.getString(1));
                cronometro.setSeries(cursor.getInt(2));
                cronometro.setTiempo(cursor.getLong(3));
                cronometro.setDescanso(cursor.getLong(4));
                cronometro.setAuto(auto);
                cronometro.setSeleccion(seleccion);
            }
        }catch (SQLiteException s){
            Log.e(Constantes.BASEDATOS, "Fallo al obtener datos", s);
        }finally {
            if ((cursor != null) && (!cursor.isClosed())) cursor.close();
        }
        return cronometro;
    }

}
