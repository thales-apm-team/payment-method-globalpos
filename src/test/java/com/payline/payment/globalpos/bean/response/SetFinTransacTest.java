package com.payline.payment.globalpos.bean.response;

import com.payline.payment.globalpos.MockUtils;
import com.toolbox.exception.InvalidDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SetFinTransacTest {

    @Test
    void fromXml() {
        SetFinTransac value = SetFinTransac.fromXml(MockUtils.setFinTransacOK());
        Assertions.assertNotNull(value.getCodeErreur());
        Assertions.assertEquals("1", value.getCodeErreur());
    }

    @Test
    void noXml() {
        Throwable thrown = assertThrows(InvalidDataException.class,
                () -> SetFinTransac.fromXml(MockUtils.noXml()));

        Assertions.assertEquals("Unable to parse XML SetFinTransac", thrown.getMessage());
    }
}