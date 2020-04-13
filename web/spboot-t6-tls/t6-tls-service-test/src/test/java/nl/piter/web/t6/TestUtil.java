package nl.piter.web.t6;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class TestUtil {

    /**
     * Create TLS-ma enabled RestTemplate
     */
    public static RestTemplate createSecureRestTemplate(String password, String keyStoreUrl, String trustStoreUrl) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        final boolean useHostnameVerification = false;

        URL keystoreUrl = ResourceUtils.getURL(keyStoreUrl);
        URL truststoreUrl = ResourceUtils.getURL(trustStoreUrl);

        //
        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(keystoreUrl, password.toCharArray(), password.toCharArray())
                .loadTrustMaterial(truststoreUrl, password.toCharArray())
                .build();
        //
        HostnameVerifier hostnameVerifier = new NoopHostnameVerifier();
        SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(sslConnectionFactory).build();
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
        //
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(httpRequestFactory);
        return restTemplate;
    }

    /**
     * Create insecure TLS RestTemplate: Ignore HTTPS certs.
     * Can test https connections but not TLS-ma.
     */
    public static RestTemplate createInsecureRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Accept all certs:
        TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;

        // All-trusting SSLContext:
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;

    }

}
