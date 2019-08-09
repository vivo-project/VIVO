/* $This file is distributed under the terms of the license in /doc/license.txt$ */

package org.vivoweb.webapp.createandlink;

import org.apache.commons.lang3.StringUtils;

public class CreateAndLinkUtils {
    public static String formatAuthorString(String familyName, String givenName) {
        if (StringUtils.isEmpty(familyName)) {
            return null;
        }

        StringBuilder authorBuilder = new StringBuilder(familyName);

        if (!StringUtils.isEmpty(givenName)) {
            authorBuilder.append(", ");
            boolean addToAuthor = true;
            for (char ch : givenName.toCharArray()) {
                if (addToAuthor) {
                    if (Character.isAlphabetic(ch)) {
                        authorBuilder.append(Character.toUpperCase(ch));
                        addToAuthor = false;
                    }
                } else {
                    if (!Character.isAlphabetic(ch)) {
                        addToAuthor = true;
                    }
                }
            }
        }

        return authorBuilder.toString();
    }
}
