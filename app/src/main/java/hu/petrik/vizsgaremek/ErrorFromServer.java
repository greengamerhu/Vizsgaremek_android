package hu.petrik.vizsgaremek;

public class ErrorFromServer {
    private int statusCode;
    private String[] message;
    private String error;

    public ErrorFromServer(int statusCode, String[] message, String error) {
        this.statusCode = statusCode;
        this.message = message;
        this.error = error;
    }

    public ErrorFromServer(int statusCode, String[] message) {
        this.statusCode = statusCode;
        this.message = message;
        this.error = "";
    }

    public ErrorFromServer() {

    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String[] getMessage() {
        return message;
    }

    public void setMessage(String[] message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
