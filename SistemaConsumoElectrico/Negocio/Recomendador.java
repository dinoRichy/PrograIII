package SistemaConsumoElectrico.Negocio;

import SistemaConsumoElectrico.Modelo.Dispositivo;
import SistemaConsumoElectrico.Modelo.Usuario;

public class Recomendador {

    private AnalizadorConsumo  analizador;
    private CalculadoraConsumo calculadora;

    public Recomendador() {
        analizador  = new AnalizadorConsumo();
        calculadora = new CalculadoraConsumo();
    }

    public String generarRecomendaciones(Usuario usuario) {
        StringBuilder texto = new StringBuilder();
        texto.append("\n===== RECOMENDACIONES ENERGÉTICAS =====\n");
        texto.append("Inmuebles registrados: ").append(usuario.getInmuebles().size()).append("\n\n");

        switch (analizador.clasificarConsumo(usuario)) {
            case "BAJO":
                texto.append("Excelente. El consumo energético es eficiente.\n");
                break;
            case "NORMAL":
                texto.append("El consumo es adecuado. Mantenga sus hábitos actuales.\n");
                break;
            case "ALTO":
                texto.append("El consumo está por encima de lo recomendado.\n");
                texto.append("Revise los dispositivos con mayor uso.\n");
                break;
            case "CRITICO":
                texto.append("El consumo es excesivamente alto.\n");
                texto.append("Se recomienda aplicar medidas inmediatas de ahorro.\n");
                break;
        }

        Dispositivo mayor = analizador.obtenerMayorConsumidor(usuario);
        if (mayor != null) {
            texto.append("\nDispositivo de mayor consumo:\n");
            texto.append(mayor.getNombre()).append("\n");
            texto.append(String.format("Consumo mensual: %.2f kWh%n", mayor.calcularConsumoMensual()));
            texto.append("Sugerencia: reducir las horas de uso de ")
                 .append(mayor.getNombre()).append(" puede disminuir el consumo.\n");
        }

        double consumo = calculadora.calcularConsumoMensual(usuario);
        double limite  = usuario.getSector().getLimiteConsumo();
        if (consumo > limite) {
            texto.append("\nEl consumo supera el límite del sector ").append(usuario.getSector()).append(".\n");
            texto.append("Considere dispositivos de mayor eficiencia energética.\n");
        }

        texto.append("\nRecomendaciones generales:\n");
        texto.append("- Apagar equipos que no estén en uso.\n");
        texto.append("- Realizar mantenimiento periódico.\n");
        texto.append("- Utilizar iluminación eficiente.\n");
        texto.append("- Distribuir correctamente los dispositivos entre los distintos inmuebles.\n");
        texto.append("- Revisar periódicamente el consumo de cada inmueble para detectar excesos.\n");

        return texto.toString();
    }
}
