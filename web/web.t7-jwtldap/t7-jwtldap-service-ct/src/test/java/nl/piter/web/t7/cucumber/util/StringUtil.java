/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.cucumber.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;

public class StringUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    public static Set<String> jsonToStringSet(String json) {
        try {
            Set set = mapper.readValue(json, Set.class);
            return set;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
