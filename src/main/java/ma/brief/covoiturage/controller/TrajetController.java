package ma.brief.covoiturage.controller;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ma.brief.covoiturage.dto.ReservationResponse;
import ma.brief.covoiturage.dto.TrajetRequest;
import ma.brief.covoiturage.dto.TrajetResponse;
import ma.brief.covoiturage.dto.TrajetSearchItem;
import ma.brief.covoiturage.service.TrajetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trajets")
@RequiredArgsConstructor
public class TrajetController {

    private final TrajetService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrajetResponse publish(@Valid @RequestBody TrajetRequest request) {
        return service.publish(request);
    }

    @GetMapping("/{id}")
    public TrajetResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/recherche")
    public Page<TrajetSearchItem> search(@RequestParam String villeDepart,
                                         @RequestParam String villeArrivee,
                                         @RequestParam(required = false) LocalDate date,
                                         @RequestParam(required = false) BigDecimal prixMax,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateDepart").ascending());
        return service.search(villeDepart, villeArrivee, date, prixMax, pageable);
    }

    @GetMapping("/{id}/reservations")
    public List<ReservationResponse> reservations(@PathVariable Long id,
                                                  @RequestParam Long conducteurId) {
        return service.reservationsDuTrajet(id, conducteurId);
    }

    @PostMapping("/{id}/terminer")
    public TrajetResponse terminer(@PathVariable Long id, @RequestParam Long conducteurId) {
        return service.terminer(id, conducteurId);
    }

    @PostMapping("/{id}/annuler")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void annuler(@PathVariable Long id, @RequestParam Long conducteurId) {
        service.annuler(id, conducteurId);
    }
}
