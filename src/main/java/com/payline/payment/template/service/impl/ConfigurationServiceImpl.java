package com.payline.payment.template.service.impl;

import com.payline.payment.template.bean.configuration.RequestConfiguration;
import com.payline.payment.template.exception.PluginException;
import com.payline.payment.template.utils.PluginUtils;
import com.payline.payment.template.utils.http.HttpClient;
import com.payline.payment.template.utils.i18n.I18nService;
import com.payline.payment.template.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.ConfigurationService;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationServiceImpl.class);

    private ReleaseProperties releaseProperties = ReleaseProperties.getInstance();
    private I18nService i18n = I18nService.getInstance();
    private HttpClient client = HttpClient.getInstance();

    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        List<AbstractParameter> parameters = new ArrayList<>();


        return parameters;
    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest request) {
        final Locale locale = request.getLocale();
        final Map<String, String> errors = new HashMap<>();
        final RequestConfiguration configuration = RequestConfiguration.build(request);


        return errors;
    }

    @Override
    public ReleaseInformation getReleaseInformation() {
        String date = releaseProperties.get("release.date");
        String version = releaseProperties.get("release.version");

        if (PluginUtils.isEmpty(date)) {
            LOGGER.error("Date is not defined");
            throw new PluginException("Plugin error: Date is not defined");
        }

        if (PluginUtils.isEmpty(version)) {
            LOGGER.error("Version is not defined");
            throw new PluginException("Plugin error: Version is not defined");
        }
        return ReleaseInformation.ReleaseBuilder.aRelease()
                .withDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .withVersion(version)
                .build();
    }

    @Override
    public String getName(Locale locale) {
        return i18n.getMessage("paymentMethod.name", locale);
    }
}