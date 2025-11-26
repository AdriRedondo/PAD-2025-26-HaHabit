package es.ucm.fdi.pad.hahabit.network;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import es.ucm.fdi.pad.hahabit.BuildConfig;
/**
 * Cliente muy simple para PaperQuotes.
 * Llama al endpoint "Quote of the day" en español y devuelve la cita por callback.
 */
public class PaperQuotesClient {

    private static final String TAG = "PaperQuotesClient";
    // Endpoint de Quote of the Day en español
    private static final String API_URL =
            "https://api.paperquotes.com/apiv1/quotes/?lang=es&tags=motivational&random=random";


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

                // Cabecera con el token
                String token = BuildConfig.PAPERQUOTES_API_TOKEN;
                connection.setRequestProperty(
                        "Authorization",
                        "Token " + token
                );
                connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

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

                    // Parseamos el JSON
                    JSONObject json = new JSONObject(sb.toString());
                    String quote = json.optString("quote");
                    String author = json.optString("author", "");

                    if (callback != null) {
                        callback.onSuccess(quote, author);
                    }
                } else {
                    if (callback != null) {
                        callback.onError(
                                new IOException("HTTP error code: " + responseCode)
                        );
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al obtener cita de PaperQuotes", e);
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
