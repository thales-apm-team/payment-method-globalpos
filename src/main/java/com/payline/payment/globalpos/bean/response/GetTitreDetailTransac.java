package com.payline.payment.globalpos.bean.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.payline.payment.globalpos.exception.InvalidDataException;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetTitreDetailTransac {
    private String codeErreur;
    private String titre;
    private String emetteur;
    private String montant;
    private String dateValid;
    private String numTitre;
    @JsonProperty("ID")
    private String id;
    private static XmlMapper xmlMapper = new XmlMapper();

    public String getCodeErreur() {
        return codeErreur;
    }

    public String getTitre() {
        return titre;
    }

    public String getEmetteur() {
        return emetteur;
    }

    public String getMontant() {
        return montant;
    }

    public String getDateValid() {
        return dateValid;
    }

    public String getNumTitre() {
        return numTitre;
    }

    public String getId() {
        return id;
    }

    public static GetTitreDetailTransac fromXml(String xml) {
        try {
            return xmlMapper.readValue(xml, GetTitreDetailTransac.class);
        } catch (IOException e) {
            throw new InvalidDataException("Unable to parse XML GetTitreDetailTransac", e);
        }
    }
}
