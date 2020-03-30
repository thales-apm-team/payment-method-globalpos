package com.payline.payment.template.service.impl;

import com.payline.payment.template.bean.AmountParse;
import com.payline.payment.template.bean.configuration.RequestConfiguration;
import com.payline.payment.template.bean.nesteed.Amount;
import com.payline.payment.template.bean.nesteed.RequestHeader;
import com.payline.payment.template.bean.request.Refund;
import com.payline.payment.template.bean.response.MyRefundResponse;
import com.payline.payment.template.exception.InvalidDataException;
import com.payline.payment.template.exception.PluginException;
import com.payline.payment.template.utils.Constants;
import com.payline.payment.template.utils.http.HttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.RefundService;
import org.apache.logging.log4j.Logger;

public class RefundServiceImpl implements RefundService {
    private HttpClient client = HttpClient.getInstance();
    private static final Logger LOGGER = LogManager.getLogger(RefundServiceImpl.class);

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        try {
            RequestConfiguration configuration = RequestConfiguration.build(refundRequest);

            if (refundRequest.getRequestContext() == null || refundRequest.getRequestContext().getSensitiveRequestData() == null
                    || refundRequest.getRequestContext().getSensitiveRequestData().get(Constants.RequestContextKeys.SESSION_ID) == null) {
                throw new InvalidDataException("Missing or Invalid RedirectionPaymentRequest.requestContext");
            }

            if (refundRequest.getAmount() == null || refundRequest.getAmount().getAmountInSmallestUnit() == null
                    || refundRequest.getAmount().getCurrency() == null) {
                throw new InvalidDataException("Missing or invalid PaymentRequest.Amount");
            }

            RequestHeader requestHeader = new RequestHeader.RequestHeaderBuilder()
                    .withApiKey(refundRequest.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.API_KEY))
                    .withSessionId(refundRequest.getRequestContext().getSensitiveRequestData().get(Constants.RequestContextKeys.SESSION_ID))
                    .build();

            Amount amount = new Amount.AmountBuilder()
//                    .withValue(refundRequest.getAmount().getAmountInSmallestUnit().toString())
                    .withValue(AmountParse.split(refundRequest.getAmount()))
                    .withCurrency(refundRequest.getAmount().getCurrency().toString())
                    .build();

            // create refund request object
            Refund refund = new Refund.RefundBuilder()
                    .withRequestHeader(requestHeader)
                    .withInstallmentPlanNumber(refundRequest.getPartnerTransactionId())
                    .withAmount(amount)
                    .withrefundStrategy(Refund.refundStrategyEnum.valueOf(refundRequest.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.REFUNDSTRATEGY).getValue()))
                    .build();

            // call http method refund
            MyRefundResponse refundResponse = client.refund(configuration, refund);

            // PaymentRefundSuccess
            if (refundResponse.getResponseHeader().isSucceeded()) {
                return RefundResponseSuccess.RefundResponseSuccessBuilder.aRefundResponseSuccess()
                        .withPartnerTransactionId(refundResponse.getInstallmentPlan().getInstallmentPlanNumber())
                        .withStatusCode(String.valueOf(refundResponse.getInstallmentPlan().getInstallmentPlanStatus().getCode()))
                        .build();
            }
            // return RefundResponseFailure
            return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                    .withErrorCode(refundResponse.getResponseHeader().getErrors().get(0).getErrorCode())
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .withPartnerTransactionId(refundResponse.getInstallmentPlan().getInstallmentPlanNumber())
                    .build();

            // refund impossible
        } catch (PluginException e) {
            return e.toRefundResponseFailureBuilder().build();
        } catch (RuntimeException e) {
            LOGGER.error("unexpected plugin error", e);
            return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                    .withErrorCode(PluginException.runtimeErrorCode(e))
                    .withFailureCause(FailureCause.INVALID_DATA)
                    .build();
        }
    }

    // you can make as much refund you want, unless you you don't pass the initial amount
    // but not at the same time
    @Override
    public boolean canMultiple() {
        return false;
    }


    // you can ask for a refund of 1 dollar if you have an initial amount of 5 dollars
    @Override
    public boolean canPartial() {
        return true;
    }
}
