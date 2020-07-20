package com.payline.payment.globalpos.utils.http;

import com.payline.payment.globalpos.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

class URIServiceTest {
    private final String baseURL = "https://ws-recette.easy2play.fr/wstransac";
    private final String guid = "123";
    private final String storeId = "456|789";
    private final String storeIdEncoded = "456%7C789";
    private final String dateTicket = "654";
    private final String numTicket = "321";
    private final String numTransac = "111";
    private final String cabTitre = "222";
    private final String status = PaymentServiceImpl.STATUS.COMMIT.name();

    private final String setFinTransactionURL = baseURL
            + "/" +
            "setfintransac?guid=" + guid
            + "&numtransac=" + numTransac
            + "&statut=" + status;

    private final String setAnnulTitreTransac = baseURL
            + "/" +
            "setannultitretransac?guid=" + guid +
            "&numtransac=" + numTransac +
            "&id=" + numTicket;

    @Test
    void createGetTransactionURL() {
        URI url = URIService.createGetTransactionURL(baseURL, guid, storeId, dateTicket, numTicket);
        String getTransactExpectedURL = baseURL
                + "/" +
                "gettransac?guid=" + guid
                + "&magcaisse=" + storeIdEncoded
                + "&dateticket=" + dateTicket
                + "&numticket=" + numTicket;

        Assertions.assertEquals(getTransactExpectedURL, url.toString());
    }

    @Test
    void createGetTitreDetailTransactionURL() {
        URI url = URIService.createGetTitreDetailTransactionURL(baseURL, guid, numTransac, cabTitre);
        String getTitreDetailTransactionExpectedURL = baseURL
                + "/"
                + "gettitredetailtransac?guid=" + guid
                + "&numtransac=" + numTransac
                + "&cabtitre=" + cabTitre;
        Assertions.assertEquals(getTitreDetailTransactionExpectedURL, url.toString());
    }

    @Test
    void createSetFinTransactionURL() {
        URI url = URIService.createSetFinTransactionURL(baseURL, guid, numTransac, PaymentServiceImpl.STATUS.COMMIT.name());
        Assertions.assertEquals(setFinTransactionURL, url.toString());
    }

    @Test
    void createSetAnnulTitreTransactionURL() {
        URI url = URIService.createSetAnnulTitreTransactionURL(baseURL, guid, numTransac, numTicket);
        Assertions.assertEquals(setAnnulTitreTransac, url.toString());
    }
}