package org.recap.service.partnerservice;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by peris on 12/23/16.
 */
public class SCSBSimpleClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
    private final HostnameVerifier verifier;

    /**
     * Instantiates a new scsb simple client http request factory, which is used to handle the ssl.
     *
     * @param verifier the verifier
     */
    public SCSBSimpleClientHttpRequestFactory(HostnameVerifier verifier) {
        this.verifier = verifier;
    }

    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        if (connection instanceof HttpsURLConnection) {
            ((HttpsURLConnection) connection).setHostnameVerifier(verifier);
        }
        super.prepareConnection(connection, httpMethod);
    }

}
