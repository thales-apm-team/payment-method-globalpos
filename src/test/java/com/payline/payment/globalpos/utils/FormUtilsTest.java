package com.payline.payment.globalpos.utils;


import com.payline.payment.globalpos.exception.InvalidDataException;
import com.payline.payment.globalpos.utils.form.FormUtils;
import com.payline.payment.globalpos.utils.i18n.I18nService;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertThrows;

 class FormUtilsTest {

    private FormUtils formUtils = FormUtils.getInstance();
    private I18nService i18n = I18nService.getInstance();

     private final String amount = "12";
     private final String currencySymbol = "€";
     private final Locale locale = Locale.FRANCE;

        @Test
        void createRetryForm() {

            PaymentFormConfigurationResponseSpecific paymentFormConfigurationResponseSpecific = formUtils.createRetryForm(locale,amount,currencySymbol);

            Assertions.assertEquals(paymentFormConfigurationResponseSpecific.getPaymentForm().getDescription(), i18n.getMessage("customFormTitre.description", locale));
        }
        @Test
        void createRetryFormWithNullAmount() {
             assertThrows(InvalidDataException.class, () -> formUtils.createRetryForm(locale,null,currencySymbol));
        }

        @Test
        void createRetryFormWithNullCurrencySymbol() {
             assertThrows(InvalidDataException.class, () -> formUtils.createRetryForm(locale,amount,null));
        }

        @Test
        void createRetryFormWithNullLocale() {
             assertThrows(InvalidDataException.class, () -> formUtils.createRetryForm(null,amount,currencySymbol));
        }
}
