package ru.ccfit.sibiryakova.manager.data;

import org.springframework.stereotype.Component;
import ru.ccfit.sibiryakova.manager.models.StatusEnum;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class CrackHashData {
    private final Map<String, StatusEnum> statusMap = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<String>> dataMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> workerCountMap = new ConcurrentHashMap<>();

    public void setStatus(String id, StatusEnum status) {
        statusMap.put(id, status);
    }

    public StatusEnum getStatus(String id) {
        return statusMap.get(id);
    }

    public List<String> getData(String id) {
        return dataMap.computeIfAbsent(id, k -> new CopyOnWriteArrayList<>());
    }

    public int addWorkerCount(String id) {
        return workerCountMap.merge(id, 1, Integer::sum);
    }

    public int getWorkerCount(String id) {
        return workerCountMap.getOrDefault(id, 0);
    }
}























/*package ru.ccfit.sibiryakova.manager.data;

import org.springframework.stereotype.Component;
import ru.ccfit.sibiryakova.manager.models.StatusEnum;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class CrackHashData {
    private final Map<String, StatusEnum> statusMap = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<String>> dataMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> workerCountMap = new ConcurrentHashMap<>();

    // Добавляем новые поля в класс
    private final Map<String, AtomicLong> processedCombinations = new ConcurrentHashMap<>();
    private final Map<String, Long> totalCombinations = new ConcurrentHashMap<>();

    private final Map<String, AtomicLong> progressCounters = new ConcurrentHashMap<>();

    public void initProgressCounter(String id) {
        progressCounters.put(id, new AtomicLong(0));
    }

    public void incrementProgress(String id, long delta) {
        AtomicLong counter = progressCounters.get(id);
        if (counter != null) {
            counter.addAndGet(delta);
            //LOGGER.info("Updated progress for {}: +{} = {}", id, delta, counter.get());
        }
    }

    public long getProgressCount(String id) {
        AtomicLong counter = progressCounters.get(id);
        return counter != null ? counter.get() : 0;
    }

    // Добавляем методы:
    public void setTotalCombinations(String id, long total) {
        totalCombinations.put(id, total);
    }

    public void addProcessedCombinations(String id, long count) {
        processedCombinations.computeIfAbsent(id, k -> new AtomicLong(0)).addAndGet(count);
    }

    public int getProgress(String id) {
        Long total = totalCombinations.get(id);
        AtomicLong processed = processedCombinations.get(id);

        if (total == null || processed == null || total == 0) {
            return 0;
        }

        long progress = (processed.get() * 100) / total;
        return (int) Math.min(progress, 100); // Не больше 100%
    }

    /*public int getProgress(String id) {
        Long total = totalCombinations.get(id);
        AtomicLong processed = processedCombinations.get(id);

        if (total == null || processed == null || total == 0) {
            return 0;
        }

        long progress = (processed.get() * 100) / total;
        return (int) Math.min(progress, 100); // Не больше 100%
    } */

/*
    public void setStatus(String id, StatusEnum status) {
        statusMap.put(id, status);
    }

    public StatusEnum getStatus(String id) {
        return statusMap.get(id);
    }

    public List<String> getData(String id) {
        if (!dataMap.containsKey(id)) {
            dataMap.put(id, new CopyOnWriteArrayList<>());
        }
        return dataMap.get(id);
    }


    public int addWorkerCount(String id) {
        return workerCountMap.merge(id, 1, Integer::sum);  // Автоматически добавляет +1
    }

    public int getWorkerCount(String id) {
        return workerCountMap.getOrDefault(id, 0);
    }
} */