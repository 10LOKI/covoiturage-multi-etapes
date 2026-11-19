package ma.brief.covoiturage.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import ma.brief.covoiturage.domain.entity.Passager;
import ma.brief.covoiturage.domain.exception.RegleMetierException;
import ma.brief.covoiturage.domain.exception.RessourceIntrouvableException;
import ma.brief.covoiturage.dto.PassagerRequest;
import ma.brief.covoiturage.dto.PassagerResponse;
import ma.brief.covoiturage.dto.ReservationResponse;
import ma.brief.covoiturage.mapper.PassagerMapper;
import ma.brief.covoiturage.mapper.ReservationMapper;
import ma.brief.covoiturage.repository.PassagerRepository;
import ma.brief.covoiturage.repository.ReservationRepository;
import ma.brief.covoiturage.service.PassagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PassagerServiceImpl implements PassagerService {

    private static final Logger log = LoggerFactory.getLogger(PassagerServiceImpl.class);

    private final PassagerRepository repository;
    private final ReservationRepository reservationRepository;
    private final PassagerMapper mapper;
    private final ReservationMapper reservationMapper;

    @Override
    @Transactional
    public PassagerResponse create(PassagerRequest request) {
        repository.findByEmail(request.email()).ifPresent(p -> {
            throw new RegleMetierException("L'email " + request.email() + " est deja utilise");
        });
        Passager passager = repository.save(mapper.toEntity(request));
        log.info("Passager cree : id={}, email={}", passager.getId(), passager.getEmail());
        return mapper.toResponse(passager);
    }

    @Override
    public PassagerResponse getById(Long id) {
        return mapper.toResponse(findEntity(id));
    }

    @Override
    public List<PassagerResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Override
    public List<PassagerResponse> search(String email) {
        String e = email == null ? "" : email;
        return repository.findByNomContainingIgnoreCaseOrEmailContainingIgnoreCase("", e)
                .stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    public PassagerResponse update(Long id, PassagerRequest request) {
        Passager passager = findEntity(id);
        repository.findByEmail(request.email())
                .filter(p -> !p.getId().equals(id))
                .ifPresent(p -> { throw new RegleMetierException("L'email est deja utilise"); });
        mapper.update(passager, request);
        log.info("Passager modifie : id={}", id);
        return mapper.toResponse(repository.save(passager));
    }

    @Override
    public List<ReservationResponse> historique(Long passagerId) {
        findEntity(passagerId);
        return reservationRepository.findByPassagerId(passagerId).stream()
                .map(reservationMapper::toResponse).toList();
    }

    private Passager findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Passager introuvable : " + id));
    }
}
