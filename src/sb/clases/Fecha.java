/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sb.clases;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author L745
 */
public class Fecha {
    
    
    public long diferencia(String fechita) {
        final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día 
        java.util.Date hoy = new Date(); //Fecha de hoy 
        System.out.println("fecha de hoy:" + hoy);
        
        //Aqui se va a cambiar los datos de fechita
        int año = 2019; int mes = 12; int dia = 8; //Fecha anterior 
        Calendar calendar = new GregorianCalendar(año, mes-1, dia); 
        java.sql.Date fecha = new java.sql.Date(calendar.getTimeInMillis());
        System.out.println("fecha de fin:"+fecha);
        long diferencia = ( hoy.getTime() - fecha.getTime() )/MILLSECS_PER_DAY; 
        diferencia = (diferencia - 1) * -1;
        System.out.println(diferencia); 
        return 0;    
    }
     
    
public static void main(String[]args){
    final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día 
    java.util.Date hoy = new Date(); //Fecha de hoy 
    System.out.println("fecha de hoy:" + hoy);

    int año = 2019; int mes = 12; int dia = 8; //Fecha anterior 
    Calendar calendar = new GregorianCalendar(año, mes-1, dia); 
    java.sql.Date fecha = new java.sql.Date(calendar.getTimeInMillis());
    System.out.println("fecha de fin:"+fecha);
    long diferencia = ( hoy.getTime() - fecha.getTime() )/MILLSECS_PER_DAY; 
    diferencia = (diferencia - 1) * -1;
    System.out.println(diferencia); 
    
}

}