package es.ucm.fdi.pad.hahabit.network;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Cliente para obtener quotes motivacionales usando ZenQuotes API.
 * https://zenquotes.io/
 */
public class ZenQuotesClient {

    private static final String TAG = "PaperQuotesClient";
    private static final String API_URL = "https://zenquotes.io/api/random";


    /**
     * Interfaz de callback para devolver la cita.
     */
    public interface QuoteCallback {
        void onSuccess(String quote, String author);
        void onError(Exception e);
    }

    /**
     * Obtiene una cita motivadora desde PaperQuotes en un hilo de fondo.
     */
    public static void fetchQuoteOfTheDay(QuoteCallback callback) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10_000);
                connection.setReadTimeout(10_000);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    );
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    String responseBody = sb.toString();

                    // ZenQuotes devuelve un array con un objeto: [{"q":"quote","a":"author"}]
                    JSONArray jsonArray = new JSONArray(responseBody);

                    if (jsonArray.length() == 0) {
                        if (callback != null) {
                            callback.onError(new IOException("No quotes available"));
                        }
                        return;
                    }

                    JSONObject quoteObj = jsonArray.getJSONObject(0);
                    String quote = quoteObj.optString("q", "");
                    String author = quoteObj.optString("a", "");

                    if (quote.isEmpty()) {
                        if (callback != null) {
                            callback.onError(new IOException("Empty quote received"));
                        }
                        return;
                    }

                    if (callback != null) {
                        callback.onSuccess(quote, author);
                    }
                } else {
                    if (callback != null) {
                        callback.onError(new IOException("HTTP error: " + responseCode));
                    }
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }
}
