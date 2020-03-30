package com.payline.payment.globalpos.service.impl;

import com.payline.payment.globalpos.service.LogoPaymentFormConfigurationService;
import com.payline.payment.globalpos.utils.i18n.I18nService;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;

public class PaymentFormConfigurationServiceImpl extends LogoPaymentFormConfigurationService {

    private I18nService i18n = I18nService.getInstance();

    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        return null;
    }
}
