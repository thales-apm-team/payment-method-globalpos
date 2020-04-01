package com.payline.payment.globalpos.utils.http;

import com.payline.payment.globalpos.MockUtils;
import com.payline.payment.globalpos.bean.configuration.RequestConfiguration;
import com.payline.payment.globalpos.exception.InvalidDataException;
import com.payline.payment.globalpos.service.impl.PaymentServiceImpl;
import com.payline.payment.globalpos.utils.Constants;
import com.payline.pmapi.bean.payment.ContractProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.HashMap;
import java.util.Map;

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

        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put(Constants.ContractConfigurationKeys.CODEMAGASIN, new ContractProperty(MockUtils.getCodeMagasin()));
        contractProperties.put(Constants.ContractConfigurationKeys.NUMEROCAISSE, new ContractProperty(MockUtils.getNumeroCaisse()));

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfigurationNoMap(contractProperties)
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        Throwable thrown = assertThrows(InvalidDataException.class,
                () -> client.getTransac(configuration, MockUtils.getTRANSACTIONID()));

        Assertions.assertEquals("GUID is missing", thrown.getMessage());
    }

    @Test
    void getTransacNoCodeMagasin() {
        StringResponse stringResponse = MockUtils.mockStringResponse(200
                , "OK"
                , MockUtils.getTransacKO()
                , null);
        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put(Constants.ContractConfigurationKeys.GUID, new ContractProperty(MockUtils.getGuid()));
        contractProperties.put(Constants.ContractConfigurationKeys.NUMEROCAISSE, new ContractProperty(MockUtils.getNumeroCaisse()));

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfigurationNoMap(contractProperties)
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        Throwable thrown = assertThrows(InvalidDataException.class,
                () -> client.getTransac(configuration, MockUtils.getTRANSACTIONID()));

        Assertions.assertEquals("CODEMAGASIN is missing", thrown.getMessage());
    }

    @Test
    void getTransacCode400() {
        StringResponse stringResponse = MockUtils.mockStringResponse(400
                , "OK"
                , MockUtils.getTransacKO()
                , null);
        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        Throwable thrown = assertThrows(InvalidDataException.class,
                () -> client.getTransac(configuration, MockUtils.getTRANSACTIONID()));

        Assertions.assertEquals("GetTransaction wrong data", thrown.getMessage());

    }

    @Test
    void getTitreDetailTransacOK() {
        StringResponse stringResponse = MockUtils.mockStringResponse(200
                , "OK"
                , MockUtils.getTitreTransacOK()
                , null);
        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        Assertions.assertDoesNotThrow(() -> client.getTitreDetailTransac(configuration, MockUtils.getNumTransac(), MockUtils.getTitre()));
        Assertions.assertEquals(MockUtils.getTitreTransacOK(), stringResponse.getContent());

    }

    @Test
    void getTitreDetailTransacCode400() {
        StringResponse stringResponse = MockUtils.mockStringResponse(400
                , "OK"
                , MockUtils.getTitreTransacOK()
                , null);
        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        Throwable thrown = assertThrows(InvalidDataException.class,
                () -> client.getTitreDetailTransac(configuration, MockUtils.getNumTransac(), MockUtils.getTitre()));

        Assertions.assertEquals("GetTitreDetailTransac wrong data", thrown.getMessage());

    }

    @Test
    void setFinTransacOK() {
        StringResponse stringResponse = MockUtils.mockStringResponse(200
                , "OK"
                , MockUtils.setFinTransacOK()
                , null);
        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        Assertions.assertDoesNotThrow(() -> client.setFinTransac(configuration, MockUtils.getNumTransac(), PaymentServiceImpl.STATUS.COMMIT));
        Assertions.assertEquals(MockUtils.setFinTransacOK(), stringResponse.getContent());

    }

    @Test
    void setFinTransacCode400() {
        StringResponse stringResponse = MockUtils.mockStringResponse(400
                , "OK"
                , MockUtils.setFinTransacOK()
                , null);
        Mockito.doReturn(stringResponse).when(client).get(any(), any());

        RequestConfiguration configuration = new RequestConfiguration(
                MockUtils.aContractConfiguration()
                , MockUtils.anEnvironment()
                , MockUtils.aPartnerConfiguration()
        );

        Throwable thrown = assertThrows(InvalidDataException.class,
                () -> client.setFinTransac(configuration, MockUtils.getNumTransac(), PaymentServiceImpl.STATUS.COMMIT));

        Assertions.assertEquals("setFinTransac wrong data", thrown.getMessage());

    }

    @Test
    void verifData() {
    }

    @Test
    void computeDate() {
    }
}