package ec.edu.utn.repository;

import ec.edu.utn.model.Billetera;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class BilleteraRepository {

    @PersistenceContext(unitName = "utngolcoinPU")
    private EntityManager em;

    public Optional<Billetera> findByUsuarioId(Integer usuarioId) {
        return em.createQuery("SELECT b FROM Billetera b WHERE b.usuarioId = :usuarioId", Billetera.class)
                 .setParameter("usuarioId", usuarioId)
                 .getResultStream()
                 .findFirst();
    }

    @Transactional
    public Billetera save(Billetera billetera) {
        if (billetera.getId() == null) {
            em.persist(billetera);
            return billetera;
        } else {
            return em.merge(billetera);
        }
    }
}
