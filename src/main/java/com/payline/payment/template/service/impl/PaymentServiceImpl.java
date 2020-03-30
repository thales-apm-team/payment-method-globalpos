package com.payline.payment.template.service.impl;

import com.payline.payment.template.utils.http.HttpClient;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentService;
import org.apache.logging.log4j.Logger;

public class PaymentServiceImpl implements PaymentService {
    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);
    private HttpClient httpClient = HttpClient.getInstance();

    @Override
    public PaymentResponse paymentRequest(PaymentRequest request) {
        return null;
    }
}
