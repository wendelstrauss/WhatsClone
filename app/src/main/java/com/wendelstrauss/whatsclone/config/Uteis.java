package com.wendelstrauss.whatsclone.config;

import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Uteis {

    public static String getData() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String data = df.format( c.getTime() );

        return data;
    }
    public static String getHora() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String hora = df.format( c.getTime() );
        return hora;
    }
}
