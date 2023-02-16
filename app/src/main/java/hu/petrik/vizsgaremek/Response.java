package hu.petrik.vizsgaremek;

public class Response {

    private int responseCode;
    private String content;

    public Response(int responseCode, String content) {
        this.responseCode = responseCode;
        this.content = content;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
