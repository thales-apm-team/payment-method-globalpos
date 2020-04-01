package com.payline.payment.globalpos.utils.http;

import com.payline.payment.globalpos.MockUtils;
import com.payline.payment.globalpos.bean.configuration.RequestConfiguration;
import com.payline.payment.globalpos.exception.InvalidDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

class HttpClientTest {

    @Spy
    @InjectMocks
    private HttpClient client = HttpClient.getInstance();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void getTransacOK() {
        StringResponse stringResponse = MockUtils.mockStringResponse(200
                , "OK"
                , MockUtils.getTransacOK()
                , null);
        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        Assertions.assertDoesNotThrow(() -> client.getTransac(configuration, MockUtils.getTRANSACTIONID()));
        Assertions.assertEquals(MockUtils.getTransacOK(), stringResponse.getContent());
    }


    @Test
    void getTransacKObutOK() {
        StringResponse stringResponse = MockUtils.mockStringResponse(200
                , "OK"
                , MockUtils.getTransacKO()
                , null);
        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        Assertions.assertDoesNotThrow(() -> client.getTransac(configuration, MockUtils.getTRANSACTIONID()));
        Assertions.assertEquals(MockUtils.getTransacKO(), stringResponse.getContent());
    }

    @Test
    void getTransacNoGUID() {
        StringResponse stringResponse = MockUtils.mockStringResponse(200
                , "OK"
                , MockUtils.getTransacKO()
                , null);
        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfigurationNoGUID()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        Throwable thrown = assertThrows(InvalidDataException.class,
                () -> client.getTransac(configuration, MockUtils.getTRANSACTIONID()));

        Assertions.assertEquals(thrown.getMessage(), "GUID is missing");
    }

    @Test
    void verifData() {
    }

    @Test
    void computeDate() {
    }
}