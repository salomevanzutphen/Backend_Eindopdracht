package nl.novi.LivingInSync.controller;

import jakarta.validation.Valid;
import nl.novi.LivingInSync.dto.input.CycleInputDto;
import nl.novi.LivingInSync.dto.output.CycleOutputDto;
import nl.novi.LivingInSync.service.CycleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/cycles")
public class CycleController {

    private final CycleService cycleService;

    public CycleController(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    @PostMapping
    public ResponseEntity<Object> createCycle(@Valid @RequestBody CycleInputDto cycleInputDto, BindingResult br, @AuthenticationPrincipal UserDetails userDetails) {
        if (br.hasFieldErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError fe : br.getFieldErrors()) {
                sb.append(fe.getField()).append(": ");
                sb.append(fe.getDefaultMessage()).append("\n");
            }
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        CycleOutputDto cycleOutputDto = cycleService.createCycle(cycleInputDto, userDetails);

        URI uri = URI.create(ServletUriComponentsBuilder
                .fromCurrentRequest().path("/" + cycleOutputDto.getId()).toUriString());

        return ResponseEntity.created(uri).body(cycleOutputDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCycle(@PathVariable Long id, @Valid @RequestBody CycleInputDto cycleInputDto, BindingResult br, @AuthenticationPrincipal UserDetails userDetails) {
        if (br.hasFieldErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError fe : br.getFieldErrors()) {
                sb.append(fe.getField()).append(": ");
                sb.append(fe.getDefaultMessage()).append("\n");
            }
            return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        CycleOutputDto cycleOutputDto = cycleService.updateCycle(id, cycleInputDto);
        return ResponseEntity.ok(cycleOutputDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CycleOutputDto> getCycle(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        CycleOutputDto cycleOutputDto = cycleService.getCycle(id);
        return ResponseEntity.ok(cycleOutputDto);
    }
}
