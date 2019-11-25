package sb.administrador;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import sb.SQL.SQLConnection;
import sb.clases.Cliente;

/**
 * FXML Controller class
 *
 * @author L745
 */
public class AdministradorController implements Initializable {

    @FXML
    private TableView<Cliente> table_clientes;
    @FXML
    private TableColumn<Cliente,String>col_nombre,col_ap,col_am,col_tel,col_comentario;
    @FXML
    private TableColumn<Cliente,Date>col_inicio,col_vencimiento;
    @FXML
    private TableColumn<Cliente,Integer>col_id,col_edad;
    @FXML
    private TableColumn<Cliente,File>col_foto_usuario;    
    
//    ******************Datos********************************************
    public TextField tf_nombre,tf_apellido_materno,tf_apellido_paterno,tf_edad,tf_telefono;
    public DatePicker inicio,fin;
    public TextArea ta_comentario;
    Connection connection;
    public Image image;
    public ImageView usuario;
    
//    ******************Datos********************************************
    
    ObservableList<Cliente> lista = FXCollections.observableArrayList();   
    
    public void seleccionar() throws SQLException{
//        Me falta establecer en textfield las coas de la tabla
        Cliente listita = table_clientes.getSelectionModel().getSelectedItem();
        System.out.println(listita.getApellido_materno());
        tf_nombre.setText(listita.getNombre());
        tf_apellido_paterno.setText(listita.getApellido_paterno());
        tf_apellido_materno.setText(listita.getApellido_materno());
        tf_edad.setText(String.valueOf(listita.getEdad()));
        tf_telefono.setText(listita.getTelefono());
        inicio.setValue(listita.getInicio().toLocalDate());
        fin.setValue(listita.getFin().toLocalDate());
        ta_comentario.setText(listita.getComentarios());
        //////////////////////////////////////////////////////
        Blob blob = listita.getUsuario();
        InputStream ist = blob.getBinaryStream();
        image = new Image(ist);
        usuario.setImage(image);
        
    }
    
    public void editar() throws SQLException{
        Cliente listita = table_clientes.getSelectionModel().getSelectedItem();
        int id = listita.getId();
        String nombre = tf_nombre.getText();
        String paterno = tf_apellido_paterno.getText();
        String materno = tf_apellido_materno.getText();
        int edad = Integer.valueOf(tf_edad.getText());
        String telefono = tf_telefono.getText();
        LocalDate i = inicio.getValue();
        LocalDate f = fin.getValue();
        String observacion = ta_comentario.getText();
        if (connection != null) {
            String query = "UPDATE cliente SET nombre = ? ,apellido_paterno = ?,apellido_materno= ? ,edad = ? ,telefono= ? ,inicio=? ,vencimiento=? ,observaciones=? WHERE id = '"+id+"' ";
            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.setString(1, nombre);
            preparedStmt.setString(2, paterno);
            preparedStmt.setString(3, materno);
            preparedStmt.setInt(4, edad);
            preparedStmt.setString(5, telefono);
            preparedStmt.setDate(6,Date.valueOf(i));
            preparedStmt.setDate(7,Date.valueOf(f));
            preparedStmt.setString(8,observacion);
            preparedStmt.executeUpdate();      
            connection.close();
            listita.setNombre(nombre);
            listita.setApellido_paterno(paterno);
            listita.setApellido_materno(materno);
            listita.setEdad(edad);
            listita.setTelefono(telefono);
            listita.setInicio(java.sql.Date.valueOf(i));
            listita.setFin(java.sql.Date.valueOf(f));
            listita.setComentarios(observacion);            
            this.table_clientes.refresh();
            new Alert(Alert.AlertType.INFORMATION, "actualizado").show();
            
        }                   
       
    }
    
    public void eliminar() throws SQLException{
        Cliente listita = table_clientes.getSelectionModel().getSelectedItem();
        int id = listita.getId();
        String nombre = tf_nombre.getText();
        String paterno = tf_apellido_paterno.getText();
        String materno = tf_apellido_materno.getText();
        int edad = Integer.valueOf(tf_edad.getText());
        String telefono = tf_telefono.getText();
        LocalDate i = inicio.getValue();
        LocalDate f = fin.getValue();
        String observacion = ta_comentario.getText();
        String query = " DELETE FROM cliente WHERE id ='"+listita.getId()+ "';";        
        PreparedStatement preparedStmt = connection.prepareStatement(query);
        preparedStmt.execute();     
        connection.close();
        lista.remove(listita);
        table_clientes.refresh();
        new Alert(Alert.AlertType.INFORMATION, "eliminado").show();
    }
    
        
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
                this.connection = new SQLConnection("root", "", "sb").getConnection();

                System.out.println("Conexion Lista");

            } catch (ClassNotFoundException | SQLException e) {

                System.out.println("Conexión no realizada"+e.getMessage());
            }
        
        try {
            ResultSet rs = connection.createStatement().executeQuery(
                    "select id,nombre,apellido_paterno,apellido_materno,edad,telefono,inicio,vencimiento,observaciones,huella from cliente"
            );            
            while(rs.next()){
//                OutputStream  out = new FileOutputStream
                lista.add(new Cliente(rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido_paterno"),
                        rs.getString("apellido_materno"),
                        rs.getInt("edad"), 
                        rs.getString("telefono"), 
                        rs.getDate("inicio"),
                        rs.getDate("vencimiento"), 
                        rs.getString("observaciones"), 
                        rs.getBlob("huella")));                        
            }                       
            col_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            col_nombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            col_ap.setCellValueFactory(new PropertyValueFactory<>("apellido_paterno"));
            col_am.setCellValueFactory(new PropertyValueFactory<>("apellido_materno"));
            col_edad.setCellValueFactory(new PropertyValueFactory<>("edad"));
            col_tel.setCellValueFactory(new PropertyValueFactory<>("telefono"));
            col_inicio.setCellValueFactory(new PropertyValueFactory<>("inicio"));
            col_vencimiento.setCellValueFactory(new PropertyValueFactory<>("fin"));
            col_comentario.setCellValueFactory(new PropertyValueFactory<>("comentarios"));
            col_foto_usuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
            table_clientes.setItems(lista);
        } catch (SQLException ex) {
            Logger.getLogger(AdministradorController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
