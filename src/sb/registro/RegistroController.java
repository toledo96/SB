package sb.registro;


import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPDataListener;
import com.digitalpersona.onetouch.capture.event.DPFPErrorListener;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusListener;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import sb.SQL.SQLConnection;
 

/**
 * FXML Controller class
 *
 * @author L745
 */
public class RegistroController implements Initializable {

    public TextField tf_nombre,tf_apellido_materno,tf_apellido_paterno,tf_edad,tf_telefono;
    public DatePicker inicio,fin;
    public TextArea observaciones;
    Connection connection;
    public FileInputStream fis,fis2; 
    public Image image;
    public ImageView usuario,huella;
    File file,file2;
    byte[] bytearray;
    boolean imagen = false;
    //para validar la imagen es con una variable boolena 
    /////////////////////////////////////////
    private DPFPCapture Lector = DPFPGlobal.getCaptureFactory().createCapture();
    private DPFPEnrollment Reclutador =
    DPFPGlobal.getEnrollmentFactory().createEnrollment();
    private DPFPVerification Verificador =
    DPFPGlobal.getVerificationFactory().createVerification();
    private DPFPTemplate template;
    public static String TEMPLATE_PROPERTY = "template";
    private DPFPCapture lector = DPFPGlobal.getCaptureFactory().createCapture();
    
    @FXML
    public void insert() throws SQLException{
        System.out.println("fis" + fis);
        System.out.println("file" + file);
            try {

                this.connection = new SQLConnection("root", "", "sb").getConnection();

                System.out.println("Conexion Lista");

            } catch (ClassNotFoundException | SQLException e) {

                System.out.println("Conexión no realizada"+e.getMessage());

            }            
            String nombre = tf_nombre.getText();
            String paterno = tf_apellido_paterno.getText();
            String materno = tf_apellido_materno.getText();
            int edad = Integer.valueOf(tf_edad.getText());
            String telefono = tf_telefono.getText();
            LocalDate i = inicio.getValue();
            LocalDate f = fin.getValue();
            String observacion = observaciones.getText();
            
//            while(){
//                
//            }
            

            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO cliente"
                + "( nombre,apellido_paterno,apellido_materno,edad,telefono,inicio,vencimiento,observaciones,huella,foto) "
                + "VALUES(?,?,?,?,?,?,?,?,?,?) "
                );                
                statement.setString(1, nombre);
                statement.setString(2, paterno);
                statement.setString(3, materno);
                statement.setInt(4, edad);
                statement.setString(5, telefono);
                statement.setDate(6, Date.valueOf(i));
                statement.setDate(7, Date.valueOf(f));
                statement.setString(8, observacion);                
                statement.setBytes(9, bytearray);
                statement.setBinaryStream(10, (InputStream) fis, (int)file.length());
                
                int executeUpdate = statement.executeUpdate();
                System.out.println(executeUpdate);
                new Alert(Alert.AlertType.INFORMATION, "Agregado!").show();

            } else {
                new Alert(Alert.AlertType.ERROR, "Error!").show();
            }   
    }
    
    @FXML
    public void image() throws FileNotFoundException{
        FileChooser fc = new FileChooser();
        file = fc.showOpenDialog(null);
        System.out.println("fie:"+file.toString());
        fis = new FileInputStream(file);// file is selected using filechooser which is in last tutorial
        if(file != null){
            System.out.println("hola");
            image = new Image(file.toURI().toString());
            usuario.setImage(null);
            usuario.setImage(image);
            imagen = true;            
        }else{
            System.out.println("vacio!");
        }
    }
    
    /////////////////////////////////Metodos nuevos////////////////////////////////////////////
    @FXML
    protected void procesarHuella(DPFPSample sample) throws IOException{
//        lector.startCapture();
        System.out.println("aqui paso");
        java.awt.Image imagen = DPFPGlobal.getSampleConversionFactory().createImage(sample);
        Image img = SwingFXUtils.toFXImage((BufferedImage) imagen, null);
        huella.setImage(img);           
        //bytearray = new byte[(int)img];
        ByteArrayOutputStream  byteOutput = new ByteArrayOutputStream();
        ImageIO.write( SwingFXUtils.fromFXImage( img, null ), "png", byteOutput );        
        bytearray = byteOutput.toByteArray();
    }
    
    @FXML
    public void activar(){
        lector.startCapture();
        System.out.println("se ha actviado");
        lector.addDataListener((DPFPDataEvent dpfpde) -> {
            try {
                procesarHuella(dpfpde.getSample());
            } catch (IOException ex) {
                Logger.getLogger(RegistroController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        System.out.println("hola");
        ///Esto obtiene la huella 
        lector.addDataListener(new DPFPDataListener() {
             @Override 
             public void dataAcquired(DPFPDataEvent dpfpde) {
                 System.out.println(dpfpde);
                 
             }
         });
         
        lector.addReaderStatusListener(new DPFPReaderStatusListener() {
            @Override
            public void readerConnected(DPFPReaderStatusEvent dpfprs) {
                
            }

            @Override
            public void readerDisconnected(DPFPReaderStatusEvent dpfprs) {
                
            }
        });
    }
    
    @FXML
    public void desactivar(){
        lector.stopCapture();
        System.out.println("desactivado");
    }
            
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    //aqui va a ir el metodo init de la huella y lo demas    
    }    

    
    
}
