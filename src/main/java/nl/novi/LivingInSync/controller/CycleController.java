package nl.novi.LivingInSync.controller;

import jakarta.validation.Valid;
import nl.novi.LivingInSync.dto.input.CycleInputDto;
import nl.novi.LivingInSync.dto.output.CycleOutputDto;
import nl.novi.LivingInSync.service.CycleService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<Object> createOrUpdateCycle(
            @Valid @RequestBody CycleInputDto cycleInputDto,
            BindingResult br,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (br.hasFieldErrors()) {
            return handleValidationErrors(br);
        }

        CycleOutputDto cycleOutputDto = cycleService.createOrUpdateCycle(cycleInputDto, userDetails);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        return ResponseEntity.created(uri).body(cycleOutputDto);
    }

    @PutMapping
    public ResponseEntity<Object> updateCycle(
            @Valid @RequestBody CycleInputDto cycleInputDto,
            BindingResult br,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (br.hasFieldErrors()) {
            return handleValidationErrors(br);
        }

        CycleOutputDto cycleOutputDto = cycleService.updateCycleForUser(cycleInputDto, userDetails);
        return ResponseEntity.ok(cycleOutputDto);
    }

    @GetMapping
    public ResponseEntity<CycleOutputDto> getCycle(@AuthenticationPrincipal UserDetails userDetails) {
        CycleOutputDto cycleOutputDto = cycleService.getUserCycle(userDetails);
        return ResponseEntity.ok(cycleOutputDto);
    }

    private ResponseEntity<Object> handleValidationErrors(BindingResult br) {
        StringBuilder sb = new StringBuilder();
        br.getFieldErrors().forEach(fe -> sb.append(fe.getField()).append(": ").append(fe.getDefaultMessage()).append("\n"));
        return new ResponseEntity<>(sb.toString(), HttpStatus.BAD_REQUEST);
    }
}
