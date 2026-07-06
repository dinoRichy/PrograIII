package SistemaConsumoElectrico.Modelo;

import SistemaConsumoElectrico.Excepciones.DatoInvalidoException;

public class UsuarioEmpresa extends Usuario {

    private String ruc;
    private String actividadEconomica;
    private static final double TARIFA_EMPRESA = 0.15;

    public UsuarioEmpresa(String cedula, String nombre, String contraseña, Sector sector,
                          String ruc, String actividadEconomica) throws DatoInvalidoException {
        super(cedula, nombre, contraseña, sector);
        validarEmpresa(ruc, actividadEconomica);
        this.ruc               = ruc;
        this.actividadEconomica = actividadEconomica;
    }

    public UsuarioEmpresa(String cedula, String nombre, String contraseña, Sector sector,
                          String ruc, String actividadEconomica, Rol rol) throws DatoInvalidoException {
        super(cedula, nombre, contraseña, sector, rol);
        validarEmpresa(ruc, actividadEconomica);
        this.ruc               = ruc;
        this.actividadEconomica = actividadEconomica;
    }

    private void validarEmpresa(String ruc, String actividad) throws DatoInvalidoException {
        if (ruc == null || !ruc.matches("\\d{13}")) {
            throw new DatoInvalidoException("El RUC debe contener exactamente 13 números");
        }
        if (actividad == null || actividad.trim().isEmpty()) {
            throw new DatoInvalidoException("La actividad económica no puede estar vacía");
        }
    }

    public String getRuc()                { return ruc; }
    public String getActividadEconomica() { return actividadEconomica; }

    @Override
    public double obtenerTarifa() {
        return TARIFA_EMPRESA;
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("Empresa | Cédula: " + cedula + " | Nombre: " + nombre
                + " | RUC: " + ruc + " | Actividad: " + actividadEconomica
                + " | Sector: " + sector + " | Tarifa: $" + TARIFA_EMPRESA + "/kWh");
    }
}
