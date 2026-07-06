package SistemaConsumoElectrico.Modelo;

import SistemaConsumoElectrico.Excepciones.DatoInvalidoException;
import java.util.ArrayList;

public class Inmueble {

    private int                id;
    private String             nombre;
    private TipoInmueble       tipo;
    private ArrayList<Dispositivo> dispositivos;

    public Inmueble(int id, String nombre, TipoInmueble tipo) throws DatoInvalidoException {
        if (id <= 0) {
            throw new DatoInvalidoException("El ID del inmueble es inválido.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoInvalidoException("El nombre del inmueble no puede estar vacío.");
        }
        if (tipo == null) {
            throw new DatoInvalidoException("Debe seleccionar un tipo de inmueble.");
        }
        this.id          = id;
        this.nombre      = nombre;
        this.tipo        = tipo;
        this.dispositivos = new ArrayList<>();
    }

    public int          getId()           { return id; }
    public String       getNombre()       { return nombre; }
    public TipoInmueble getTipo()         { return tipo; }
    public ArrayList<Dispositivo> getDispositivos() { return dispositivos; }

    public void agregarDispositivo(Dispositivo dispositivo) throws DatoInvalidoException {
        if (dispositivo == null) throw new DatoInvalidoException("Dispositivo inválido.");
        dispositivos.add(dispositivo);
    }

    public boolean eliminarDispositivo(int idDispositivo) {
        return dispositivos.removeIf(d -> d.getId() == idDispositivo);
    }

    public Dispositivo buscarDispositivo(int idDispositivo) {
        for (Dispositivo d : dispositivos) {
            if (d.getId() == idDispositivo) return d;
        }
        return null;
    }

    @Override
    public String toString() {
        return nombre + " (" + tipo + ")";
    }
}
