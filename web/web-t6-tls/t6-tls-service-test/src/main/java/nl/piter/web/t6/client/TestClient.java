package nl.piter.web.t6.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Stand alone client.
 */
@Slf4j
public class TestClient {

    private static String serviceUrl = "https://localhost:9443";

    public static void main(String[] args) {
        try {
            TestClient client = new TestClient();
            client.testPing();
            client.testSecure();
            client.testSecureAuth();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void testPing() throws Exception {
        String urlPath = "ping";
        RestTemplate restTemplate = createSecureRestTemplate();
        log.debug("ping:'{}'", urlPath);
        //
        ResponseEntity<String> response = restTemplate.getForEntity(serviceUrl + "/" + urlPath, String.class);
        //
        log.debug("RESPONSE :{}", response);
        log.debug("BODY     :{}", response.getBody());
    }

    public void testSecure() throws Exception {
        String urlPath = "secure";
        RestTemplate restTemplate = createSecureRestTemplate();
        log.debug("secure:'{}'", urlPath);
        //
        ResponseEntity<String> response = restTemplate.getForEntity(serviceUrl + "/" + urlPath, String.class);
        //
        log.debug("RESPONSE :{}", response);
        log.debug("BODY     :{}", response.getBody());
    }

    public void testSecureAuth() throws Exception {
        String urlPath = "secure/auth";
        RestTemplate restTemplate = createSecureRestTemplate();
        log.debug("secure:'{}'", urlPath);
        //
        ResponseEntity<String> response = restTemplate.getForEntity(serviceUrl + "/" + urlPath, String.class);
        //
        log.debug("RESPONSE :{}", response);
        log.debug("BODY     :{}", response.getBody());
    }

    public static RestTemplate createSecureRestTemplate() throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        final boolean useHostnameVerification = false;
        final String password = "password";

        URL keystoreUrl = ResourceUtils.getURL("classpath:keystores/customer.p12");
        URL truststoreUrl = ResourceUtils.getURL("classpath:keystores/root-ca.ts.p12");

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(keystoreUrl.openStream(), password.toCharArray());
        //
        SSLContext sslContext = SSLContextBuilder.create()
                .loadKeyMaterial(keyStore, password.toCharArray())
                .loadTrustMaterial(truststoreUrl, password.toCharArray())
                .setProtocol("TLSv1.2")
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
}
