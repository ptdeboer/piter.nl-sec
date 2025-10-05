/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.sec.common;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CollectionUtil {

    public static <T> List<T> toListOrNull(Collection<T> collectionOfLists) {
        return (collectionOfLists != null) ? collectionOfLists.stream().toList() : null;
    }

    public static <K, V> V mapPutNotNull(Map<K, V> map, K key, V value) {
        return (value != null) ? map.put(key, value) : null;
    }

}
