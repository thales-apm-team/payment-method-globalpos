package com.payline.payment.globalpos.bean;

import com.payline.pmapi.bean.common.Amount;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Currency;

public class AmountParse {

    // can't create an amountParse object
    private AmountParse() {
    }

    // put the coma on the right place depending of the currency of Monext
    // add 0 if needed, before or after
    public static String split(Amount amount) {
        int nbDigits = amount.getCurrency().getDefaultFractionDigits();
        final BigDecimal bigDecimal = new BigDecimal(amount.getAmountInSmallestUnit());
        return String.valueOf(bigDecimal.movePointLeft(nbDigits));
    }

    public static String split(Double amount, Currency currency) {
        int nbDigits = currency.getDefaultFractionDigits();
        final BigDecimal bigDecimal = BigDecimal.valueOf(amount);
        return String.valueOf(bigDecimal.movePointLeft(nbDigits));
    }


    // delete the coma for Monext API
    public static BigInteger transfoBigInt(String amount) {
        if (!amount.contains(".")) {
            return BigInteger.valueOf(Integer.parseInt(amount));
        }
        return BigInteger.valueOf(Integer.parseInt(amount.substring(0, amount.indexOf('.')) + amount.substring(amount.indexOf('.') + 1)));
    }
}
