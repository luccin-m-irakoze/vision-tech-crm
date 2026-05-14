package com.visiontechcrm.controller;

import com.visiontechcrm.dto.InteractionDto;
import com.visiontechcrm.service.InteractionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interactions")
public class InteractionController {

    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @GetMapping
    public ResponseEntity<List<InteractionDto>> list(
            @RequestParam(value = "customerId", required = false) Long customerId) {
        if (customerId != null) {
            return ResponseEntity.ok(interactionService.findByCustomer(customerId));
        }
        return ResponseEntity.ok(interactionService.findAll());
    }

    @PostMapping
    public ResponseEntity<InteractionDto> create(@Valid @RequestBody InteractionDto dto) {
        InteractionDto created = interactionService.create(dto);
        return ResponseEntity.created(URI.create("/api/interactions/" + created.getId()))
                .body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        interactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
