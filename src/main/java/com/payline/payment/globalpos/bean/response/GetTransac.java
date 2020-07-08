package com.payline.payment.globalpos.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payline.payment.globalpos.exception.InvalidDataException;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetTransac {
    private String codeErreur;
    @JsonProperty("NumTransac")
    private String numTransac;
    private static XmlMapper xmlMapper = new XmlMapper();

    public String getCodeErreur() {
        return codeErreur;
    }

    public String getNumTransac() {
        return numTransac;
    }

    public static GetTransac fromXml(String xml) {
        try {
            return xmlMapper.readValue(xml, GetTransac.class);
        } catch (IOException e) {
            throw new InvalidDataException("Unable to parse XML GetTransac", e);
        }
    }
}
