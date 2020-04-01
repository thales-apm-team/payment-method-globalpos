package com.payline.payment.globalpos.utils;


import com.payline.payment.globalpos.utils.http.HttpClient;
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
     * Map some errors codes for the appropriate FailureCause
     *
     * @param errorCode
     * @return FailureCause
     */
    public static FailureCause getFailureCause(String errorCode) {
        FailureCause cause;

        switch (errorCode) {
            case "-21":
            case "-30":
                cause = FailureCause.COMMUNICATION_ERROR;
                break;

            case "-60":
            case "-70":
            case "-80":
                cause = FailureCause.REFUSED;
                break;

            default:
                cause = FailureCause.INVALID_DATA;
        }
        return cause;
    }
}