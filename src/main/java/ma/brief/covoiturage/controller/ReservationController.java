package ma.brief.covoiturage.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ma.brief.covoiturage.dto.ReservationRequest;
import ma.brief.covoiturage.dto.ReservationResponse;
import ma.brief.covoiturage.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse reserver(@Valid @RequestBody ReservationRequest request) {
        return service.reserver(request);
    }

    @GetMapping
    public List<ReservationResponse> byPassager(@RequestParam Long passagerId) {
        return service.findByPassager(passagerId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void annuler(@PathVariable Long id, @RequestParam Long passagerId) {
        service.annuler(id, passagerId);
    }
}
