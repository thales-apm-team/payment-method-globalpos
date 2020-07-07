package com.payline.payment.globalpos.service;

import com.payline.payment.globalpos.bean.configuration.RequestConfiguration;
import com.payline.payment.globalpos.exception.InvalidDataException;
import com.payline.payment.globalpos.service.impl.PaymentServiceImpl;
import com.payline.payment.globalpos.utils.PluginUtils;
import com.payline.payment.globalpos.utils.constant.ContractConfigurationKeys;
import com.payline.payment.globalpos.utils.constant.PartnerConfigurationKeys;
import com.payline.payment.globalpos.utils.http.HttpClient;
import com.payline.payment.globalpos.utils.http.StringResponse;
import com.payline.payment.globalpos.utils.http.URIService;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpService {
    private static final Logger LOGGER = LogManager.getLogger(HttpService.class);
    private HttpClient client = HttpClient.getInstance();

    private static final String PIPE = "|";

    private HttpService() {
    }

    private static class Holder {
        private static final HttpService instance = new HttpService();
    }


    public static HttpService getInstance() {
        return Holder.instance;
    }
    // --- Singleton Holder pattern + initialization END


    /**
     * Initialize a GlobalPOS transaction
     *
     * @param configuration  the request configuration
     * @param guid           Id of the merchant
     * @param storeCode      Id of the store
     * @param checkoutNumber Sub id of the store
     * @param transactionId  Id of the transaction
     * @return
     */
    public String getTransact(RequestConfiguration configuration, String guid, String storeCode, String checkoutNumber, String transactionId) {
        if (configuration.getPartnerConfiguration() == null) {
            throw new InvalidDataException("PartnerConfiguration is empty");
        }

        if (configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.URL) == null) {
            throw new InvalidDataException("URL is missing");
        }

        if (PluginUtils.isEmpty(guid)) {
            throw new InvalidDataException("GUID is missing");
        }
        if (PluginUtils.isEmpty(storeCode)) {
            throw new InvalidDataException("CODEMAGASIN is missing");
        }
        String storeId = String.join(PIPE, storeCode, checkoutNumber);

        // create the valid url
        String baseUrl = configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.URL);
        URI url = URIService.createGetTransactionURL(baseUrl, guid, storeId, computeDate(), transactionId);

        // call API
        StringResponse response = client.get(url, null);

        //
        if (!response.isSuccess()) {
            String error = "GetTransaction wrong data";
            LOGGER.error(error, response.getContent());
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
        verifyData(configuration);

        // create the valid url
        String baseUrl = configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.URL);
        String guid = configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.GUID).getValue();
        URI url = URIService.createGetTitreDetailTransactionURL(baseUrl, guid, numTransac, cabTitre);

        StringResponse response = client.get(url, null);

        if (!response.isSuccess()) {
            String error = "GetTitreDetailTransac wrong data";
            LOGGER.error(error, response.getContent());
            throw new InvalidDataException(error);
        } else {
            return response.getContent();
        }
    }

    /**
     * cancel a payment ticket in a transaction
     *
     * @param configuration the request configuration
     * @param numTransac    the transaction in which we want to remove a payment ticket (partnerTransactionId)
     * @param ticketId      the payment ticket Id
     * @return
     */
    public String setAnnulTitreTransact(RequestConfiguration configuration, String numTransac, String ticketId) {
        verifyData(configuration);

        // create the valid url
        String baseUrl = configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.URL);
        String guid = configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.GUID).getValue();
        URI url = URIService.createSetAnnulTitreTransactionURL(baseUrl, guid, numTransac, ticketId);

        StringResponse response = client.get(url, null);

        if (!response.isSuccess()) {
            String error = "setAnnulTitreTransac wrong data";
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
     * @param status        the status of the transaction COMMIT or ROLLBACK
     * @return content of the StringResponse
     */
    public String setFinTransact(RequestConfiguration configuration, String numTransac, PaymentServiceImpl.STATUS status) {
        verifyData(configuration);

        // create the valid url
        String baseUrl = configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.URL);
        String guid = configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.GUID).getValue();
        URI url = URIService.createSetFinTransactionURL(baseUrl, guid, numTransac, status.name());

        StringResponse response = client.get(url, null);

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
    public void verifyData(RequestConfiguration configuration) {
        if (configuration.getPartnerConfiguration() == null) {
            throw new InvalidDataException("PartnerConfiguration is empty");
        }

        if (configuration.getContractConfiguration() == null) {
            throw new InvalidDataException("ContractConfiguration is empty");
        }

        if (configuration.getPartnerConfiguration().getProperty(PartnerConfigurationKeys.URL) == null) {
            throw new InvalidDataException("URL is missing");
        }

        if (configuration.getContractConfiguration().getProperty(ContractConfigurationKeys.GUID) == null) {
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
