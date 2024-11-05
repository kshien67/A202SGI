package recipe_saver.inti.myapplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import org.json.JSONObject;

public class SupabaseConnector {
    private static final String SUPABASE_URL = "https://eectqypapojndpoosfza.supabase.co";
    private static final String SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVlY3RxeXBhcG9qbmRwb29zZnphIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjg2MzE0MzUsImV4cCI6MjA0NDIwNzQzNX0.6XY2mYEtVrmD9rKyoxjsijLHCNvyv4fQ2qEAAhhrdYg";
    private static final Logger mLogger = Logger.getLogger(SupabaseConnector.class.getName());
    private static String accessToken = null;

    private static HttpURLConnection httpRequest(String type, String urlString, String jsonInputString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(type);
            conn.setRequestProperty("apikey", SUPABASE_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            mLogger.info("Request URL: " + urlString);
            mLogger.info("Request Body: " + jsonInputString);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            return conn;

        } catch (Exception e) {
            mLogger.severe(e.getMessage());
            return null;
        }
    }

    private static void printFullResponse(HttpURLConnection conn) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            mLogger.info("Response: " + response.toString());
        } catch (Exception e) {
            mLogger.severe(e.getMessage());
        }
    }

    private static String getFullResponse(HttpURLConnection conn) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        } catch (Exception e) {
            mLogger.severe(e.getMessage());
            return null;
        }
    }

    // Supabase Shenanigans
    // • Email MUST be in our Supabase organisation, otherwise it will return a 400 error
    // • Password MUST be at least 6 characters long, otherwise it will return a 400 error
    // • A confirmation email will be sent to the email address
    public static boolean signUp(String email, String password) {
        String url = SUPABASE_URL + "/auth/v1/signup";
        String jsonInputString = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
        HttpURLConnection conn = httpRequest("POST", url, jsonInputString);

        if (conn != null) {
            try {
                int responseCode = conn.getResponseCode();
                mLogger.info("Response code: " + responseCode);
                printFullResponse(conn);
                return responseCode == 200;
            } catch (Exception e) {
                mLogger.severe(e.getMessage());
            }
        }
        return false;
    }

    public static boolean login(String email, String password) {
        String url = SUPABASE_URL + "/auth/v1/token?grant_type=password";
        String jsonInputString = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);
        HttpURLConnection conn = httpRequest("POST", url, jsonInputString);

        if (conn != null) {
            try {
                int responseCode = conn.getResponseCode();
                mLogger.info("Response code: " + responseCode);
                String response = getFullResponse(conn);
                if (responseCode ==200) {
                    JSONObject jsonresponse = new JSONObject(response);
                    accessToken = jsonresponse.getString("access_token");
                    mLogger.info("Access Token: " + accessToken);
                    return true;
                }
            } catch (Exception e) {
                mLogger.severe(e.getMessage());
            }
        }
        return false;
    }

    public static boolean logout(String userToken) {
        String url = SUPABASE_URL + "/auth/v1/logout";
        HttpURLConnection conn = null;
        try {
            URL urlObj = new URL(url);
            conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("apikey", SUPABASE_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + userToken);
            conn.setDoOutput(true);

            int responseCode = conn.getResponseCode();
            mLogger.info("Response code: " + responseCode);
            printFullResponse(conn);
            if (responseCode == 200) {
                accessToken = null;
                return true;
            }
        } catch (Exception e) {
            mLogger.severe(e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return false;
    }
}