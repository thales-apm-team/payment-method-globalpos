package com.payline.payment.globalpos.utils;


import com.payline.payment.globalpos.exception.InvalidDataException;
import com.payline.pmapi.bean.common.FailureCause;

import java.time.YearMonth;

public class PluginUtils {


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
        return value == null || value.trim().isEmpty();
    }

    public static YearMonth createYearMonthFromExpiry(String expiry) {
        if (isEmpty(expiry) || expiry.length() != 4) {
            throw new InvalidDataException("expiry must be 4 digit, not:" + expiry);

        }
        int month = Integer.parseInt(expiry.substring(0, 2));
        int year = 2000 + Integer.parseInt(expiry.substring(2, 4));

        return YearMonth.of(year, month);

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