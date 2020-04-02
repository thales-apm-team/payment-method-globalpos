package com.payline.payment.globalpos.service.impl;

import com.payline.payment.globalpos.MockUtils;
import com.payline.payment.globalpos.bean.configuration.RequestConfiguration;
import com.payline.payment.globalpos.bean.response.GetTransac;
import com.payline.payment.globalpos.utils.http.HttpClient;
import com.payline.payment.globalpos.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

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
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String version = "M.m.p";

        // given: the release properties are OK
        doReturn(version).when(releaseProperties).get("release.version");
        Calendar cal = new GregorianCalendar();
        cal.set(2019, Calendar.AUGUST, 19);
        doReturn(formatter.format(cal.getTime())).when(releaseProperties).get("release.date");

        // when: calling the method getReleaseInformation
        ReleaseInformation releaseInformation = configurationServiceImpl.getReleaseInformation();

        // then: releaseInformation contains the right values
        assertEquals(version, releaseInformation.getVersion());
        assertEquals(2019, releaseInformation.getDate().getYear());
        assertEquals(Month.AUGUST, releaseInformation.getDate().getMonth());
        assertEquals(19, releaseInformation.getDate().getDayOfMonth());
    }

    @Test
    void getName() {
        // when: calling the method getName
        String name = configurationServiceImpl.getName(Locale.getDefault());

        // then: the method returns the name
        assertNotNull(name);
    }
}