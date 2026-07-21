package ec.edu.utn.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HistorialDTO {
    private Integer partidoId;
    private String resultadoPronosticado;
    private BigDecimal montoApostado;
    private BigDecimal cuota;
    private String estado;
    private LocalDateTime fechaCreacion;

    public HistorialDTO() {}

    public Integer getPartidoId() { return partidoId; }
    public void setPartidoId(Integer partidoId) { this.partidoId = partidoId; }
    public String getResultadoPronosticado() { return resultadoPronosticado; }
    public void setResultadoPronosticado(String resultadoPronosticado) { this.resultadoPronosticado = resultadoPronosticado; }
    public BigDecimal getMontoApostado() { return montoApostado; }
    public void setMontoApostado(BigDecimal montoApostado) { this.montoApostado = montoApostado; }
    public BigDecimal getCuota() { return cuota; }
    public void setCuota(BigDecimal cuota) { this.cuota = cuota; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
