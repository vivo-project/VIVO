package edu.cornell.mannlib.vivo.orcid.export;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class OrcidExportQueries {

    public static String loadQuery(String fileName) {

        try (InputStream is = OrcidExportQueries.class
            .getClassLoader()
            .getResourceAsStream("sparql/" + fileName)
        ) {
            if (is == null) {
                throw new RuntimeException("SPARQL file not found: " + fileName);
            }

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[4096];

            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load SPARQL query: " + fileName, e);
        }
    }
}
