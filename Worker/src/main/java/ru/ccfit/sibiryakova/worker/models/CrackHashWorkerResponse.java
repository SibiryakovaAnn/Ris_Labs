package ru.ccfit.sibiryakova.worker.models;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlRootElement(name = "CrackHashWorkerResponse", namespace = "http://ccfit.nsu.ru/schema/crack-hash-response")
@XmlType(propOrder = {"requestId", "partNumber", "answers"})
@Data
public class CrackHashWorkerResponse {
    @XmlElement(name = "RequestId")
    private String requestId;

    @XmlElement(name = "PartNumber")
    private int partNumber;

    @XmlElement(name = "Answers")
    private List<String> answers;

}
