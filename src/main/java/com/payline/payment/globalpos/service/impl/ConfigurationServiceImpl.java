package com.payline.payment.globalpos.service.impl;

import com.payline.payment.globalpos.bean.configuration.RequestConfiguration;
import com.payline.payment.globalpos.exception.PluginException;
import com.payline.payment.globalpos.utils.Constants;
import com.payline.payment.globalpos.utils.PluginUtils;
import com.payline.payment.globalpos.utils.http.HttpClient;
import com.payline.payment.globalpos.utils.i18n.I18nService;
import com.payline.payment.globalpos.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.ConfigurationService;
import org.apache.logging.log4j.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationServiceImpl.class);

    private final ReleaseProperties releaseProperties = ReleaseProperties.getInstance();
    private final I18nService i18n = I18nService.getInstance();
    private HttpClient client = HttpClient.getInstance();

    private static final String I18N_CONTRACT_PREFIX = "contract.";
    private static final String NUM_TICKET = "1234";

    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        List<AbstractParameter> parameters = new ArrayList<>();

        AbstractParameter guid = new InputParameter();
        guid.setKey(Constants.ContractConfigurationKeys.GUID);
        guid.setLabel(i18n.getMessage("guid.label", locale));
        guid.setDescription(i18n.getMessage("guid.description", locale));
        guid.setRequired(true);
        parameters.add(guid);

        AbstractParameter codeMagasin = new InputParameter();
        codeMagasin.setKey(Constants.ContractConfigurationKeys.CODEMAGASIN);
        codeMagasin.setLabel(i18n.getMessage("codeMagasin.label", locale));
        codeMagasin.setDescription(i18n.getMessage("codeMagasin.description", locale));
        codeMagasin.setRequired(true);
        parameters.add(codeMagasin);

        AbstractParameter numeroCaisse = new InputParameter();
        numeroCaisse.setKey(Constants.ContractConfigurationKeys.NUMEROCAISSE);
        numeroCaisse.setLabel(i18n.getMessage("numeroCaisse.label", locale));
        numeroCaisse.setDescription(i18n.getMessage("numeroCaisse.description", locale));
        numeroCaisse.setRequired(false);
        parameters.add(numeroCaisse);

        return parameters;
    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest request) {
        final Locale locale = request.getLocale();
        final Map<String, String> errors = new HashMap<>();
        final RequestConfiguration configuration = RequestConfiguration.build(request);

        Map<String, String> accountInfo = request.getAccountInfo();

        // check required fields
        for (AbstractParameter param : this.getParameters(locale)) {
            if (param.isRequired() && PluginUtils.isEmpty(accountInfo.get(param.getKey()))) {
                String message = i18n.getMessage(I18N_CONTRACT_PREFIX + param.getKey() + ".requiredError", locale);
                errors.put(param.getKey(), message);
            }
        }

        // If partner id is missing, no need to go further, as it is required
        if (!errors.isEmpty()) {
            return errors;
        }

        try {
            String guid = request.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.GUID).getValue();
            String codeMagasin = request.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.CODEMAGASIN).getValue();
            if (PluginUtils.isEmpty(guid)) {
                errors.put(Constants.ContractConfigurationKeys.GUID, i18n.getMessage("guid.empty", locale));
            } else if (PluginUtils.isEmpty(codeMagasin)) {
                errors.put(Constants.ContractConfigurationKeys.CODEMAGASIN, i18n.getMessage("codeMagasin.empty", locale));
            } else {
                client.getTransac(configuration, NUM_TICKET);
            }
        } catch (PluginException e) {
            errors.put(Constants.ContractConfigurationKeys.GUID, i18n.getMessage("guid.invalid", locale));
            errors.put(Constants.ContractConfigurationKeys.CODEMAGASIN, i18n.getMessage("codeMagasin.invalid", locale));
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