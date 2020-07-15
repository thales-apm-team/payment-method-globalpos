package com.payline.payment.globalpos.service.impl;

import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.toolbox.service.LogoPaymentFormConfigurationService;
import com.toolbox.utils.i18n.I18nService;

public class PaymentFormConfigurationServiceImpl extends LogoPaymentFormConfigurationService {

    private static final String NO_FIELD_TEXT = "form.button.text";
    private static final String NO_FIELD_DESCRIPTION = "form.button.description";

    private I18nService i18n = I18nService.getInstance();

    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        NoFieldForm noFieldForm = NoFieldForm.NoFieldFormBuilder.aNoFieldForm()
                .withDisplayButton(true)
                .withButtonText(i18n.getMessage(NO_FIELD_TEXT, paymentFormConfigurationRequest.getLocale()))
                .withDescription(i18n.getMessage(NO_FIELD_DESCRIPTION, paymentFormConfigurationRequest.getLocale()))
                .build();

        return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder.aPaymentFormConfigurationResponseSpecific()
                .withPaymentForm(noFieldForm)
                .build();
    }
}
