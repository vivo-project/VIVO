package edu.cornell.mannlib.vivo.orcid.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import edu.cornell.mannlib.vitro.webapp.web.URLEncoder;

public class OrcidTokenExchange {

    private final String tokenUrl;

    private final String clientId;
    private final String clientSecret;

    public OrcidTokenExchange(String clientId, String clientSecret, boolean inSandbox) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tokenUrl = inSandbox ? "https://sandbox.orcid.org/oauth/token" : "https://orcid.org/oauth/token";
    }

    /**
     * Exchanges authorization code for access token
     */
    public OrcidTokenResponse exchangeCodeForToken(String authorizationCode) throws IOException {
        String formData = String.format(
            "client_id=%s&client_secret=%s&grant_type=authorization_code&code=%s",
            URLEncoder.encode(clientId),
            URLEncoder.encode(clientSecret),
            URLEncoder.encode(authorizationCode)
        );

        return makeTokenRequest(formData);
    }

    /**
     * Refreshes an access token using refresh token
     */
    public OrcidTokenResponse refreshToken(String refreshToken) throws IOException {
        String formData = String.format(
            "client_id=%s&client_secret=%s&grant_type=refresh_token&refresh_token=%s",
            URLEncoder.encode(clientId),
            URLEncoder.encode(clientSecret),
            URLEncoder.encode(refreshToken)
        );

        return makeTokenRequest(formData);
    }

    private OrcidTokenResponse makeTokenRequest(String formData) throws IOException {
        URL url = new URL(tokenUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = formData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = connection.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    String response = reader.lines().collect(Collectors.joining());
                    return parseTokenResponse(response);
                }
            } else {
                // Read error response
                try (InputStream es = connection.getErrorStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(es))) {
                    String errorResponse = reader.lines().collect(Collectors.joining());
                    throw new IOException("Token exchange failed: " + responseCode + " - " + errorResponse);
                }
            }
        } finally {
            connection.disconnect();
        }
    }

    private OrcidTokenResponse parseTokenResponse(String jsonResponse) {
        String accessToken = extractJsonValue(jsonResponse, "access_token");
        String refreshToken = extractJsonValue(jsonResponse, "refresh_token");
        String tokenType = extractJsonValue(jsonResponse, "token_type");
        String scope = extractJsonValue(jsonResponse, "scope");
        int expiresIn = Integer.parseInt(extractJsonValue(jsonResponse, "expires_in"));
        String orcid = extractJsonValue(jsonResponse, "orcid");
        String name = extractJsonValue(jsonResponse, "name");

        return new OrcidTokenResponse(accessToken, refreshToken, tokenType, scope, expiresIn, orcid, name);
    }

    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }

        startIndex += searchKey.length();
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = json.indexOf("}", startIndex);
        }

        String value = json.substring(startIndex, endIndex).trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }

    /**
     * Simple POJO for token response
     */
    public static class OrcidTokenResponse {
        private final String accessToken;
        private final String refreshToken;
        private final String tokenType;
        private final String scope;
        private final int expiresIn;
        private final String orcid;
        private final String name;

        public OrcidTokenResponse(String accessToken, String refreshToken, String tokenType,
                                  String scope, int expiresIn, String orcid, String name) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.tokenType = tokenType;
            this.scope = scope;
            this.expiresIn = expiresIn;
            this.orcid = orcid;
            this.name = name;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public String getScope() {
            return scope;
        }

        public int getExpiresIn() {
            return expiresIn;
        }

        public String getOrcid() {
            return orcid;
        }

        public String getName() {
            return name;
        }
    }
}
