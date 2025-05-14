package ru.ccfit.sibiryakova.manager.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "CrackHashManagerRequest", namespace = "http://ccfit.nsu.ru/schema/crack-hash-request")
@XmlType(propOrder = {"requestId", "partNumber", "partCount", "hash", "maxLength", "alphabet"})
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

    public CrackHashManagerRequest() {
    }

    public String getRequestId() {
        return this.requestId;
    }

    public int getPartNumber() {
        return this.partNumber;
    }

    public int getPartCount() {
        return this.partCount;
    }

    public String getHash() {
        return this.hash;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public List<String> getAlphabet() {
        return this.alphabet;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public void setPartCount(int partCount) {
        this.partCount = partCount;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public void setAlphabet(List<String> alphabet) {
        this.alphabet = alphabet;
    }
}
