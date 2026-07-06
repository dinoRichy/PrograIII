package SistemaConsumoElectrico.Modelo;

import SistemaConsumoElectrico.Excepciones.DatoInvalidoException;

public class Dispositivo implements CalculableConsumo {

    private int    id;
    private String nombre;
    private double potencia;
    private int    cantidad;
    private double horasUsoDiarias;

    public Dispositivo(int id, String nombre, double potencia, int cantidad, double horasUsoDiarias)
            throws DatoInvalidoException {
        validarDatos(nombre, potencia, cantidad, horasUsoDiarias);
        this.id             = id;
        this.nombre         = nombre;
        this.potencia       = potencia;
        this.cantidad       = cantidad;
        this.horasUsoDiarias = horasUsoDiarias;
    }

    private void validarDatos(String nombre, double potencia, int cantidad, double horas)
            throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre del dispositivo no puede estar vacío");
        }
        if (potencia <= 0) {
            throw new DatoInvalidoException("La potencia debe ser mayor a cero");
        }
        if (cantidad <= 0) {
            throw new DatoInvalidoException("La cantidad debe ser mayor a cero");
        }
        if (horas <= 0 || horas > 24) {
            throw new DatoInvalidoException("Las horas de uso deben estar entre 1 y 24");
        }
    }

    public int    getId()              { return id; }
    public String getNombre()          { return nombre; }
    public double getPotencia()        { return potencia; }
    public int    getCantidad()        { return cantidad; }
    public double getHorasUsoDiarias() { return horasUsoDiarias; }

    public void setCantidad(int cantidad) throws DatoInvalidoException {
        if (cantidad <= 0) throw new DatoInvalidoException("Cantidad inválida");
        this.cantidad = cantidad;
    }

    public void setHorasUsoDiarias(double horas) throws DatoInvalidoException {
        if (horas <= 0 || horas > 24) throw new DatoInvalidoException("Horas de uso inválidas");
        this.horasUsoDiarias = horas;
    }

    @Override
    public double calcularConsumoDiario() {
        return (potencia * cantidad * horasUsoDiarias) / 1000;
    }

    @Override
    public double calcularConsumoMensual() {
        return calcularConsumoDiario() * 30;
    }

    @Override
    public double calcularConsumoAnual() {
        return calcularConsumoMensual() * 12;
    }

    @Override
    public String toString() {
        return "ID: " + id
                + " | Nombre: " + nombre
                + " | Potencia: " + potencia + " W"
                + " | Cantidad: " + cantidad
                + " | Horas/día: " + horasUsoDiarias
                + " | Consumo mensual: " + String.format("%.2f", calcularConsumoMensual()) + " kWh";
    }
}
