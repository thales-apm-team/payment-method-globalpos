package com.payline.payment.globalpos.utils;

/**
 * Support for constants used everywhere in the plugin sources.
 */
public class Constants {

    /**
     * Keys for the entries in ContractConfiguration map.
     */
    public static class ContractConfigurationKeys {
        private final String GUID = "guid";
        private final String CODEMAGASIN = "codeMagasin";
        private final String NUMEROCAISSE = "numeroCaisse";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private ContractConfigurationKeys() {
        }
    }

    /**
     * Keys for the entries in PartnerConfiguration maps.
     */
    public static class PartnerConfigurationKeys {
        public static final String URL = "URL";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private PartnerConfigurationKeys() {
        }
    }

    /**
     * Keys for the entries in RequestContext data.
     */
    public static class RequestContextKeys {

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private RequestContextKeys() {
        }
    }

    public static class FormConfigurationKeys {
        public static final String OPTIONS = "cleDesOptions";

        public static final String PAYMENTBUTTONTEXT = "paymentButtonText.label";
        public static final String PAYMENTBUTTONDESC = "paymentButtonText.description";

        /* Static utility class : no need to instantiate it (Sonar bug fix) */
        private FormConfigurationKeys() {
        }
    }

    /* Static utility class : no need to instantiate it (Sonar bug fix) */
    private Constants() {
    }

}