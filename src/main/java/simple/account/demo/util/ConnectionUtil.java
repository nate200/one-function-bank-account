package simple.account.demo.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionUtil {
    public static JsonObject getJsonObjFromUrl(String url_str) throws IOException {
        try (
            InputStream is = getContentStream(url_str);
            InputStreamReader inr = new InputStreamReader(is)
        ) {
            JsonElement jsonElement = JsonParser.parseReader(inr);
            return jsonElement.getAsJsonObject();
        }
    }
    private static InputStream getContentStream(String url_str) throws IOException {
        URL url = new URL(url_str);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        return request.getInputStream();
    }
}
