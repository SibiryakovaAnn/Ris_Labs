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
