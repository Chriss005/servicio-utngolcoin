package ec.edu.utn.repository;

import ec.edu.utn.model.Transaccion;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class TransaccionRepository {

    @PersistenceContext(unitName = "utngolcoinPU")
    private EntityManager em;

    @Transactional
    public void save(Transaccion transaccion) {
        em.persist(transaccion);
    }

    public List<Transaccion> findByBilleteraId(Integer billeteraId) {
        return em.createQuery("SELECT t FROM Transaccion t WHERE t.billetera.id = :billeteraId ORDER BY t.fecha DESC", Transaccion.class)
                 .setParameter("billeteraId", billeteraId)
                 .getResultList();
    }
}
