/*/
 */ (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 */ https://www.piter.nl/github
 */ See LICENSE.txt for more details.
 */
//
package nl.piter.sec.common.pki;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static nl.piter.sec.common.data.CollectionUtil.mapPutNotNull;
import static nl.piter.sec.common.data.CollectionUtil.toListOrNull;

final public class CertUtil {

    private CertUtil() {
        //static
    }

    public static Map<String, Object> getAttributesAsMap(X509Certificate cert) throws CertificateParsingException {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("subject", cert.getSubjectX500Principal().getName());
        map.put("issuer", cert.getIssuerX500Principal().getName());
        map.put("serialNumber", cert.getSerialNumber());
        map.put("notBefore", cert.getNotBefore());
        map.put("notAfter", cert.getNotAfter());
        mapPutNotNull(map, "subjectAlternativeNames", toListOrNull(cert.getSubjectAlternativeNames()));
        //
        map.put("basicConstraints", cert.getBasicConstraints());
        mapPutNotNull(map, "extendedKeyUsage", cert.getExtendedKeyUsage());
        return map;
    }

    public static List<Rdn> parseRDN(String rdnName, boolean sort) throws InvalidNameException {
        List<Rdn> list = new ArrayList(new LdapName(rdnName).getRdns());
        if (sort) list.sort(Rdn::compareTo);
        return list;
    }

    public static void saveCert(Path file, X509Certificate cert) throws IOException {
        try (OutputStream outps = Files.newOutputStream(file, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            outps.write(cert.getEncoded());
            outps.flush();
        } catch (IOException | CertificateEncodingException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    public static String paddRight(String str, int n) {
        return String.format("%-" + n + "s", str);
    }

    public static String paddLeft(String str, int n) {
        return String.format("%" + n + "s", str);
    }

}
