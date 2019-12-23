/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sb.login;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author L745
 */
public class LoginController implements Initializable {
    
    public PasswordField psf;
    /**
     * Initializes the controller class.
     */
    @FXML
    public void ingresar(Event event){
        if(psf.getText().equals("smart1993")){
            try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/sb/administrador/Administrador.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));  
            stage.show();
            }catch(Exception e) {
            e.printStackTrace();
            }
            ((Node)(event.getSource())).getScene().getWindow().hide();
        }else{
            new Alert(Alert.AlertType.WARNING, "La contraseña es incorrecta!").show();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
