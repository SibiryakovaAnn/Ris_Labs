package ru.ccfit.sibiryakova.worker.services;

import org.paukov.combinatorics3.Generator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.ccfit.sibiryakova.worker.models.CrackHashManagerRequest;
import ru.ccfit.sibiryakova.worker.models.CrackHashWorkerResponse;

import java.math.BigInteger;
import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CrackHashService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrackHashService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    public void managerTask(CrackHashManagerRequest request) {
        if (request.getPartNumber() < 0 || request.getPartNumber() > 2) {
            LOGGER.warn("Invalid partNumber: {}. Must be 0, 1, or 2", request.getPartNumber());
            return;
        }

        List<String> result = Collections.synchronizedList(new ArrayList<>());

        try {
            List<String> alphabet = request.getAlphabet();
            String hash = request.getHash();
            int maxLength = request.getMaxLength();

            if (alphabet != null && !alphabet.isEmpty() && maxLength > 0) {

                generateCombinations(alphabet, maxLength, "", hash, result, request.getRequestId());
            }
        } catch (Exception e) {
            LOGGER.error("Error processing task: {}", e.getMessage());
        } finally {
            sendResponseToManager(
                    request.getRequestId(),
                    request.getPartNumber(),
                    result
            );
        }
    }


    private void generateCombinations(List<String> alphabet, int maxLength,
                                      String current, String targetHash,
                                      List<String> results, String requestId) {
        if (current.length() == maxLength) {
            if (checkHash(current, targetHash)) {
                results.add(current);
            }
            return;
        }

        for (String s : alphabet) {
            generateCombinations(alphabet, maxLength, current + s, targetHash, results, requestId);

            // Отправляем обновление прогресса каждые 100 комбинаций
            if (current.length() == 0 && results.size() % 100 == 0) {
                updateProgress(requestId, 100);
            }
        }
    }

    private void updateProgress(String requestId, int count) {
        try {
            String url = "http://manager:8080/internal/api/manager/progress/update";
            restTemplate.postForObject(url,
                    Map.of("requestId", requestId, "count", count),
                    Void.class);
        } catch (Exception e) {
            LOGGER.error("Failed to update progress: {}", e.getMessage());
        }
    }

    private boolean checkHash(String word, String targetHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(word.getBytes());
            BigInteger no = new BigInteger(1, digest);
            String hashText = String.format("%032x", no);
            return targetHash.equals(hashText);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    private void sendResponseToManager(String requestId, int partNumber, List<String> answers) {
        try {
            CrackHashWorkerResponse response = new CrackHashWorkerResponse();
            response.setRequestId(requestId);
            response.setPartNumber(partNumber);
            response.setAnswers(answers != null ? answers : Collections.emptyList());

            String url = "http://manager:8080/internal/api/manager/hash/crack/request";

            restTemplate.postForObject(url, response, Void.class);

            LOGGER.info("Worker-{} sent response for task {} ({} matches)",
                    partNumber, requestId, answers.size());
        } catch (Exception e) {
            LOGGER.error("Failed to send response to manager: {}", e.getMessage());
        }
    }
}
