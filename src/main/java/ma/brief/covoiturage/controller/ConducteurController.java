package ma.brief.covoiturage.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ma.brief.covoiturage.dto.ConducteurRequest;
import ma.brief.covoiturage.dto.ConducteurResponse;
import ma.brief.covoiturage.service.ConducteurService;
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
@RequestMapping("/api/conducteurs")
@RequiredArgsConstructor
public class ConducteurController {

    private final ConducteurService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConducteurResponse create(@Valid @RequestBody ConducteurRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<ConducteurResponse> findAll(@RequestParam(required = false) String nom,
                                            @RequestParam(required = false) String email) {
        if (nom != null || email != null) {
            return service.search(nom, email);
        }
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ConducteurResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ConducteurResponse update(@PathVariable Long id, @Valid @RequestBody ConducteurRequest request) {
        return service.update(id, request);
    }
}
