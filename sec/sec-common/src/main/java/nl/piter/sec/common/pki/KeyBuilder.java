/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.sec.common.pki;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class KeyBuilder {

    private String type = "RSA";
    private int size = 2048;

    public KeyBuilder() {
    }

    public KeyBuilder type(String type) {
        this.type = type;
        return this;
    }

    public KeyBuilder size(int size) {
        this.size = size;
        return this;
    }

    public KeyPair buildKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(type);
        generator.initialize(size);
        return generator.generateKeyPair();
    }

}
