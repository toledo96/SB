/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sb.reporte;


import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import sb.SQL.SQLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;


import sb.clases.Sensor;

/**
 * FXML Controller class
 *
 * @author L745
 */


//Tengo que combinar la clase sensor con esta clase para poder hacer las cosas bien y no me salga un nullpoint
public class ReporteController implements Initializable {

    Connection connection;    
    byte virificador;
    public DPFPFeatureSet featuresverificacion;
    //////////////////////////////////////////
    Sensor sensor = new Sensor();
    
    public void identificarHuella() throws IOException, SQLException{        

        try {

            this.connection = new SQLConnection("root", "", "sb").getConnection();

            System.out.println("Conexion Lista");

        } catch (ClassNotFoundException | SQLException e) {

            System.out.println("Conexión no realizada" + e.getMessage());

        }
        
     try {
       //Establece los valores para la sentencia SQL
       
       
       //Obtiene todas las huellas de la bd
       PreparedStatement identificarStmt = connection.prepareStatement("SELECT huella FROM cliente");
       ResultSet rs = identificarStmt.executeQuery();

       //Si se encuentra el nombre en la base de datos
       while(rs.next()){
       //Lee la plantilla de la base de datos
       byte templateBuffer[] = rs.getBytes("huella");
       //Crea una nueva plantilla a partir de la guardada en la base de datos
       DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
       //Envia la plantilla creada al objeto contendor de Template del componente de huella digital
       sensor.setTemplate(referenceTemplate);

       // Compara las caracteriticas de la huella recientemente capturda con la
       // alguna plantilla guardada en la base de datos que coincide con ese tipo
       DPFPVerificationResult result = sensor.Verificador.verify(sensor.getFeaturesverificacion(), sensor.getTemplate());

       //compara las plantilas (actual vs bd)
       //Si encuentra correspondencia dibuja el mapa
       //e indica el nombre de la persona que coincidió.
       if (result.isVerified()){
       //crea la imagen de los datos guardado de las huellas guardadas en la base de datos
            System.out.println("bienvenido");
       return;
                               }
       }
       //Si no encuentra alguna huella correspondiente al nombre lo indica con un mensaje
         System.out.println("fuera prro");
       sensor.setTemplate(null);
       } catch (SQLException e) {
       //Si ocurre un error lo indica en la consola
       System.err.println("Error al identificar huella dactilar."+e.getMessage());
       }finally{
       connection.close();
       }
   }
    

    @Override
    public void initialize(URL url, ResourceBundle rb){        

        
    }    

 
    
    
}