package SistemaConsumoElectrico.Negocio;

import SistemaConsumoElectrico.Modelo.Usuario;

public class Factura {

    private final Usuario            usuario;
    private final CalculadoraConsumo calculadora;
    private static final double IVA = 0.15;

    public Factura(Usuario usuario) {
        this.usuario     = usuario;
        this.calculadora = new CalculadoraConsumo();
    }

    private double calcularSubtotal() { return calculadora.calcularCostoMensual(usuario); }
    private double calcularIVA()      { return calcularSubtotal() * IVA; }
    private double calcularTotal()    { return calcularSubtotal() + calcularIVA(); }

    public String generarFactura() {
        StringBuilder texto = new StringBuilder();
        texto.append("\n==================================\n");
        texto.append("        FACTURA DE CONSUMO\n");
        texto.append("==================================\n");
        texto.append("Cliente:               ").append(usuario.getNombre()).append("\n");
        texto.append("Sector:                ").append(usuario.getSector()).append("\n");
        texto.append("Inmuebles registrados: ").append(usuario.getInmuebles().size()).append("\n");
        texto.append(String.format("Consumo mensual:       %.2f kWh%n",    calculadora.calcularConsumoMensual(usuario)));
        texto.append(String.format("Tarifa aplicada:       $%.2f por kWh%n", usuario.obtenerTarifa()));
        texto.append(String.format("Subtotal:              $%.2f%n",        calcularSubtotal()));
        texto.append(String.format("IVA (15%%):             $%.2f%n",       calcularIVA()));
        texto.append(String.format("TOTAL A PAGAR:         $%.2f%n",        calcularTotal()));
        texto.append("==================================");
        return texto.toString();
    }
}
