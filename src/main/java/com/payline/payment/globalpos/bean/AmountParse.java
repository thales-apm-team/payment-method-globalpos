package com.payline.payment.globalpos.bean;

import com.payline.pmapi.bean.common.Amount;

import java.math.BigDecimal;

public class AmountParse {

    // can't create an amountParse object
    private AmountParse() {
    }

    // put the coma on the right place depending of the currency of payline
    // add 0 if needed, before or after
    public static BigDecimal splitDecimal(Amount amount) {
        int nbDigits = amount.getCurrency().getDefaultFractionDigits();
        final BigDecimal bigDecimal = new BigDecimal(amount.getAmountInSmallestUnit());
        return bigDecimal.movePointLeft(nbDigits);
    }
}