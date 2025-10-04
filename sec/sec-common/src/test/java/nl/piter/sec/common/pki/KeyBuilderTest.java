/*/
 */ (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 */ https://www.piter.nl/github
 */ See LICENSE.txt for more details.
 */
//
package nl.piter.sec.common.pki;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KeyBuilderTest {

    @Test
    void buildKeyPair2048() throws NoSuchAlgorithmException {
        KeyPair kp = new KeyBuilder()
                .size(2048)
                .type("RSA")
                .buildKeyPair();
        assertThat(kp.getPrivate()).isInstanceOf(RSAPrivateKey.class);
        RSAPrivateKey rsaKey = (RSAPrivateKey) kp.getPrivate();
        assertThat(rsaKey.getAlgorithm().equals("RSA"));
        assertThat(rsaKey.getModulus().bitLength()).isEqualTo(2048);
    }

    @Test
    void buildKeyPair4096() throws NoSuchAlgorithmException {
        KeyPair kp = new KeyBuilder()
                .size(4096)
                .type("RSA")
                .buildKeyPair();
        assertThat(kp.getPrivate()).isInstanceOf(RSAPrivateKey.class);
        RSAPrivateKey rsaKey = (RSAPrivateKey) kp.getPrivate();
        assertThat(rsaKey.getAlgorithm().equals("RSA"));
        assertThat(rsaKey.getModulus().bitLength()).isEqualTo(4096);
    }

}
