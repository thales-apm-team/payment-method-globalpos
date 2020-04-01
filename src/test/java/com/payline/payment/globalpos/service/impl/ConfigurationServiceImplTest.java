package com.payline.payment.globalpos.service.impl;

import com.payline.payment.globalpos.MockUtils;
import com.payline.payment.globalpos.bean.configuration.RequestConfiguration;
import com.payline.payment.globalpos.bean.response.GetTransac;
import com.payline.payment.globalpos.utils.http.HttpClient;
import com.payline.payment.globalpos.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class ConfigurationServiceImplTest {

    @InjectMocks
    ConfigurationServiceImpl configurationServiceImpl = new ConfigurationServiceImpl();
    @Mock
    private ReleaseProperties releaseProperties;
    Locale locale = Locale.getDefault();

    @Mock
    private HttpClient client;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void getParameters() {
    }

    @Test
    void check() {
        ContractParametersCheckRequest checkRequest = MockUtils.aContractParametersCheckRequest();
        Mockito.doReturn(MockUtils.getTransacOK()).when(client).getTransac(any(RequestConfiguration.class), any());
        Map<String, String> errors = configurationServiceImpl.check(checkRequest);
        Assertions.assertEquals(0, errors.size());
    }

    @Test
    void getReleaseInformation() {
    }

    @Test
    void getName() {
    }
}