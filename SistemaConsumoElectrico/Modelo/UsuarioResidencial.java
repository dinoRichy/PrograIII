package SistemaConsumoElectrico.Modelo;

import SistemaConsumoElectrico.Excepciones.DatoInvalidoException;

public class UsuarioResidencial extends Usuario {

    private static final double TARIFA_RESIDENCIAL = 0.10;

    public UsuarioResidencial(String cedula, String nombre, String contraseña, Sector sector)
            throws DatoInvalidoException {
        super(cedula, validarNombreResidencial(nombre), contraseña, sector);
    }

    private static String validarNombreResidencial(String nombre) throws DatoInvalidoException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatoInvalidoException("El nombre no puede estar vacío.");
        }
        if (!nombre.matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]+")) {
            throw new DatoInvalidoException(
                    "El nombre del usuario residencial solo puede contener letras y espacios.");
        }
        return nombre.trim();
    }

    @Override
    public double obtenerTarifa() {
        return TARIFA_RESIDENCIAL;
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("Usuario Residencial | Cédula: " + cedula
                + " | Nombre: " + nombre + " | Sector: " + sector
                + " | Tarifa: $" + TARIFA_RESIDENCIAL + "/kWh");
    }
}
