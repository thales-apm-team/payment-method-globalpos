package com.payline.payment.template.utils;


import com.payline.payment.template.utils.http.HttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;

public class PluginUtils {
    private static HttpClient client = HttpClient.getInstance();


    private PluginUtils() {
        // ras.
    }

    public static String truncate(String value, int length) {
        if (value != null && value.length() > length) {
            value = value.substring(0, length);
        }
        return value;
    }


    /**
     * Check if a String is null or empty
     *
     * @param value the String to check
     * @return
     */
    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }


    /**
     * Map some errors codes for create the paymentResponseFailure
     *
     * @param errorCode
     * @return
     */
//    public static PaymentResponseFailure paymentResponseFailure(String errorCode) {
//        FailureCause cause;
//
//        switch (errorCode) {
//            case "599":
//                cause = FailureCause.COMMUNICATION_ERROR;
//                break;
//
//            case "400":
//                cause = FailureCause.INVALID_FIELD_FORMAT;
//                break;
//
//            default:
//                cause = FailureCause.INVALID_DATA;
//        }
//        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
//                .withErrorCode(errorCode)
//                .withFailureCause(cause)
//                .build();
//    }
}