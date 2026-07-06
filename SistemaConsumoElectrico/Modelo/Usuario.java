package SistemaConsumoElectrico.Modelo;

import SistemaConsumoElectrico.Excepciones.DatoInvalidoException;
import java.util.ArrayList;

public abstract class Usuario extends Persona {

    protected String cedula;
    protected String contraseña;
    protected Sector sector;
    protected Rol rol;
    protected ArrayList<Inmueble> inmuebles;
    // Historial de inmuebles eliminados (mantener para reportes)
    protected ArrayList<Inmueble> historialInmuebles;
    protected boolean activo;

    public Usuario(String cedula, String nombre, String contraseña, Sector sector)
            throws DatoInvalidoException {
        this(cedula, nombre, contraseña, sector, Rol.USUARIO);
    }

    public Usuario(String cedula, String nombre, String contraseña, Sector sector, Rol rol)
            throws DatoInvalidoException {
        super(nombre);
        validarCedula(cedula);

        if (contraseña == null || contraseña.trim().isEmpty()) {
            throw new DatoInvalidoException("La contraseña no puede estar vacía");
        }
        if (sector == null) {
            throw new DatoInvalidoException("El sector no puede ser nulo");
        }
        if (rol == null) {
            throw new DatoInvalidoException("El rol no puede ser nulo");
        }

        this.cedula    = cedula;
        this.contraseña = contraseña;
        this.sector    = sector;
        this.rol       = rol;
        this.inmuebles = new ArrayList<>();
        this.historialInmuebles = new ArrayList<>();
        this.activo    = true;
    }

    private void validarCedula(String cedula) throws DatoInvalidoException {
        if (cedula == null || !cedula.matches("\\d{10}")) {
            throw new DatoInvalidoException("La cédula debe contener exactamente 10 números");
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            throw new DatoInvalidoException("La cédula ecuatoriana tiene una provincia inválida");
        }

        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito = Integer.parseInt(String.valueOf(cedula.charAt(i)));
            if (i % 2 == 0) {
                digito *= 2;
                if (digito > 9) digito -= 9;
            }
            suma += digito;
        }

        int decenaSuperior    = ((suma / 10) + 1) * 10;
        int digitoVerificador = decenaSuperior - suma;
        if (digitoVerificador == 10) digitoVerificador = 0;

        int ultimoDigito = Integer.parseInt(String.valueOf(cedula.charAt(9)));
        if (digitoVerificador != ultimoDigito) {
            throw new DatoInvalidoException("La cédula ecuatoriana no es válida");
        }
    }

    public String getCedula()     { return cedula; }
    public String getContraseña() { return contraseña; }
    public Sector getSector()     { return sector; }
    public Rol    getRol()        { return rol; }

    // ── Gestión de inmuebles ───────────────────────────────────────────────

    public ArrayList<Inmueble> getInmuebles() { return inmuebles; }

    public void agregarInmueble(Inmueble inmueble) throws DatoInvalidoException {
        if (inmueble == null) {
            throw new DatoInvalidoException("No se puede agregar un inmueble vacío.");
        }
        inmuebles.add(inmueble);
    }

    /**
     * Elimina un inmueble de la lista activa y lo mueve al historial.
     * Devuelve true si se eliminó correctamente, false si no se encontró.
     */
    public boolean eliminarInmueble(int idInmueble) {
        for (Inmueble i : new ArrayList<>(inmuebles)) {
            if (i.getId() == idInmueble) {
                inmuebles.remove(i);
                historialInmuebles.add(i);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Inmueble> getHistorialInmuebles() { return historialInmuebles; }

    // ── Estado del usuario ─────────────────────────────────────────────────

    public boolean isActivo()              { return activo; }
    public void    setActivo(boolean activo) { this.activo = activo; }

    public abstract double obtenerTarifa();
}
