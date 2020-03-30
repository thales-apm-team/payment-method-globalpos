package com.payline.payment.template.service.impl;

import com.payline.payment.template.utils.http.HttpClient;
import com.payline.pmapi.bean.reset.request.ResetRequest;
import com.payline.pmapi.bean.reset.response.ResetResponse;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.ResetService;
import org.apache.logging.log4j.Logger;


public class ResetServiceImpl implements ResetService {
    private HttpClient client = HttpClient.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(ResetServiceImpl.class);

    @Override
    public ResetResponse resetRequest(ResetRequest resetRequest) {
        return null;
    }


    // you can't reset a check
    @Override
    public boolean canMultiple() {
        return false;
    }

    // you can't reset a check
    @Override
    public boolean canPartial() {
        return false;
    }
}