package com.canonale.timerseries.helper;


/**
 * Created by adrian on 31/07/13.
 */
public class Cronometro {
    public long id;
    public String nombre;
    public int series;
    public long tiempo;
    public long descanso;
    public boolean auto;
    public boolean seleccion;

    public Cronometro(){}
    public Cronometro(long id, String nombre,
                      int series, long tiempo,
                      long descanso, boolean auto, boolean seleccion){
        this.id = id;
        this.nombre = nombre;
        this.series = series;
        this.tiempo = tiempo;
        this.descanso = descanso;
        this.auto = auto;
        this.seleccion = seleccion;
    }

    @Override
    public String toString()
    {
        return this.nombre + ": " + String.valueOf(this.tiempo) + " - " + String.valueOf(this.descanso);
    }

    public String getHoras(long millis){
        int sec  = (int)(millis/ 1000) % 60 ;
        int min  = (int)((millis/ (1000*60)) % 60);
        int hr   = (int)((millis/ (1000*60*60)) % 24);
        return String.format("%02d:%02d:%02d", hr, min, sec);
    }
    public String getCentesimas(long millis){
        int mil  = (int) millis % 1000;
        return String.format(".%03d", mil);
    }

    public long getMilesimas(String hora, String centesimas){
        String[] digitos = hora.split(":");
        long sec = (long)(Integer.parseInt(digitos[2]) * 1000);
        long min = (long)(Integer.parseInt(digitos[1]) * 1000 * 60);
        long hr = (long)(Integer.parseInt(digitos[0]) * 1000 * 60 * 60);
        long mil = (long)(Integer.parseInt(centesimas) * 1);

        return (mil + hr + min + sec);
    }

    public long getMilesimas(String hora){
        String[] digitos = hora.split(":");
        long sec = (long)(Integer.parseInt(digitos[2]) * 1000);
        long min = (long)(Integer.parseInt(digitos[1]) * 1000 * 60);
        long hr = (long)(Integer.parseInt(digitos[0]) * 1000 * 60 * 60);
        return (hr + min + sec);
    }
    public void setId(long id){
        this.id = id;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    public void setSeries(int series){
        this.series = series;
    }
    public void setTiempo(long tiempo){
        this.tiempo = tiempo;
    }
    public void setDescanso(long descanso){
        this.descanso = descanso;
    }
    public void setAuto(boolean auto){
        this.auto = auto;
    }
    public void setSeleccion(boolean seleccion){
        this.seleccion = seleccion;
    }

    public long getId(){
        return this.id;
    }
    public String getNombre(){
        return this.nombre;
    }
    public int getSeries(){
        return this.series;
    }
    public long getTiempo(){
        return this.tiempo;
    }
    public long getDescanso(){
        return this.descanso;
    }
    public boolean getAuto(){
        return this.auto;
    }
    public boolean getSeleccion(){
        return this.seleccion;
    }
}

