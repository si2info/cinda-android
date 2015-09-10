package info.si2.iista.bolunteernetworks.apiclient;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class Result {

    private boolean error;
    private String mensaje;
    private int codigoError;
    private int resultFrom;

    public Result(boolean error, String mensaje, int resultFrom, int codigoError) {
        super();
        this.error = error;
        this.mensaje = mensaje;
        this.resultFrom = resultFrom;
        this.codigoError = codigoError;
    }

    public boolean isError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public int getResultFrom() {
        return resultFrom;
    }

    public int getCodigoError(){
        return codigoError;
    }

}
