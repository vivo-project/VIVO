/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.createandlink.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StringArrayDeserializer extends JsonDeserializer<String[]> {
    @Override
    public String[] deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException, JsonProcessingException {
        if (JsonToken.VALUE_NULL.equals(jsonParser.getCurrentToken())) {
            jsonParser.nextToken();
            return null;
        }

        if (JsonToken.START_ARRAY.equals(jsonParser.getCurrentToken())) {
            List<String> list = new ArrayList<>();
            while (!JsonToken.END_ARRAY.equals(jsonParser.nextToken())) {
                list.add(jsonParser.getValueAsString());
            }
            return list.toArray(new String[list.size()]);
        } else if (JsonToken.VALUE_STRING.equals(jsonParser.getCurrentToken())) {
            return new String[] { jsonParser.getText() };
        }

        return null;
    }
}
