package ec.edu.utn.repository;

import ec.edu.utn.model.Prediccion;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class PrediccionRepository {

    @PersistenceContext(unitName = "utngolcoinPU")
    private EntityManager em;

    @Transactional
    public Prediccion save(Prediccion prediccion) {
        if (prediccion.getId() == null) {
            em.persist(prediccion);
            return prediccion;
        } else {
            return em.merge(prediccion);
        }
    }

    public boolean existePrediccion(Integer usuarioId, Integer partidoId) {
        Long count = em.createQuery(
                "SELECT COUNT(p) FROM Prediccion p WHERE p.usuarioId = :usuarioId AND p.partidoId = :partidoId", 
                Long.class)
                .setParameter("usuarioId", usuarioId)
                .setParameter("partidoId", partidoId)
                .getSingleResult();
        return count > 0;
    }

    public List<Prediccion> findPendingByPartidoId(Integer partidoId) {
        return em.createQuery(
                "SELECT p FROM Prediccion p WHERE p.partidoId = :partidoId AND p.estado = :estado", 
                Prediccion.class)
                .setParameter("partidoId", partidoId)
                .setParameter("estado", Prediccion.EstadoPrediccion.PENDIENTE)
                .getResultList();
    }
    
    public List<Prediccion> findByUsuarioId(Integer usuarioId) {
        return em.createQuery(
                "SELECT p FROM Prediccion p WHERE p.usuarioId = :usuarioId ORDER BY p.fechaCreacion DESC", 
                Prediccion.class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }
}
