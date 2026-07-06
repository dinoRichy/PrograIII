package SistemaConsumoElectrico.Negocio;

import SistemaConsumoElectrico.Modelo.Dispositivo;
import SistemaConsumoElectrico.Modelo.Inmueble;
import SistemaConsumoElectrico.Modelo.Usuario;

public class AnalizadorConsumo {

    private CalculadoraConsumo calculadora;

    public AnalizadorConsumo() {
        calculadora = new CalculadoraConsumo();
    }

    public Dispositivo obtenerMayorConsumidor(Usuario usuario) {
        Dispositivo mayor = null;
        double mayorConsumo = 0;
        for (Inmueble inmueble : usuario.getInmuebles()) {
            for (Dispositivo d : inmueble.getDispositivos()) {
                double consumo = d.calcularConsumoMensual();
                if (consumo > mayorConsumo) {
                    mayorConsumo = consumo;
                    mayor = d;
                }
            }
        }
        return mayor;
    }

    public String clasificarConsumo(Usuario usuario) {
        double consumo = calculadora.calcularConsumoMensual(usuario);
        double limite  = usuario.getSector().getLimiteConsumo();
        if (consumo < limite * 0.50) return "BAJO";
        if (consumo < limite)        return "NORMAL";
        if (consumo < limite * 1.30) return "ALTO";
        return "CRITICO";
    }

    public boolean consumoExcesivo(Usuario usuario) {
        return calculadora.calcularConsumoMensual(usuario) > usuario.getSector().getLimiteConsumo();
    }

    public String generarAnalisis(Usuario usuario) {
        StringBuilder texto = new StringBuilder();
        double consumo = calculadora.calcularConsumoMensual(usuario);
        double limite  = usuario.getSector().getLimiteConsumo();

        texto.append("\n===== ANÁLISIS ENERGÉTICO =====\n");
        texto.append(String.format("Consumo mensual: %.2f kWh%n", consumo));
        texto.append(String.format("Límite recomendado (%s): %.2f kWh%n", usuario.getSector(), limite));
        texto.append("Inmuebles registrados: ").append(usuario.getInmuebles().size()).append("\n");
        texto.append("Clasificación: ").append(clasificarConsumo(usuario)).append("\n");

        if (consumoExcesivo(usuario)) {
            texto.append("ALERTA: El consumo supera el límite recomendado.\n");
        } else {
            texto.append("El consumo está dentro de parámetros aceptables.\n");
        }

        Dispositivo mayor = obtenerMayorConsumidor(usuario);
        if (mayor != null) {
            texto.append("\nDispositivo de mayor consumo:\n");
            texto.append(mayor.getNombre()).append("\n");
            texto.append(String.format("Consumo mensual: %.2f kWh%n", mayor.calcularConsumoMensual()));
        }

        return texto.toString();
    }
}
