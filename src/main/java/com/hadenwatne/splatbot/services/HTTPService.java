package com.hadenwatne.splatbot.services;

import com.hadenwatne.splatbot.App;
import com.hadenwatne.splatbot.enums.HTTPVerb;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPService {
    /**
     * Creates and performs a synchronous HTTP request, and provides the response.
     * @param v The HTTP Verb to use with this request.
     * @param fqurl The fully-qualified URL for this request.
     * @param body The JSON body of this request, or null if none.
     * @return A string response, if received.
     */
    public static String SendHTTPRequest(HTTPVerb v, String fqurl, JSONObject body) {
        try {
            URL url = new URL(fqurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            switch (v) {
                case GET:
                    conn.setRequestMethod("GET");

                    break;
                case POST:
                    conn.setRequestMethod("POST");

                    if (body != null) {
                        conn.setDoOutput(true);
                        conn.setRequestProperty("content-type", "application/json");

                        if(!App.IsDebug) {
                            conn.setRequestProperty("User-Agent", "Splatbot by hwdotexe, Discord chatbot");
                        }

                        conn.getOutputStream().write(body.toString().getBytes());
                    }

                    break;
            }

            // Retrieve data
            if (conn.getResponseCode() == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                StringBuilder result = new StringBuilder();

                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                rd.close();
                conn.disconnect();

                return result.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            LoggingService.LogException(e);
            return null;
        }
    }
}
