package ru.ccfit.sibiryakova.manager.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.ccfit.sibiryakova.manager.data.CrackHashData;
import ru.ccfit.sibiryakova.manager.models.*;
import ru.ccfit.sibiryakova.manager.repository.HashRepository;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class CrackHashService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrackHashService.class);

    @Value("${worker.count}")
    private int workerCount;

    private final CrackHashData crackHashData;
    private final HashRepository hashRepository;
    private final List<String> alphabet = new ArrayList<>();
    private static final long TIMEOUT_DURATION = 600;

    public CrackHashService(CrackHashData crackHashData, HashRepository hashRepository) {
        this.crackHashData = crackHashData;
        this.hashRepository = hashRepository;
        generateAlphabet();
        restoreRequests();
    }

    private void restoreRequests() {
        try {
            List<Hash> hashes = hashRepository.findAll();
            for (Hash hash : hashes) {
                String id = UUID.randomUUID().toString();
                crackHashData.setStatus(id, StatusEnum.IN_PROGRESS);
                CompletableFuture.runAsync(() -> sendWorkerRequests(id, hash));
                LOGGER.info("Restored request for hash: {}", hash.hash());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to restore requests", e);
        }
    }

    public RequestId crackHash(Hash hash) {
        String id = UUID.randomUUID().toString();
        crackHashData.setStatus(id, StatusEnum.IN_PROGRESS);
        hashRepository.save(id, hash.hash(), hash.maxLength());

        CompletableFuture.runAsync(() -> sendWorkerRequests(id, hash));

        return new RequestId(id);
    }

    public Status getStatus(String requestId) {
        StatusEnum st = crackHashData.getStatus(requestId);
        List<String> data = crackHashData.getData(requestId);
        int responses = crackHashData.getWorkerCount(requestId);
        return new Status(st, data, responses, workerCount);
    }

    private void sendWorkerRequests(String id, Hash hash) {
        RestTemplate restTemplate = new RestTemplate();
        int partCount = workerCount;
        int chunkSize = (int) Math.ceil((double) alphabet.size() / partCount);

        for (int partNumber = 0; partNumber < partCount; partNumber++) {
            int start = partNumber * chunkSize;
            int end = Math.min(start + chunkSize, alphabet.size());
            List<String> workerAlphabet = alphabet.subList(start, end);

            CrackHashManagerRequest request = new CrackHashManagerRequest();
            request.setRequestId(id);
            request.setHash(hash.hash());
            request.setMaxLength(hash.maxLength());
            request.setPartCount(partCount);
            request.setPartNumber(partNumber);
            request.setAlphabet(workerAlphabet);

            String workerUrl = "http://worker-" + partNumber + ":8000/internal/api/worker/hash/crack/task";
            restTemplate.postForObject(workerUrl, request, Void.class);
        }

        CompletableFuture.delayedExecutor(TIMEOUT_DURATION, TimeUnit.SECONDS)
                .execute(() -> {
                    if (crackHashData.getStatus(id) == StatusEnum.IN_PROGRESS) {
                        crackHashData.setStatus(id, StatusEnum.ERROR);
                    }
                });
    }

    public void workerResponse(CrackHashWorkerResponse response) {
        String id = response.getRequestId();
        List<String> answers = Optional.ofNullable(response.getAnswers()).orElse(Collections.emptyList());

        int count = crackHashData.addWorkerCount(id);
        crackHashData.getData(id).addAll(answers);

        LOGGER.info("Received {} answers from worker {} for task {} ({} of {})",
                answers.size(), response.getPartNumber(), id, count, workerCount);

        if (count >= workerCount) {
            crackHashData.setStatus(id, StatusEnum.READY);
            hashRepository.remove(id);
            LOGGER.info("Task {} marked READY: all workers responded", id);
        }
    }

    private void generateAlphabet() {
        for (char c = 'a'; c <= 'z'; c++) alphabet.add(String.valueOf(c));
        for (int i = 0; i <= 9; i++) alphabet.add(String.valueOf(i));
    }
}