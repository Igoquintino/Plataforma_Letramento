package com.projeto.tcc.letramento.controller;

import com.projeto.tcc.letramento.dto.ScenarioRequestDTO;
import com.projeto.tcc.letramento.dto.TrailRequestDTO;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.model.Trail;
import com.projeto.tcc.letramento.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/tails")
    public ResponseEntity<Trail> postTrail(@RequestBody @Valid TrailRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminService.createTrail(data));
    }

    @PostMapping("/scenarios")
    public ResponseEntity<Scenario> postScenario(@RequestBody @Valid ScenarioRequestDTO data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminService.createScenario(data));
    }

    @DeleteMapping("/trails/{id}")
    public ResponseEntity<Void> deleteTrail(@PathVariable Long id) {
        adminService.deleteTrail(id);
        return ResponseEntity.noContent().build();
    }


}
