package ru.ccfit.sibiryakova.manager.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ccfit.sibiryakova.manager.models.*;
import ru.ccfit.sibiryakova.manager.services.CrackHashService;
import ru.ccfit.sibiryakova.manager.data.CrackHashData;

import java.util.Map;

@RestController
public class CrackHashController {
    private final CrackHashService crackHashService;
    private final CrackHashData crackHashData;

    @Autowired
    public CrackHashController(CrackHashService crackHashService, CrackHashData crackHashData) {
        this.crackHashService = crackHashService;
        this.crackHashData = crackHashData;
    }

    @PostMapping("/api/hash/crack")
    public ResponseEntity<RequestId> crackHash(@RequestBody Hash hash) {
        return ResponseEntity.ok(crackHashService.crackHash(hash));
    }

    @GetMapping("/api/hash/status")
    public ResponseEntity<Status> getStatus(@RequestParam("requestId")String requestId) {
        return ResponseEntity.ok(crackHashService.getStatus(requestId));
    }

    @PostMapping("/internal/api/manager/hash/crack/request")
    public ResponseEntity<Void> workerResponse(@RequestBody CrackHashWorkerResponse response) {
        crackHashService.workerResponse(response);
        return ResponseEntity.ok().build();
    }

}
