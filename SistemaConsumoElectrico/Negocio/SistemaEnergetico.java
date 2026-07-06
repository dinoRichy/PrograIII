package SistemaConsumoElectrico.Negocio;

import SistemaConsumoElectrico.Modelo.Usuario;

public class SistemaEnergetico {

    private GestorUsuarios    gestorUsuarios;
    private CalculadoraConsumo calculadora;
    private AnalizadorConsumo  analizador;
    private Recomendador       recomendador;

    public SistemaEnergetico() {
        gestorUsuarios = new GestorUsuarios();
        calculadora    = new CalculadoraConsumo();
        analizador     = new AnalizadorConsumo();
        recomendador   = new Recomendador();
    }

    public GestorUsuarios     getGestorUsuarios() { return gestorUsuarios; }
    public CalculadoraConsumo getCalculadora()    { return calculadora; }
    public AnalizadorConsumo  getAnalizador()     { return analizador; }

    public String obtenerResumenUsuario(Usuario usuario)  { return calculadora.generarResumen(usuario); }
    public String obtenerAnalisisUsuario(Usuario usuario) { return analizador.generarAnalisis(usuario); }
    public String obtenerRecomendaciones(Usuario usuario) { return recomendador.generarRecomendaciones(usuario); }
    public String generarFactura(Usuario usuario)         { return new Factura(usuario).generarFactura(); }
}
