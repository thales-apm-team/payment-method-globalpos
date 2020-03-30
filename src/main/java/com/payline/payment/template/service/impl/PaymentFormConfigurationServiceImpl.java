package com.payline.payment.template.service.impl;

import com.payline.payment.template.service.LogoPaymentFormConfigurationService;
import com.payline.payment.template.utils.i18n.I18nService;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;

public class PaymentFormConfigurationServiceImpl extends LogoPaymentFormConfigurationService {

    private I18nService i18n = I18nService.getInstance();

    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        return null;
    }
}
