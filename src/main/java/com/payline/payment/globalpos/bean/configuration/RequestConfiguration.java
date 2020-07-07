package com.payline.payment.globalpos.bean.configuration;

import com.payline.payment.globalpos.exception.InvalidDataException;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.Environment;
import com.payline.pmapi.bean.payment.request.PaymentRequest;

/**
 * Generic class that supports any type of request's configuration.
 */
public class RequestConfiguration {

    private ContractConfiguration contractConfiguration;
    private Environment environment;
    private PartnerConfiguration partnerConfiguration;

    public RequestConfiguration(ContractConfiguration contractConfiguration, Environment environment, PartnerConfiguration partnerConfiguration) {
        if (contractConfiguration == null) {
            throw new InvalidDataException("Missing request contractConfiguration");
        }
        if (environment == null) {
            throw new InvalidDataException("Missing request environment");
        }
        if (partnerConfiguration == null) {
            throw new InvalidDataException("Missing request partnerConfiguration");
        }
        this.contractConfiguration = contractConfiguration;
        this.environment = environment;
        this.partnerConfiguration = partnerConfiguration;
    }

    public ContractConfiguration getContractConfiguration() {
        return contractConfiguration;
    }

    public PartnerConfiguration getPartnerConfiguration() {
        return partnerConfiguration;
    }

    public static RequestConfiguration build(PaymentRequest request) {
        return new RequestConfiguration(request.getContractConfiguration(), request.getEnvironment(), request.getPartnerConfiguration());
    }

    public static RequestConfiguration build(ContractParametersCheckRequest request) {
        return new RequestConfiguration(request.getContractConfiguration(), request.getEnvironment(), request.getPartnerConfiguration());
    }

}
