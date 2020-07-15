package com.payline.payment.globalpos.service.impl;

import com.payline.payment.globalpos.service.HttpService;
import com.payline.payment.globalpos.utils.constant.ContractConfigurationKeys;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.ConfigurationService;
import com.toolbox.bean.configuration.RequestConfiguration;
import com.toolbox.exception.PluginException;
import com.toolbox.utils.PluginUtils;
import com.toolbox.utils.i18n.I18nService;
import com.toolbox.utils.properties.ReleaseProperties;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationServiceImpl.class);

    private ReleaseProperties releaseProperties = ReleaseProperties.getInstance();
    private final I18nService i18n = I18nService.getInstance();
    private HttpService httpService = HttpService.getInstance();

    private static final String NUM_TICKET = "1234";

    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        List<AbstractParameter> parameters = new ArrayList<>();

        AbstractParameter guid = new InputParameter();
        guid.setKey(ContractConfigurationKeys.GUID);
        guid.setLabel(i18n.getMessage("guid.label", locale));
        guid.setDescription(i18n.getMessage("guid.description", locale));
        guid.setRequired(true);
        parameters.add(guid);

        AbstractParameter storeCode = new InputParameter();
        storeCode.setKey(ContractConfigurationKeys.CODEMAGASIN);
        storeCode.setLabel(i18n.getMessage("codeMagasin.label", locale));
        storeCode.setDescription(i18n.getMessage("codeMagasin.description", locale));
        storeCode.setRequired(true);
        parameters.add(storeCode);

        AbstractParameter checkoutNumber = new InputParameter();
        checkoutNumber.setKey(ContractConfigurationKeys.NUMEROCAISSE);
        checkoutNumber.setLabel(i18n.getMessage("numeroCaisse.label", locale));
        checkoutNumber.setDescription(i18n.getMessage("numeroCaisse.description", locale));
        checkoutNumber.setRequired(false);
        parameters.add(checkoutNumber);

        return parameters;
    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest request) {
        final Locale locale = request.getLocale();
        final Map<String, String> errors = new HashMap<>();
        final RequestConfiguration configuration = RequestConfiguration.build(request);
        Map<String, String> accountInfo = request.getAccountInfo();

        String guid = accountInfo.get(ContractConfigurationKeys.GUID);
        String storeCode = accountInfo.get(ContractConfigurationKeys.CODEMAGASIN);
        String checkoutNumber = accountInfo.get(ContractConfigurationKeys.NUMEROCAISSE);
        if (PluginUtils.isEmpty(guid)) {
            errors.put(ContractConfigurationKeys.GUID, i18n.getMessage("guid.empty", locale));
        }
        if (PluginUtils.isEmpty(storeCode)) {
            errors.put(ContractConfigurationKeys.CODEMAGASIN, i18n.getMessage("codeMagasin.empty", locale));
        }

        if (errors.isEmpty()) {
            try {
                httpService.getTransact(configuration, guid, storeCode, checkoutNumber, NUM_TICKET);
            } catch (PluginException e) {
                errors.put(ContractConfigurationKeys.GUID, i18n.getMessage("guid.invalid", locale));
                errors.put(ContractConfigurationKeys.CODEMAGASIN, i18n.getMessage("codeMagasin.invalid", locale));
            }
        }

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