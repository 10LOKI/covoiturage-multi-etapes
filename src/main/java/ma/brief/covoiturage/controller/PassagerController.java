package ma.brief.covoiturage.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ma.brief.covoiturage.dto.PassagerRequest;
import ma.brief.covoiturage.dto.PassagerResponse;
import ma.brief.covoiturage.dto.ReservationResponse;
import ma.brief.covoiturage.service.PassagerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/passagers")
@RequiredArgsConstructor
public class PassagerController {

    private final PassagerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassagerResponse create(@Valid @RequestBody PassagerRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<PassagerResponse> findAll(@RequestParam(required = false) String email) {
        if (email != null) {
            return service.search(email);
        }
        return service.findAll();
    }

    @GetMapping("/{id}")
    public PassagerResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public PassagerResponse update(@PathVariable Long id, @Valid @RequestBody PassagerRequest request) {
        return service.update(id, request);
    }

    @GetMapping("/{id}/reservations")
    public List<ReservationResponse> historique(@PathVariable Long id) {
        return service.historique(id);
    }
}
