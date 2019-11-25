
package sb.clases;

import java.io.File;
import java.sql.Blob;
import java.sql.Date;

public class Cliente {
    String nombre,apellido_paterno,apellido_materno,telefono,comentarios;
    int id,edad;
    Date inicio,fin;
    Blob usuario;

    public Cliente( int id,String nombre, String apellido_paterno, String apellido_materno, int edad , String telefono, Date inicio, Date fin, String comentarios, Blob usuario) {
        this.id = id;
        this.nombre = nombre;
        this.apellido_paterno = apellido_paterno;
        this.apellido_materno = apellido_materno;
        this.edad = edad;
        this.telefono = telefono;        
        this.inicio = inicio;
        this.fin = fin;
        this.comentarios = comentarios;
        this.usuario = usuario;
    }


    
    
    public Cliente(int id,String nombre, String apellido_paterno, String apellido_materno, String telefono, int edad, Date inicio, Date fin) {
        this.id = id;
        this.nombre = nombre;
        this.apellido_paterno = apellido_paterno;
        this.apellido_materno = apellido_materno;
        this.telefono = telefono;
        this.edad = edad;
        this.inicio = inicio;
        this.fin = fin;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public Blob getUsuario() {
        return usuario;
    }

    public void setUsuario(Blob usuario) {
        this.usuario = usuario;
    }
    
    

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido_paterno() {
        return apellido_paterno;
    }

    public void setApellido_paterno(String apellido_paterno) {
        this.apellido_paterno = apellido_paterno;
    }

    public String getApellido_materno() {
        return apellido_materno;
    }

    public void setApellido_materno(String apellido_materno) {
        this.apellido_materno = apellido_materno;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

    
    
    
    
    
    
    
    
    
    
    
}
