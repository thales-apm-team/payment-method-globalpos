package com.payline.payment.globalpos.utils;

import com.payline.pmapi.bean.common.FailureCause;


public class MdpUtils {

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