package ec.edu.utn.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bonos_diarios", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "fecha_entrega"}))
public class BonoDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "fecha_entrega", nullable = false)
    private LocalDate fechaEntrega;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    
    public LocalDate getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDate fechaEntrega) { this.fechaEntrega = fechaEntrega; }
}
