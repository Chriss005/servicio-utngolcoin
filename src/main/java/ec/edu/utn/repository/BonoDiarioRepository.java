package ec.edu.utn.repository;

import ec.edu.utn.model.BonoDiario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.time.LocalDate;

@ApplicationScoped
public class BonoDiarioRepository {

    @PersistenceContext(unitName = "utngolcoinPU")
    private EntityManager em;

    public boolean yaReclamoHoy(Integer usuarioId) {
        Long count = em.createQuery(
                "SELECT COUNT(b) FROM BonoDiario b WHERE b.usuarioId = :uid AND b.fechaEntrega = :hoy", Long.class)
                .setParameter("uid", usuarioId)
                .setParameter("hoy", LocalDate.now())
                .getSingleResult();
        return count > 0;
    }

    @Transactional
    public void registrarBono(Integer usuarioId) {
        BonoDiario bono = new BonoDiario();
        bono.setUsuarioId(usuarioId);
        bono.setFechaEntrega(LocalDate.now());
        em.persist(bono);
    }
}
