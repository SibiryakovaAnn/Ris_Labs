package ru.ccfit.sibiryakova.worker.models;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "CrackHashManagerRequest", namespace = "http://ccfit.nsu.ru/schema/crack-hash-request")
@XmlType(propOrder = {"requestId", "partNumber", "partCount", "hash", "maxLength", "alphabet"})
@Data
public class CrackHashManagerRequest {
    @XmlElement(name = "RequestId")
    private String requestId;

    @XmlElement(name = "PartNumber")

    private int partNumber;

    @XmlElement(name = "PartCount")
    private int partCount;

    @XmlElement(name = "Hash")
    private String hash;

    @XmlElement(name = "MaxLength")
    private int maxLength;

    @XmlElement(name = "Alphabet")
    private List<String> alphabet;

}

