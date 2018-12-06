/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.createandlink.utils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class HttpReader {
    public static String fromResponse(HttpResponse response) throws IOException {
        HttpEntity entity = response != null ? response.getEntity() : null;
        try {
            if (entity != null) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    try (InputStream in = entity.getContent()) {
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(in, writer, "UTF-8");
                        return writer.toString();
                    }
                }
            }
        } finally {
            if (entity != null) {
                EntityUtils.consume(entity);
            }
        }

        return null;
    }
}
