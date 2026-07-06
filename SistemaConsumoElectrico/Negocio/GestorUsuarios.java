package SistemaConsumoElectrico.Negocio;

import SistemaConsumoElectrico.Excepciones.DatoInvalidoException;
import SistemaConsumoElectrico.Modelo.*;

import java.util.ArrayList;

public class GestorUsuarios {

    private ArrayList<Usuario> usuarios;

    public GestorUsuarios() {
        usuarios = new ArrayList<>();
        try {
            Usuario administrador = new UsuarioEmpresa(
                    "1710034065", "Administracion", "admin123",
                    Sector.CENTRO, "1799999999001", "Administración del sistema",
                    Rol.ADMINISTRADOR);
            usuarios.add(administrador);
        } catch (DatoInvalidoException e) {
            e.printStackTrace();
        }
    }

    public void agregarUsuario(Usuario usuario) throws DatoInvalidoException {
        if (usuario == null) {
            throw new DatoInvalidoException("No se puede registrar un usuario vacío");
        }
        Usuario existente = buscarUsuario(usuario.getCedula());
        if (existente != null) {
            if (!existente.isActivo()) {
                existente.setActivo(true);
                return;
            }
            throw new DatoInvalidoException("Ya existe un usuario registrado con esa cédula");
        }
        usuarios.add(usuario);
    }

    public Usuario buscarUsuario(String cedula) {
        for (Usuario u : usuarios) {
            if (u.getCedula().equals(cedula)) return u;
        }
        return null;
    }

    public Usuario validarLogin(String cedula, String contraseña) throws DatoInvalidoException {
        Usuario usuario = buscarUsuario(cedula);
        if (usuario == null) {
            throw new DatoInvalidoException("La cédula no se encuentra registrada");
        }
        if (!usuario.getContraseña().equals(contraseña)) {
            throw new DatoInvalidoException("Contraseña incorrecta");
        }
        return usuario;
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }
}
