package com.payline.payment.globalpos.integration;

import com.payline.payment.globalpos.MockUtils;
import com.payline.payment.globalpos.service.impl.ConfigurationServiceImpl;
import com.payline.payment.globalpos.service.impl.PaymentServiceImpl;
import com.payline.payment.globalpos.utils.Constants;
import com.payline.pmapi.bean.payment.RequestContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFormUpdated;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.service.ConfigurationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class PaymentIT {

    private ConfigurationService configurationService = new ConfigurationServiceImpl();
    private PaymentServiceImpl paymentService = new PaymentServiceImpl();

    PaymentRequest createPaymentRequest(RequestContext context) {

        PaymentRequest.Builder request = MockUtils.aPaylinePaymentRequestNoRequestContextBuilder()
                .withRequestContext(MockUtils.aRequestContextBuilderStep(
                        context.getRequestData().get(Constants.RequestContextKeys.CONTEXT_DATA_STEP),
                        context.getRequestData().get(Constants.RequestContextKeys.NUMTRANSAC))
                        .build()
                );
        if (context.getRequestData().get(Constants.RequestContextKeys.CONTEXT_DATA_STEP).equals("STEP2")) {
            request.withPaymentFormContext(MockUtils.aPaymentFormContextStep2(MockUtils.getTitre()));
        } else if (context.getRequestData().get(Constants.RequestContextKeys.CONTEXT_DATA_STEP).equals("STEP3")) {
            request.withPaymentFormContext(MockUtils.aPaymentFormContext());
        }
        return request.build();
    }

    @Test
    void fullPaymentTest() {
        // Login
        Map<String, String> errors = configurationService.check(MockUtils.aContractParametersCheckRequest());
        Assertions.assertEquals(0, errors.size());

        // call the getTransac
        PaymentRequest request1 = createDefaultPaymentRequest();
        PaymentResponse paymentResponseGetTransac = paymentService.paymentRequest(request1);
        Assertions.assertEquals(PaymentResponseFormUpdated.class, paymentResponseGetTransac.getClass());
        PaymentResponseFormUpdated response1 = (PaymentResponseFormUpdated) paymentResponseGetTransac;

        PaymentRequest request2 = createPaymentRequest(response1.getRequestContext());
        PaymentResponse paymentResponseGetTitreDetailTransac = paymentService.paymentRequest(request2);
        Assertions.assertEquals(PaymentResponseFormUpdated.class, paymentResponseGetTitreDetailTransac.getClass());
        PaymentResponseFormUpdated response2 = (PaymentResponseFormUpdated) paymentResponseGetTitreDetailTransac;

        PaymentRequest request3 = createPaymentRequest(response2.getRequestContext());
        PaymentResponse paymentResponseSetFinTransac = paymentService.paymentRequest(request3);
        Assertions.assertEquals(PaymentResponseSuccess.class, paymentResponseSetFinTransac.getClass());
    }


    public PaymentRequest createDefaultPaymentRequest() {
        return MockUtils.aPaylinePaymentRequest();
    }
}
