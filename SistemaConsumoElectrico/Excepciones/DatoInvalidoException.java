package SistemaConsumoElectrico.Excepciones;

public class DatoInvalidoException extends Exception {

    public DatoInvalidoException(String mensaje){

        super(mensaje);
    }
}