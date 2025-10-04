/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.sec.common.pki;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

final public class BCInitializer {

    public static final String BOUNCY_CASTLE_PROVIDER_NAME = BouncyCastleProvider.PROVIDER_NAME;

    private BCInitializer() {
        //static class
    }

    private static final boolean initialized;

    static {
        // Not thread safe:
        if (Security.getProvider(BOUNCY_CASTLE_PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        initialized = true;
    }

    public static boolean initialized() {
        return initialized;
    }

    public static Provider getProvider(String name) {
        return Security.getProvider(name);
    }

}
