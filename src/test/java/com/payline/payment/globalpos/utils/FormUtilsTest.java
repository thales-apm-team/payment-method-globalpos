package com.payline.payment.globalpos.utils;


import com.payline.payment.globalpos.MockUtils;
import com.payline.payment.globalpos.bean.response.APIResponseError;
import com.payline.payment.globalpos.exception.InvalidDataException;
import com.payline.payment.globalpos.utils.form.FormUtils;
import com.payline.payment.globalpos.utils.i18n.I18nService;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FormUtilsTest {

    private FormUtils formUtils = FormUtils.getInstance();
    private I18nService i18n = I18nService.getInstance();


        @Test
        void createRetryForm() {
            Locale locale = Locale.FRANCE;
            String amount = "12";
            String currencySymbol = "€";

            PaymentFormConfigurationResponseSpecific paymentFormConfigurationResponseSpecific = formUtils.createRetryForm(locale,amount,currencySymbol);

            Assertions.assertEquals(paymentFormConfigurationResponseSpecific.getPaymentForm().getDescription(), i18n.getMessage("customFormTitre.description", locale));

            assertThrows(InvalidDataException.class, () -> formUtils.createRetryForm(locale,null,currencySymbol));
            assertThrows(InvalidDataException.class, () -> formUtils.createRetryForm(locale,amount,null));
            assertThrows(InvalidDataException.class, () -> formUtils.createRetryForm(null,amount,currencySymbol));


        }
} 
