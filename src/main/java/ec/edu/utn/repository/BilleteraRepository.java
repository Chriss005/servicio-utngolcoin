package ec.edu.utn.repository;

import ec.edu.utn.model.Billetera;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
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

    // ── RF21: Consulta de Ranking ──
    public List<Object[]> getRanking() {
        return em.createQuery(
                "SELECT b.usuarioId, b.saldo, COUNT(p.id) " +
                "FROM Billetera b LEFT JOIN Prediccion p ON b.usuarioId = p.usuarioId AND p.estado = 'GANADA' " +
                "GROUP BY b.usuarioId, b.saldo " +
                "ORDER BY b.saldo DESC, COUNT(p.id) DESC", Object[].class)
                .setMaxResults(10)
                .getResultList();
    }
}
