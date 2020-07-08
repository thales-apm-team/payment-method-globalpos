package com.payline.payment.globalpos.utils;

import com.payline.payment.globalpos.exception.InvalidDataException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

class PluginUtilsTest {

    @Test
    void createYearMonthFromExpiry() {
        YearMonth yearMonth = PluginUtils.createYearMonthFromExpiry("0121");
        Assertions.assertEquals(2021, yearMonth.getYear());
        Assertions.assertEquals(01, yearMonth.getMonthValue());
    }

    @Test
    void createYearMonthFromBadExpiry() {
        Assertions.assertThrows(InvalidDataException.class, () -> PluginUtils.createYearMonthFromExpiry(null));
        Assertions.assertThrows(InvalidDataException.class, () -> PluginUtils.createYearMonthFromExpiry(""));
        Assertions.assertThrows(InvalidDataException.class, () -> PluginUtils.createYearMonthFromExpiry("1"));
        Assertions.assertThrows(InvalidDataException.class, () -> PluginUtils.createYearMonthFromExpiry("11"));
        Assertions.assertThrows(InvalidDataException.class, () -> PluginUtils.createYearMonthFromExpiry("111"));
        Assertions.assertThrows(InvalidDataException.class, () -> PluginUtils.createYearMonthFromExpiry("11111"));
        Assertions.assertThrows(RuntimeException.class, () -> PluginUtils.createYearMonthFromExpiry("aaaa"));
    }


    @Test
    void truncate() {
        Assertions.assertEquals(null, PluginUtils.truncate(null, 10));
        Assertions.assertEquals("", PluginUtils.truncate("message", 0));
        Assertions.assertEquals("this is a ", PluginUtils.truncate("this is a long message", 10));
        Assertions.assertEquals("foo", PluginUtils.truncate("foo", 10));
    }

    @Test
    void isEmpty() {
        Assertions.assertTrue(PluginUtils.isEmpty(null));
        Assertions.assertTrue(PluginUtils.isEmpty(""));
        Assertions.assertTrue(PluginUtils.isEmpty(" "));
        Assertions.assertFalse(PluginUtils.isEmpty("foo"));
    }

}