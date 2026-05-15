package com.projeto.tcc.letramento.controller;

import com.projeto.tcc.letramento.model.Progress;
import com.projeto.tcc.letramento.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class progressController {

    private final ProgressService progressService;

    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<List<Progress>> getDashboard(@PathVariable Long userId) {
        return ResponseEntity.ok(progressService.getStudentDashboard(userId));
    }
}
