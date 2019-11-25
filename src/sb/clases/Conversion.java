/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sb.clases;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * @author L745
 */
public class Conversion {

    public double x;
    
public void binario(String ip){
        ArrayList<String> ips = new ArrayList<String>();
        String[] nueva_ip = ip.split("\\.");
        
        for (int i = 0; i < nueva_ip.length;i++){
            
            String numero_almacenado = bin(nueva_ip[i]);
            ips.add(numero_almacenado);
            
        }        
        System.out.println(ips);
}

    public String conversion(double valor)
    {
      Locale.setDefault(Locale.US);
      DecimalFormat num = new DecimalFormat("#");
      return num.format(valor);
    }
 
 

public String bin(String ip){
   int numero, exp, digito;
   double binario;
   numero = Integer.valueOf(ip);
    exp=0;
    binario=0;
    while(numero!=0){
        digito = numero % 2;           
        binario = binario + digito * Math.pow(10, exp);  
        exp++;
        numero = numero/2;
     }
    
    String nuevo_bin = conversion(binario); //Aqui se agrega la funcion convertir
    System.out.println(nuevo_bin);
    return nuevo_bin;
    
}
    
  public static void main(String args[]){
      Conversion con = new Conversion();
       con.binario("192.254.1.28");
    
    
  }
    
}
