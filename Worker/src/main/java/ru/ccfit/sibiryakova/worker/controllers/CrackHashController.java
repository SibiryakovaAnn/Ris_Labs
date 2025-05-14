package ru.ccfit.sibiryakova.worker.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.ccfit.sibiryakova.worker.models.CrackHashManagerRequest;
import ru.ccfit.sibiryakova.worker.services.CrackHashService;

@RestController
public class CrackHashController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrackHashController.class);

    private final CrackHashService crackHashService;

    @Autowired
    public CrackHashController(CrackHashService crackHashService) {
        this.crackHashService = crackHashService;
    }

    @PostMapping("/internal/api/worker/hash/crack/task")
    public ResponseEntity<Void> managerTask(@RequestBody CrackHashManagerRequest request) {
        LOGGER.info("Manager request");
        crackHashService.managerTask(request);
        return ResponseEntity.ok().build();
    }
}
