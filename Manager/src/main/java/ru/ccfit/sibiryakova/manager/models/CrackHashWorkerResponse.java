package ru.ccfit.sibiryakova.manager.models;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "CrackHashWorkerResponse", namespace = "http://ccfit.nsu.ru/schema/crack-hash-response")
@XmlType(propOrder = {"requestId", "partNumber", "answers"})
public class CrackHashWorkerResponse {
    @XmlElement(name = "RequestId")
    private String requestId;

    @XmlElement(name = "PartNumber")
    private int partNumber;

    @XmlElement(name = "Answers")
    private List<String> answers;

    public String getRequestId() {
        return this.requestId;
    }

    public int getPartNumber() {
        return this.partNumber;
    }

    public List<String> getAnswers() {
        return answers != null ? answers : Collections.emptyList();
    }
}
