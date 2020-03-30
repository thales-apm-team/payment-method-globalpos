package com.payline.payment.template.service.impl;

import com.payline.payment.template.utils.http.HttpClient;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.apache.logging.log4j.Logger;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {
    private HttpClient httpClient = HttpClient.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(PaymentWithRedirectionServiceImpl.class);

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        return null;
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        return null;
    }
}