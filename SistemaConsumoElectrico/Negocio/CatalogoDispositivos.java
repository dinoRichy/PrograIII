package SistemaConsumoElectrico.Negocio;

import SistemaConsumoElectrico.Excepciones.DatoInvalidoException;
import SistemaConsumoElectrico.Modelo.Dispositivo;

import java.util.ArrayList;

public class CatalogoDispositivos {

    private ArrayList<Dispositivo> catalogo;

    public CatalogoDispositivos() throws DatoInvalidoException {
        catalogo = new ArrayList<>();
        cargarDispositivos();
    }

    private void cargarDispositivos() throws DatoInvalidoException {
        catalogo.add(new Dispositivo(1, "Televisor",          150,  1, 5));
        catalogo.add(new Dispositivo(2, "Refrigerador",       350,  1, 24));
        catalogo.add(new Dispositivo(3, "Microondas",         1200, 1, 0.5));
        catalogo.add(new Dispositivo(4, "Lavadora",           500,  1, 1));
        catalogo.add(new Dispositivo(5, "Computadora",        300,  1, 6));
        catalogo.add(new Dispositivo(6, "Aire Acondicionado", 1500, 1, 8));
        catalogo.add(new Dispositivo(7, "Plancha",            1200, 1, 0.5));
        catalogo.add(new Dispositivo(8, "Licuadora",          400,  1, 0.25));
    }

    public Dispositivo buscarDispositivo(int id) {
        for (Dispositivo d : catalogo) {
            if (d.getId() == id) return d;
        }
        return null;
    }

    public ArrayList<Dispositivo> getCatalogo() {
        return catalogo;
    }
}
