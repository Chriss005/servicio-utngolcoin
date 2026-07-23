package ec.edu.utn.repository;

import ec.edu.utn.model.MapeoUsuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class MapeoUsuarioRepository {
    @PersistenceContext(unitName = "utngolcoinPU")
    private EntityManager em;
    
    public Optional<MapeoUsuario> findByUsername(String username) {
        return em.createQuery("SELECT m FROM MapeoUsuario m WHERE m.username = :username", MapeoUsuario.class)
                 .setParameter("username", username).getResultStream().findFirst();
    }
    
    @Transactional
    public MapeoUsuario save(MapeoUsuario mapeo) {
        if (mapeo.getId() == null) { em.persist(mapeo); return mapeo; }
        return em.merge(mapeo);
    }
    
    public boolean existsByUsername(String username) {
        Long count = em.createQuery("SELECT COUNT(m) FROM MapeoUsuario m WHERE m.username = :username", Long.class)
                .setParameter("username", username).getSingleResult();
        return count > 0;
    }
}
