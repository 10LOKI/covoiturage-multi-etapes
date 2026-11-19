package ma.brief.covoiturage.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import ma.brief.covoiturage.domain.entity.Conducteur;
import ma.brief.covoiturage.domain.entity.Vehicule;
import ma.brief.covoiturage.domain.exception.AccesRefuseException;
import ma.brief.covoiturage.domain.exception.RegleMetierException;
import ma.brief.covoiturage.domain.exception.RessourceIntrouvableException;
import ma.brief.covoiturage.dto.VehiculeRequest;
import ma.brief.covoiturage.dto.VehiculeResponse;
import ma.brief.covoiturage.mapper.VehiculeMapper;
import ma.brief.covoiturage.repository.ConducteurRepository;
import ma.brief.covoiturage.repository.VehiculeRepository;
import ma.brief.covoiturage.service.VehiculeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VehiculeServiceImpl implements VehiculeService {

    private static final Logger log = LoggerFactory.getLogger(VehiculeServiceImpl.class);

    private final VehiculeRepository repository;
    private final ConducteurRepository conducteurRepository;
    private final VehiculeMapper mapper;

    @Override
    @Transactional
    public VehiculeResponse create(VehiculeRequest request) {
        repository.findByImmatriculation(request.immatriculation()).ifPresent(v -> {
            throw new RegleMetierException("L'immatriculation est deja utilisee");
        });
        if (request.capacite() < 1 || request.capacite() > 8) {
            throw new RegleMetierException("La capacite doit etre comprise entre 1 et 8");
        }
        Conducteur conducteur = conducteurRepository.findById(request.conducteurId())
                .orElseThrow(() -> new RessourceIntrouvableException(
                        "Conducteur introuvable : " + request.conducteurId()));
        Vehicule vehicule = mapper.toEntity(request);
        vehicule.setConducteur(conducteur);
        Vehicule saved = repository.save(vehicule);
        log.info("Vehicule cree : id={}, immatriculation={}", saved.getId(), saved.getImmatriculation());
        return mapper.toResponse(saved);
    }

    @Override
    public VehiculeResponse getById(Long id) {
        return mapper.toResponse(findEntity(id));
    }

    @Override
    public List<VehiculeResponse> findByConducteur(Long conducteurId) {
        return repository.findByConducteurId(conducteurId).stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public VehiculeResponse update(Long id, VehiculeRequest request) {
        Vehicule vehicule = findEntity(id);
        if (!vehicule.getConducteur().getId().equals(request.conducteurId())) {
            throw new AccesRefuseException("Un vehicule appartient a un seul conducteur");
        }
        repository.findByImmatriculation(request.immatriculation())
                .filter(v -> !v.getId().equals(id))
                .ifPresent(v -> { throw new RegleMetierException("L'immatriculation est deja utilisee"); });
        if (request.capacite() < 1 || request.capacite() > 8) {
            throw new RegleMetierException("La capacite doit etre comprise entre 1 et 8");
        }
        mapper.update(vehicule, request);
        log.info("Vehicule modifie : id={}", id);
        return mapper.toResponse(repository.save(vehicule));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Vehicule vehicule = findEntity(id);
        if (repository.existsTrajets(id)) {
            throw new RegleMetierException("Impossible de supprimer un vehicule associe a un trajet");
        }
        repository.delete(vehicule);
        log.info("Vehicule supprime : id={}", id);
    }

    private Vehicule findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Vehicule introuvable : " + id));
    }
}
