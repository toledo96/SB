/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sb.clases;

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
import static com.digitalpersona.onetouch.processing.DPFPTemplateStatus.TEMPLATE_STATUS_READY;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javax.swing.SwingUtilities;


/**
 *
 * @author L745
 */
public class Sensor{
    
    private final DPFPCapture Lector = DPFPGlobal.getCaptureFactory().createCapture();
    public final DPFPEnrollment Reclutador = DPFPGlobal.getEnrollmentFactory().createEnrollment();
    public final DPFPVerification Verificador = DPFPGlobal.getVerificationFactory().createVerification();
    private DPFPTemplate template;
    public static String TEMPLATE_PROPERTY = "template";
    
    public DPFPFeatureSet featuresinscripcion;
    public DPFPFeatureSet featuresverificacion;
    
    private Boolean activo;

    public Sensor() {
        this.activo = false;
    }

    public DPFPFeatureSet getFeaturesverificacion() {
        return featuresverificacion;
    }

    public void setFeaturesverificacion(DPFPFeatureSet featuresverificacion) {
        this.featuresverificacion = featuresverificacion;
    }
    
    
    public void Iniciar(){
        //EnviarTexto("Funciona");
        System.out.println("funciona");
        Lector.addDataListener(new DPFPDataAdapter() {
            @Override 
            public void dataAcquired(final DPFPDataEvent e) {
                System.out.println("aqui paso");
//                SwingUtilities.invokeLater(() -> {
//                    //EnviarTexto("La Huella Digital ha sido Capturada");
//                    System.out.println("la huella se capturo");
//                    ProcesarCaptura(e.getSample());                         
//                });
                
                Platform.runLater(new Runnable(){                
                    @Override
                    public void run(){
                        System.out.println("la huella se capturo");
                        ProcesarCaptura(e.getSample());
                    }           
                });            
            }   
            
        });

        Lector.addReaderStatusListener(new DPFPReaderStatusAdapter() {
            @Override 
            public void readerConnected(final DPFPReaderStatusEvent e) {
//                SwingUtilities.invokeLater(() -> {
//                    EnviarTexto("El Sensor de Huella Digital esta Activado o Conectado");
//                });
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        EnviarTexto("El Sensor de Huella Digital esta Activado o Conectado");
                    }
                });
                
            }
            @Override 
            public void readerDisconnected(final DPFPReaderStatusEvent e) {
//                SwingUtilities.invokeLater(() -> {
//                    EnviarTexto("El Sensor de Huella Digital esta Desactivado o no Conectado");
//                });
                Platform.runLater(new Runnable(){                
                    @Override
                    public void run(){
                        EnviarTexto("El Sensor de Huella Digital esta Desactivado o no Conectado");
                        
                    }           
                });
            }
        });

        Lector.addSensorListener(new DPFPSensorAdapter() {
            @Override 
            public void fingerTouched(final DPFPSensorEvent e) {
//                SwingUtilities.invokeLater(() -> {
//                    EnviarTexto("El dedo ha sido colocado sobre el Lector de Huella");
//                });
                Platform.runLater(new Runnable(){                
                    @Override
                    public void run(){
                        EnviarTexto("El dedo ha sido colocado sobre el Lector de Huella");
                    }           
                });
            }
            @Override 
            public void fingerGone(final DPFPSensorEvent e) {
//                SwingUtilities.invokeLater(() -> {
//                    EnviarTexto("El dedo ha sido quitado del Lector de Huella");
//                });
                Platform.runLater(new Runnable(){                
                    @Override
                    public void run(){
                        EnviarTexto("El dedo ha sido quitado del Lector de Huella");
                        
                    }           
                });
            }
                
        });

        Lector.addErrorListener(new DPFPErrorAdapter(){
            public void errorReader(final DPFPErrorEvent e){
//                SwingUtilities.invokeLater(() -> {
//                    EnviarTexto("Error: "+e.getError());
//                });
                Platform.runLater(new Runnable(){                
                    @Override
                    public void run(){
                        EnviarTexto("El dedo ha sido quitado del Lector de Huella");
                        
                    }           
                });
            }
        });
    }
    
    public  void ProcesarCaptura(DPFPSample sample){
        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de inscripción.
        featuresinscripcion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);

        // Procesar la muestra de la huella y crear un conjunto de características con el propósito de verificacion.
        featuresverificacion = extraerCaracteristicas(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);
        
        // Comprobar la calidad de la muestra de la huella y lo añade a su reclutador si es bueno
        if (featuresinscripcion != null){
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
                        //Reclutador.clear();
                        setTemplate(Reclutador.getTemplate());
                        break;
                    case TEMPLATE_STATUS_FAILED: // informe de fallas y reiniciar la captura de huellas
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
    public  void EstadoHuellas(){
	EnviarTexto("Muestra de Huellas Necesarias para Guardar Template "+ Reclutador.getFeaturesNeeded());
        System.out.println("Muestra de Huellas Necesarias para Guardar Template " + Reclutador.getFeaturesNeeded());
    }
    
    public DPFPTemplateStatus estadoHuellas(){
        return Reclutador.getTemplateStatus();
    }
    
 
    public  void start(){
	Lector.startCapture();
	EnviarTexto("Utilizando el Lector de Huella Dactilar ");
    }

    public  void stop(){
        Lector.stopCapture();
    //    EnviarTexto("No se está usando el Lector de Huella Dactilar ");
    }
    
    public DPFPTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DPFPTemplate template) {
        DPFPTemplate old = this.template;
	this.template = template;
	//firePropertyChange(TEMPLATE_PROPERTY, old, template);
    }
    
    public void EnviarTexto(String msg){
        new Alert(Alert.AlertType.INFORMATION, msg).show();
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    public void clear(){
        setTemplate(null);
        Reclutador.clear();
        featuresinscripcion = null;
        stop();
        start();
        activo = false;
    }
    
    public Boolean verificarHuella(byte templateBuffer[]){
        //byte templateBuffer[] = resultado.getBytes("huella");
        //int id_empleado_c = resultado.getInt("idempleado");
        if(templateBuffer != null){
            DPFPTemplate referenceTemplate = DPFPGlobal.getTemplateFactory().createTemplate(templateBuffer);
            setTemplate(referenceTemplate);
            if(featuresinscripcion == null) return false;
            DPFPVerificationResult result = Verificador.verify(featuresverificacion, getTemplate());
            return result.isVerified();
        }
        return false;
    }
    
    
}
