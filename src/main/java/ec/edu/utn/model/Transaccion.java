package ec.edu.utn.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billetera_id", nullable = false)
    private Billetera billetera;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoTransaccion tipo;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha", updatable = false)
    private LocalDateTime fecha;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
    }

    public enum TipoTransaccion {
        BONO_BIENVENIDA, APUESTA, PREMIO, BONO_DIARIO
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Billetera getBilletera() { return billetera; }
    public void setBilletera(Billetera billetera) { this.billetera = billetera; }
    public TipoTransaccion getTipo() { return tipo; }
    public void setTipo(TipoTransaccion tipo) { this.tipo = tipo; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
