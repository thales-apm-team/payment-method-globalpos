package com.payline.payment.globalpos.utils.http;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.globalpos.bean.configuration.RequestConfiguration;
import com.payline.payment.globalpos.exception.InvalidDataException;
import com.payline.payment.globalpos.exception.PluginException;
import com.payline.payment.globalpos.service.impl.PaymentServiceImpl;
import com.payline.payment.globalpos.utils.Constants;
import com.payline.payment.globalpos.utils.properties.ConfigProperties;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HttpClient {
    private static final Logger LOGGER = LogManager.getLogger(HttpClient.class);
    private Gson parser;

    // Exceptions messages
    private static final String SERVICE_URL_ERROR = "Service URL is invalid";

    /**
     * The number of time the client must retry to send the request if it doesn't obtain a response.
     */
    private int retries;

    private org.apache.http.client.HttpClient client;

    // --- Singleton Holder pattern + initialization BEGIN

    private HttpClient() {
        this.parser = new GsonBuilder().create();

        int connectionRequestTimeout;
        int connectTimeout;
        int socketTimeout;
        try {
            // request config timeouts (in seconds)
            ConfigProperties config = ConfigProperties.getInstance();
            connectionRequestTimeout = Integer.parseInt(config.get("http.connectionRequestTimeout"));
            connectTimeout = Integer.parseInt(config.get("http.connectTimeout"));
            socketTimeout = Integer.parseInt(config.get("http.socketTimeout"));

            // retries
            this.retries = Integer.parseInt(config.get("http.retries"));
        } catch (NumberFormatException e) {
            throw new PluginException("plugin error: http.* properties must be integers", e);
        }

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout * 1000)
                .setConnectTimeout(connectTimeout * 1000)
                .setSocketTimeout(socketTimeout * 1000)
                .build();

        // instantiate Apache HTTP client
        this.client = HttpClientBuilder.create()
                .useSystemProperties()
                .setDefaultRequestConfig(requestConfig)
                .setSSLSocketFactory(new SSLConnectionSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory(), SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
                .build();

    }

    private static class Holder {
        private static final HttpClient instance = new HttpClient();
    }


    public static HttpClient getInstance() {
        return Holder.instance;
    }
    // --- Singleton Holder pattern + initialization END

//    private Header[] createHeaders() {
//        Header[] headers = new Header[0];
//        headers[0] = new BasicHeader("Content-Type", "application/json");
//        headers[1] = new BasicHeader("Accept", "application/json");
//        return headers;
//    }

    /**
     * Send the request, with a retry system in case the client does not obtain a proper response from the server.
     *
     * @param httpRequest The request to send.
     * @return The response converted as a {@link StringResponse}.
     * @throws PluginException If an error repeatedly occurs and no proper response is obtained.
     */
    StringResponse execute(HttpRequestBase httpRequest) {
        StringResponse strResponse = null;
        int attempts = 1;

        while (strResponse == null && attempts <= this.retries) {
            LOGGER.info("Start call to partner API [{} {}] (attempt {})", httpRequest.getMethod(), httpRequest.getURI(), attempts);
            try (CloseableHttpResponse httpResponse = (CloseableHttpResponse) this.client.execute(httpRequest)) {
                strResponse = StringResponse.fromHttpResponse(httpResponse);
            } catch (IOException e) {
                LOGGER.error("An error occurred during the HTTP call :", e);
                strResponse = null;
            } finally {
                attempts++;
            }
        }

        if (strResponse == null) {
            throw new PluginException("Failed to contact the partner API", FailureCause.COMMUNICATION_ERROR);
        }
        LOGGER.info("APIResponseError obtained from partner API [{} {}]", strResponse.getStatusCode(), strResponse.getStatusMessage());
        return strResponse;
    }

    /**
     * Manage Get API call
     *
     * @param url     the url to call
     * @param headers header(s) of the request
     * @return
     */
    StringResponse get(String url, Header[] headers) {
        URI uri;
        try {
            // Add the createOrderId to the url
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new InvalidDataException(SERVICE_URL_ERROR, e);
        }

        final HttpGet httpGet = new HttpGet(uri);
        httpGet.setHeaders(headers);

        // Execute request
        return this.execute(httpGet);
    }

    /**
     * Manage Post API call
     *
     * @param url     the url to call
     * @param headers header(s) of the request
     * @param body    the body of the request
     * @return
     */
    StringResponse post(String url, Header[] headers, StringEntity body) {
        URI uri;
        try {
            // Add the createOrderId to the url
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new InvalidDataException(SERVICE_URL_ERROR, e);
        }

        final HttpPost httpPost = new HttpPost(uri);
        httpPost.setHeaders(headers);
        httpPost.setEntity(body);

        // Execute request
        return this.execute(httpPost);
    }

    /**
     * Initialise a transaction GlobalPOS
     *
     * @param configuration the request configuration
     * @param numTicket     transactionID Payline
     * @return content of the StringResponse
     */
    public String getTransac(RequestConfiguration configuration, String numTicket) {
        verifData(configuration);
        // url encode for the symbol |
        // The API didn't control the numeroCaisse, but the url should contain a | after the CodeMagasin
        String numCaisse = "%7C";

        if (configuration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.CODEMAGASIN) == null) {
            throw new InvalidDataException("CODEMAGASIN is missing");
        }

        if (configuration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.NUMEROCAISSE) != null) {
            numCaisse += configuration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.NUMEROCAISSE);
        }

        // create the valid url
        String url = configuration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.URL)
                + "/"
                + "gettransac?guid="
                + configuration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.GUID)
                + "&magcaisse="
                + configuration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.CODEMAGASIN)
                + numCaisse
                + "&dateticket="
                + computeDate()
                + "&numticket="
                + numTicket;

        StringResponse response = get(url, null);

        if (!response.isSuccess()) {
            String error = "GetTransaction wrong data";
            LOGGER.error(error);
            throw new InvalidDataException(error);
        } else {
            return response.getContent();
        }
    }

    /**
     * Show all details of a check GlobalPOS
     *
     * @param configuration the request configuration
     * @param numTransac    partnerTransactionID
     * @return content of the StringResponse
     */
    public String getTitreDetailTransac(RequestConfiguration configuration, String numTransac, String cabTitre) {
        verifData(configuration);

        // create the valid url
        String url = configuration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.URL)
                + "/"
                + "gettitredetailtransac?guid="
                + configuration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.GUID)
                + "&numtransac="
                + numTransac
                + "&cabtitre="
                + cabTitre;

        StringResponse response = get(url, null);

        if (!response.isSuccess()) {
            String error = "GetTitreDetailTransac wrong data";
            LOGGER.error(error);
            throw new InvalidDataException(error);
        } else {
            return response.getContent();
        }
    }

    /**
     * end a transaction for GlobalPOS
     *
     * @param configuration the request configuration
     * @param numTransac    partnerTransactionID
     * @return content of the StringResponse
     */
    public String setFinTransac(RequestConfiguration configuration, String numTransac, PaymentServiceImpl.STATUS status) {
        verifData(configuration);

        // create the valid url
        String url = configuration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.URL)
                + "/"
                + "setfintransac?guid="
                + configuration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.GUID)
                + "&numtransac="
                + numTransac
                + "&statut="
                + status;

        StringResponse response = get(url, null);

        if (!response.isSuccess()) {
            String error = "setFinTransac wrong data";
            LOGGER.error(error);
            throw new InvalidDataException(error);
        } else {
            return response.getContent();
        }
    }

    /**
     * Check if the datas required for all request are empty or not
     *
     * @param configuration the request configuration
     */
    public void verifData(RequestConfiguration configuration) {
        if (configuration.getPartnerConfiguration() == null) {
            throw new InvalidDataException("PartnerConfiguration is empty");
        }

        if (configuration.getContractConfiguration() == null) {
            throw new InvalidDataException("ContractConfiguration is empty");
        }

        if (configuration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.URL) == null) {
            throw new InvalidDataException("URL is missing");
        }

        if (configuration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.GUID) == null) {
            throw new InvalidDataException("GUID is missing");
        }
    }

    /**
     * compute the date of the day for initialise the transaction
     * date have format yyyyMMjjHHMMSS
     *
     * @return String the date of the day
     */
    public String computeDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date());
    }
}