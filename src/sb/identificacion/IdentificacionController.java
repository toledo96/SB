/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sb.identificacion;

import com.digitalpersona.onetouch.DPFPDataPurpose;
import com.digitalpersona.onetouch.DPFPFeatureSet;
import com.digitalpersona.onetouch.DPFPGlobal;
import com.digitalpersona.onetouch.DPFPSample;
import com.digitalpersona.onetouch.DPFPTemplate;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.capture.event.DPFPErrorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPErrorEvent;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPReaderStatusEvent;
import com.digitalpersona.onetouch.capture.event.DPFPSensorAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPSensorEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPFeatureExtraction;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.processing.DPFPTemplateStatus;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import sb.SQL.SQLConnection;
import sb.reporte.ReporteController;

/**
 * FXML Controller class
 *
 * @author L745
 */
public class IdentificacionController implements Initializable {
    
    Connection connection;    
    byte virificador;
    byte[] bytearray;
    private Boolean activo;
    //////////////////////////////////////////
    //Sensor sensor = new Sensor();
    private DPFPCapture lector = DPFPGlobal.getCaptureFactory().createCapture();
    private DPFPEnrollment Reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    private DPFPVerification Verificador = DPFPGlobal.getVerificationFactory().createVerification();
    private DPFPTemplate template;
    public static String TEMPLATE_PROPERTY = "template";
    public DPFPFeatureSet featuresinscripcion;
    public DPFPFeatureSet featuresverificacion;
    public Label label_name;
    public Label label_end;
    
    
    
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
       PreparedStatement identificarStmt = connection.prepareStatement("SELECT vencimiento,nombre,huella FROM cliente");
       ResultSet rs = identificarStmt.executeQuery();

       //Si se encuentra el nombre en la base de datos
       while(rs.next()){
       //Lee la plantilla de la base de datos
       byte templateBuffer[] = rs.getBytes("huella");
        String nombre=rs.getString("nombre");
        Date venimiento = rs.getDate("vencimiento");
       //Crea una nueva plantilla a partir de la guardada en la base de datos
       DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
       //Envia la plantilla creada al objeto contendor de Template del componente de huella digital
       setTemplate(referenceTemplate);
       System.out.println(getTemplate());
       // Compara las caracteriticas de la huella recientemente capturda con la
       // alguna plantilla guardada en la base de datos que coincide con ese tipo
       
       DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());

       //compara las plantilas (actual vs bd)
       //Si encuentra correspondencia dibuja el mapa
       //e indica el nombre de la persona que coincidió.
       if (result.isVerified()){
       //crea la imagen de los datos guardado de las huellas guardadas en la base de datos
            System.out.println("bienvenido" + " " + nombre);
            label_name.setText("Bienvenido"+" "+nombre);
            label_end.setText("Su mensualidad vence el:"+" "+venimiento);
            stop();
       return;       
       }
       }
       
       //Si no encuentra alguna huella correspondiente al nombre lo indica con un mensaje

       } catch (SQLException e) {
       //Si ocurre un error lo indica en la consola
       System.err.println("Error al identificar huella dactilar."+e.getMessage());
       }finally{
       connection.close();
       }
     stop();
   }
    
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

     
    public void Iniciar() {
        //EnviarTexto("Funciona");
        start();
//        System.out.println("funciona");
        lector.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(final DPFPDataEvent e) {
//                System.out.println("aqui paso");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
//                        System.out.println("la huella se capturo");
                        try {
                            ProcesarCaptura(e.getSample());
                            identificarHuella();
                        } catch (IOException ex) {
                            Logger.getLogger(ReporteController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (SQLException ex) {
                            Logger.getLogger(ReporteController.class.getName()).log(Level.SEVERE, null, ex);
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
//                        EnviarTexto("El Sensor de Huella Digital esta Activado o Conectado");
                    }
                });
            }

            @Override
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
//                        EnviarTexto("El Sensor de Huella Digital esta Desactivado o no Conectado");

                    }
                });
            }
        });

        lector.addSensorListener(new DPFPSensorAdapter() {
            @Override
            public void fingerTouched(final DPFPSensorEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
//                        EnviarTexto("El dedo ha sido colocado sobre el Lector de Huella");
                    }
                });
            }

            @Override
            public void fingerGone(final DPFPSensorEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
//                        EnviarTexto("El dedo ha sido quitado del Lector de Huella");

                    }
                });
            }

        });

        lector.addErrorListener(new DPFPErrorAdapter() {
            public void errorReader(final DPFPErrorEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
//                        EnviarTexto("El dedo ha sido quitado del Lector de Huella");

                    }
                });
            }
        });
    }
    
    public void EnviarTexto(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }

    public void ProcesarCaptura(DPFPSample sample) {
        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de inscripción.
        featuresinscripcion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de verificacion.
        featuresverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);

        // Comprobar la calidad de la muestra de la huella y lo añade a su reclutador si es bueno
        if (featuresinscripcion != null) {
            setFeaturesverificacion(featuresverificacion);
            try {
                System.out.println("Las Caracteristicas de la Huella han sido creada");
                activo = true;
                Reclutador.addFeatures(featuresinscripcion);// Agregar las caracteristicas de la huella a la plantilla a crear
            } catch (DPFPImageQualityException ex) {
                System.err.println("Error: " + ex.getMessage());
            } finally {
                // Comprueba si la plantilla se ha creado.
                switch (Reclutador.getTemplateStatus()) {
                    case TEMPLATE_STATUS_READY:	// informe de éxito y detiene  la captura de huellas
                        setTemplate(Reclutador.getTemplate());
                        break;
                    case TEMPLATE_STATUS_FAILED: // informe de fallas y reiniciar la captura de huellas
                        Reclutador.clear();
                        stop();
                        setTemplate(null);
                        start();
                        break;
                }
            }
        }
    }
    
    public DPFPTemplateStatus estadoHuellas() {
        return Reclutador.getTemplateStatus();
    }
    
    public DPFPFeatureSet getFeaturesverificacion() {
        return featuresverificacion;
    }

    public void setFeaturesverificacion(DPFPFeatureSet featuresverificacion) {
        this.featuresverificacion = featuresverificacion;
    }
    
    public DPFPFeatureSet extraerCaracteristicas(DPFPSample sample, DPFPDataPurpose purpose) {
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
    }

    public void start() {
        lector.startCapture();
    }

    public void stop() {
        lector.stopCapture();
    }
    
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Iniciar();
        if(label_name.getText().isEmpty() && label_end.getText().isEmpty()){
            stop();
        }
    }    
    
}
