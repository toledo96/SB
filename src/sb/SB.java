
package sb;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sb.SQL.SQLConnection;

/**
 *
 * @author L745
 */
public class SB extends Application {
    public static SQLConnection connection;
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("/sb/identificacion/Identificacion.fxml"));
//        Parent root = FXMLLoader.load(getClass().getResource("/sb/registro/Registro.fxml"));
        Scene scene = new Scene(root);
        this.initConnection();
        stage.setScene(scene);
        stage.show();
    }
    
         private void initConnection(){
        
        try {
            
            SB.connection = new SQLConnection("root", "", "sb");
            
            System.out.println("Conexion exitosa");
            
        } catch (ClassNotFoundException | SQLException e) {
            
            System.out.println("Conexion erronea:" + e.getMessage());
            
        }
        
}

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
