package SistemaConsumoElectrico.Modelo;

public enum Sector {

    NORTE(250),
    SUR(220),
    CENTRO(240),
    QUITUMBE(210),
    VALLE_DE_LOS_CHILLOS(280),
    CUMBAYA(350),
    TUMBACO(320);

    private final double limiteConsumo;

    Sector(double limiteConsumo){

        this.limiteConsumo = limiteConsumo;

    }

    public double getLimiteConsumo(){

        return limiteConsumo;

    }

    public String getDescripcion(){

        return "Sector: "
                + this.name()
                + " | Límite recomendado: "
                + limiteConsumo
                + " kWh";

    }
}
