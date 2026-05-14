package com.visiontechcrm.controller;

import com.visiontechcrm.dto.LeadDto;
import com.visiontechcrm.dto.StageUpdateRequest;
import com.visiontechcrm.service.LeadService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public ResponseEntity<List<LeadDto>> list() {
        return ResponseEntity.ok(leadService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(leadService.findById(id));
    }

    @PostMapping
    public ResponseEntity<LeadDto> create(@Valid @RequestBody LeadDto dto) {
        LeadDto created = leadService.create(dto);
        return ResponseEntity.created(URI.create("/api/leads/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LeadDto> update(@PathVariable Long id, @Valid @RequestBody LeadDto dto) {
        return ResponseEntity.ok(leadService.update(id, dto));
    }

    @PutMapping("/{id}/stage")
    public ResponseEntity<LeadDto> updateStage(
            @PathVariable Long id, @Valid @RequestBody StageUpdateRequest req) {
        return ResponseEntity.ok(leadService.updateStage(id, req.getStage()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        leadService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
