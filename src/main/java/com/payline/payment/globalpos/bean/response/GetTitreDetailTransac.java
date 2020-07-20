package com.payline.payment.globalpos.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payline.payment.globalpos.exception.InvalidDataException;
import lombok.Getter;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetTitreDetailTransac {
    @Getter
    private String codeErreur;
    @Getter
    private String titre;
    @Getter
    private String emetteur;
    @Getter
    private String montant;
    @Getter
    private String dateValid;
    @Getter
    private String numTitre;
    @Getter
    @JsonProperty("ID")
    private String id;
    private static XmlMapper xmlMapper = new XmlMapper();


    public static GetTitreDetailTransac fromXml(String xml) {
        try {
            return xmlMapper.readValue(xml, GetTitreDetailTransac.class);
        } catch (IOException e) {
            throw new InvalidDataException("Unable to parse XML GetTitreDetailTransac", e);
        }
    }
}
