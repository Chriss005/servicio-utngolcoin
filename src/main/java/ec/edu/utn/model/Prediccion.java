package ec.edu.utn.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "predicciones", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "partido_id"}))
public class Prediccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "partido_id", nullable = false)
    private Integer partidoId;

    @Column(name = "resultado_pronosticado", nullable = false, length = 1)
    private String resultadoPronosticado;

    @Column(name = "monto_apostado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoApostado;

    @Column(name = "cuota", nullable = false, precision = 5, scale = 2)
    private BigDecimal cuota;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPrediccion estado = EstadoPrediccion.PENDIENTE;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }

    public enum EstadoPrediccion {
        PENDIENTE, GANADA, PERDIDA
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getPartidoId() { return partidoId; }
    public void setPartidoId(Integer partidoId) { this.partidoId = partidoId; }
    public String getResultadoPronosticado() { return resultadoPronosticado; }
    public void setResultadoPronosticado(String resultadoPronosticado) { this.resultadoPronosticado = resultadoPronosticado; }
    public BigDecimal getMontoApostado() { return montoApostado; }
    public void setMontoApostado(BigDecimal montoApostado) { this.montoApostado = montoApostado; }
    public BigDecimal getCuota() { return cuota; }
    public void setCuota(BigDecimal cuota) { this.cuota = cuota; }
    public EstadoPrediccion getEstado() { return estado; }
    public void setEstado(EstadoPrediccion estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
