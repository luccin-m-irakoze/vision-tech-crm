package com.visiontechcrm.controller;

import com.visiontechcrm.dto.TicketDto;
import com.visiontechcrm.service.TicketService;
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
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public ResponseEntity<List<TicketDto>> list() {
        return ResponseEntity.ok(ticketService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TicketDto> create(@Valid @RequestBody TicketDto dto) {
        TicketDto created = ticketService.create(dto);
        return ResponseEntity.created(URI.create("/api/tickets/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TicketDto> update(
            @PathVariable Long id, @Valid @RequestBody TicketDto dto) {
        return ResponseEntity.ok(ticketService.update(id, dto));
    }

    @PutMapping("/{id}/escalate")
    public ResponseEntity<TicketDto> escalate(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.escalate(id));
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<TicketDto> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.resolve(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
