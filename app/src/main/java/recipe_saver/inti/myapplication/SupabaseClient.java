package recipe_saver.inti.myapplication;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class SupabaseClient {
    private static final String SUPABASE_URL = "https://eectqypapojndpoosfza.supabase.co/rest/v1/your_table_name";
    private static final String SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVlY3RxeXBhcG9qbmRwb29zZnphIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mjg2MzE0MzUsImV4cCI6MjA0NDIwNzQzNX0.6XY2mYEtVrmD9rKyoxjsijLHCNvyv4fQ2qEAAhhrdYg";

    public static void fetchTableData() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(SUPABASE_URL);
            request.setHeader("Authorization", "Bearer " + SUPABASE_ANON_KEY);
            request.setHeader("Content-Type", "application/json");

            HttpResponse response = httpClient.execute(request);
            String jsonResponse = EntityUtils.toString(response.getEntity());
            System.out.println("Response: " + jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
