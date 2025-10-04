/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.sec.common.pki;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.naming.NamingException;
import javax.naming.ldap.Rdn;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class CertUtilTest {

    @Test
    void parseSubjectDN() throws NamingException {
        List<Rdn> list = CertUtil.parseRDN("CN=hello-world,O=organization,OU=organizational-unit,C=FRL", true);
        list.forEach(el -> log.info(" - entry: '{}': '{}'", el.getType(), el.getValue()));
        List<String> names = new ArrayList(list.stream().map(rdn -> rdn.getType()).toList());
        names.sort(String::compareTo);
        assertThat(names.size()).isEqualTo(4);
        // Check whether the RDN order is kept as-is:
        assertThat(names).isEqualTo(Arrays.asList("C", "CN", "O", "OU"));
    }

}
