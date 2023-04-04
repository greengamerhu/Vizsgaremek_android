package hu.petrik.vizsgaremek;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

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
class ErrorFromServerDeserializer implements JsonDeserializer<ErrorFromServer> {
    @Override
    public ErrorFromServer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        int statusCode = jsonObject.get("statusCode").getAsInt();
        JsonElement messageElement = jsonObject.get("message");

        String[] message;
        if (messageElement.isJsonArray()) {
            message = context.deserialize(messageElement, String[].class);
        } else {
            message = new String[]{messageElement.getAsString()};
        }
        String error = null;
        JsonElement errorElement = jsonObject.get("error");
        if (errorElement != null && !errorElement.isJsonNull()) {
            error = errorElement.getAsString();
        } else {
            error = "";
        }

        return new ErrorFromServer(statusCode, message, error);
    }

}
