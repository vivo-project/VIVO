package edu.cornell.mannlib.vivo.orcid.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.cornell.mannlib.vivo.orcid.export.model.common.BaseEntityDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OrcidExternalOperationsUtil {

    private static final Log log = LogFactory.getLog(OrcidExternalOperationsUtil.class);


    @Nullable
    public static String pushToOrcid(String orcidId, String endpoint, Object dto, String accessToken,
                                     boolean alreadyPushed, String resourceUri, boolean sandboxed) throws IOException {
        String url = String.format("https://api%s.orcid.org/v3.0/%s/%s",
            sandboxed ? ".sandbox" : "",
            orcidId.replace("http://orcid.org/", ""),
            endpoint
        );

        if (alreadyPushed) {
            String updateCode = OrcidInternalOperationsUtil.readOrcidUpdateCode(resourceUri);
            ((BaseEntityDTO) dto).setPutCode(Integer.parseInt(updateCode));
            url += "/" + updateCode;
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        try {
            connection.setRequestMethod(alreadyPushed ? "PUT" : "POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization",
                "Bearer " + OrcidInternalOperationsUtil.decryptSecret(accessToken));
            connection.setDoOutput(true);

            String requestBody = convertToJson(dto);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                String location = connection.getHeaderField("Location");
                if (location == null) {
                    return null;
                }

                return location.substring(location.lastIndexOf('/') + 1);
            } else {
                try (InputStream es = connection.getErrorStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(es))) {
                    String errorResponse = reader.lines().collect(Collectors.joining());
                    log.warn("ORCID API request failed: " + responseCode + " - " + errorResponse);
                    if (responseCode == HttpURLConnection.HTTP_NOT_FOUND && alreadyPushed) {
                        OrcidInternalOperationsUtil.setPushed(resourceUri, false);
                        return "RETRY";
                    }
                }
            }
        } finally {
            connection.disconnect();
        }

        return null; // should never return here
    }

    private static String convertToJson(Object dto) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(dto);
    }
}
