package sb.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet; 
import java.sql.SQLException;
import java.sql.Statement;

public class SQLConnection {
    
    private Connection myConnection;
    private Statement statement;
    
    public SQLConnection(String user, String password, String dataBase) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            myConnection = DriverManager.getConnection("jdbc:mysql://localhost/" + dataBase, user, password);
            statement = myConnection.createStatement();
        } catch (ClassNotFoundException ex) {
            throw new ClassNotFoundException("Conector no encontrado. Mensaje de error: " + ex.getMessage());
        } catch (SQLException ex) {
            throw new SQLException("Error en SQL. Mensaje de error: " + ex.getMessage());
        }
    }
    
    public Connection getConnection() {
        return this.myConnection;
    }
    
    public boolean insert(String instruccion) {
        try{
            if (!myConnection.isClosed()){
                statement.execute(instruccion);
            } else {
                System.out.println("Mensaje de error: La conexión con la Base de Datos está cerrada.");
                return false;
            }
        }catch(SQLException ex){
            System.out.println("Mensaje de error: " + ex.getMessage());
            return false;
        }
        return true;
    }    public boolean delete(String instruction) {
        return insert(instruction);
    }
    
    

    public boolean update(String instruction) {
        try{
            if (!myConnection.isClosed()){
                statement.executeUpdate(instruction);
            }else{ 
                System.out.println("Mensaje de error: La conexión con la Base de Datos está cerrada.");
                return false;
            }
        }catch(SQLException ex){
            System.out.println("Error " + ex.getMessage());
            return false;
        }
        return true;
    }
    
    public ResultSet select(String instruccion) {
        try{
            if (!myConnection.isClosed()){
                ResultSet resultSet = statement.executeQuery(instruccion);
                if(!resultSet.next()){
                    System.out.println("No hay resultados que coincidan con la búsqueda.");
                    return null;
                }

                return resultSet;
            }else{
                System.out.println("La conexión con la Base de Datos está cerrada.");
                return null;
            }
        }catch(SQLException ex){
            System.out.println("Error " + ex.getMessage());
            return null;
        }
    }
    
    public int countRecords(String tabla) {
        ResultSet resultSet = select("SELECT COUNT(*) FROM " + tabla);
        if (resultSet != null) {
            try {
                return resultSet.getInt(1);
            } catch (SQLException ex) {
                System.out.println("Error " + ex.getMessage());
                return -1;
            }
        } else {
            return -1;
        }
    }

    public void close() {
        try {
            statement.close(); // Cerrar el Statement
            myConnection.close(); // Cierre de la conexión
        } catch (SQLException ex) {
            System.out.println("Error " + ex.getMessage());
        } finally {
            myConnection = null;
            statement = null;
        }
    }
}