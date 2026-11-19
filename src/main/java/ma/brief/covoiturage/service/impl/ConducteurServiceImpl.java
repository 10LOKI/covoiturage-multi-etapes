package ma.brief.covoiturage.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import ma.brief.covoiturage.domain.entity.Conducteur;
import ma.brief.covoiturage.domain.exception.RegleMetierException;
import ma.brief.covoiturage.domain.exception.RessourceIntrouvableException;
import ma.brief.covoiturage.dto.ConducteurRequest;
import ma.brief.covoiturage.dto.ConducteurResponse;
import ma.brief.covoiturage.mapper.ConducteurMapper;
import ma.brief.covoiturage.repository.ConducteurRepository;
import ma.brief.covoiturage.service.ConducteurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConducteurServiceImpl implements ConducteurService {

    private static final Logger log = LoggerFactory.getLogger(ConducteurServiceImpl.class);

    private final ConducteurRepository repository;
    private final ConducteurMapper mapper;

    @Override
    @Transactional
    public ConducteurResponse create(ConducteurRequest request) {
        repository.findByEmail(request.email()).ifPresent(c -> {
            throw new RegleMetierException("L'email " + request.email() + " est deja utilise");
        });
        repository.findByNumeroPermis(request.numeroPermis()).ifPresent(c -> {
            throw new RegleMetierException("Le numero de permis est deja utilise");
        });
        Conducteur conducteur = repository.save(mapper.toEntity(request));
        log.info("Conducteur cree : id={}, email={}", conducteur.getId(), conducteur.getEmail());
        return mapper.toResponse(conducteur);
    }

    @Override
    public ConducteurResponse getById(Long id) {
        return mapper.toResponse(findEntity(id));
    }

    @Override
    public List<ConducteurResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<ConducteurResponse> search(String nom, String email) {
        String n = nom == null ? "" : nom;
        String e = email == null ? "" : email;
        return repository.findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase(n, e)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public ConducteurResponse update(Long id, ConducteurRequest request) {
        Conducteur conducteur = findEntity(id);
        repository.findByEmail(request.email())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> { throw new RegleMetierException("L'email est deja utilise"); });
        repository.findByNumeroPermis(request.numeroPermis())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> { throw new RegleMetierException("Le numero de permis est deja utilise"); });
        mapper.update(conducteur, request);
        log.info("Conducteur modifie : id={}", id);
        return mapper.toResponse(repository.save(conducteur));
    }

    private Conducteur findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Conducteur introuvable : " + id));
    }
}
