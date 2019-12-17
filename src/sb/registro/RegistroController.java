package sb.registro;


import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPDataListener;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorListener;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusListener;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.processing.DPFPTemplateStatus;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
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
import javafx.application.Platform;
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

    private DPFPEnrollment Reclutador =
    DPFPGlobal.getEnrollmentFactory().createEnrollment();
    private DPFPVerification Verificador =
    DPFPGlobal.getVerificationFactory().createVerification();
    private DPFPTemplate template;
    public static String TEMPLATE_PROPERTY = "template";
    private DPFPCapture lector = DPFPGlobal.getCaptureFactory().createCapture();
    
    public DPFPFeatureSet featuresinscripcion;
    public DPFPFeatureSet featuresverificacion;
    private Boolean activo;

    
    @FXML
    public void insert() throws SQLException{
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
                DPFPTemplate temp = getTemplate();
                if (temp != null) {
                    byte[] b = temp.serialize();
                    statement.setBytes(9, b);
                }
                //statement.setBytes(10, bytearray);
//                statement.setBinaryStream(10, (InputStream) fis, (int)file.length());
                statement.setBinaryStream(10, (InputStream) fis, (int)file.length());
                //ByteArrayInputStream datosHuella = new ByteArrayInputStream(getTemplate().serialize());

                //statement.setBinaryStream(10, datosHuella);
                
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
    
    /////////////////////////////////Metodos nuevos////////////////////////////////////////////////////////////

    public void Iniciar(){
        start();
        System.out.println("funciona");
        lector.addDataListener(new DPFPDataAdapter() {
            @Override 
            public void dataAcquired(final DPFPDataEvent e) {
                Platform.runLater(new Runnable(){                
                    @Override
                    public void run(){
                        try {
                            System.out.println("la huella se capturo");
                            ProcesarCaptura(e.getSample());
                        } catch (IOException ex) {
                            Logger.getLogger(RegistroController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }           
                });            
            }   
            
        });

        lector.addReaderStatusListener(new DPFPReaderStatusAdapter() {
            @Override 
            public void readerConnected(final DPFPReaderStatusEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        EnviarTexto("El Sensor de Huella Digital esta Activado o Conectado");
                    }
                });                
            }
            @Override 
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                Platform.runLater(new Runnable(){                
                    @Override
                    public void run(){
                        EnviarTexto("El Sensor de Huella Digital esta Desactivado o no Conectado");
                        
                    }           
                });
            }
        });

        lector.addSensorListener(new DPFPSensorAdapter() {
            @Override 
            public void fingerTouched(final DPFPSensorEvent e) {
                Platform.runLater(new Runnable(){                
                    @Override
                    public void run(){
                        EnviarTexto("El dedo ha sido colocado sobre el Lector de Huella");
                    }           
                });
            }
            @Override 
            public void fingerGone(final DPFPSensorEvent e) {
                Platform.runLater(new Runnable(){                
                    @Override
                    public void run(){
                        EnviarTexto("El dedo ha sido quitado del Lector de Huella");
                        
                    }           
                });
            }
                
        });

        lector.addErrorListener(new DPFPErrorAdapter(){
            public void errorReader(final DPFPErrorEvent e){
                Platform.runLater(new Runnable(){                
                    @Override
                    public void run(){
                        EnviarTexto("El dedo ha sido quitado del Lector de Huella");
                        
                    }           
                });
            }
        });
    }
    
    public void EnviarTexto(String msg){
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }
    
    public  void ProcesarCaptura(DPFPSample sample) throws IOException{
        System.out.println("entro a procesar");
        setTemplate(Reclutador.getTemplate());
        java.awt.Image imagen = DPFPGlobal.getSampleConversionFactory().createImage(sample);
        Image img = SwingFXUtils.toFXImage((BufferedImage) imagen, null);
        huella.setImage(img);
        
        //bytearray = new byte[(int)img];
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", byteOutput);
        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de inscripción.
        featuresinscripcion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de verificacion.
        featuresverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);
        
        // Comprobar la calidad de la muestra de la huella y lo añade a su reclutador si es bueno
        if (featuresinscripcion != null){
            System.out.println("entro al if");
            setFeaturesverificacion(featuresverificacion);
            try{
                System.out.println("Las Caracteristicas de la Huella han sido creada");
                activo = true;
                Reclutador.addFeatures(featuresinscripcion);// Agregar las caracteristicas de la huella a la plantilla a crear
            }catch (DPFPImageQualityException ex) {
                System.err.println("Error: "+ex.getMessage());
            }
            finally {
                
                EstadoHuellas();
                // Comprueba si la plantilla se ha creado.
                switch(Reclutador.getTemplateStatus()){                    
                    case TEMPLATE_STATUS_READY:	// informe de éxito y detiene  la captura de huellas
                        System.out.println("exito!");
                        setTemplate(Reclutador.getTemplate());
                        stop();
                        break;  

                    case TEMPLATE_STATUS_FAILED: // informe de fallas y reiniciar la captura de huellas
                        System.out.println("reinicio");
                        Reclutador.clear();
                        stop();
                        EstadoHuellas();
                        setTemplate(null);
                        //JOptionPane.showMessageDialog(Capturas.this, "La Plantilla de la Huella no pudo ser creada, Repita el Proceso", "Inscripcion de Huellas Dactilares", JOptionPane.ERROR_MESSAGE);
                        start();
                        break;
                }
	    }
        }
    }
    
    public  DPFPFeatureSet extraerCaracteristicas(DPFPSample sample, DPFPDataPurpose purpose){
        DPFPFeatureExtraction extractor = DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction();
        try {
            return extractor.createFeatureSet(sample, purpose);
        } catch (DPFPImageQualityException e) {
            return null;
        }
    }
    
    public DPFPTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DPFPTemplate template) {
        DPFPTemplate old = this.template;
	this.template = template;
	//firePropertyChange(TEMPLATE_PROPERTY, old, template);
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public DPFPFeatureSet getFeaturesverificacion() {
        return featuresverificacion;
    }

    public void setFeaturesverificacion(DPFPFeatureSet featuresverificacion) {
        this.featuresverificacion = featuresverificacion;
    }
    
    public DPFPTemplateStatus estadoHuellas(){
        return Reclutador.getTemplateStatus();
    }
    
    public  void start(){
	lector.startCapture();
	EnviarTexto("Utilizando el Lector de Huella Dactilar ");
    }

    public  void stop(){
        lector.stopCapture();
    //    EnviarTexto("No se está usando el Lector de Huella Dactilar ");
    }
    
    public  void EstadoHuellas(){
	//EnviarTexto("Muestra de Huellas Necesarias para Guardar Template "+ Reclutador.getFeaturesNeeded());
        System.out.println("Muestra de Huellas Necesarias para Guardar Template " + Reclutador.getFeaturesNeeded());
    }
    
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Iniciar();
    }    

    
    
}
