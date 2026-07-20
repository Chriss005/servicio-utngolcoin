package ec.edu.utn.dto;

import java.math.BigDecimal;

public class PrediccionDTO {
    private Integer usuarioId;
    private Integer partidoId;
    private String resultadoPronosticado; // "1", "X", o "2"
    private BigDecimal montoApostado;
    private BigDecimal cuota;

    public PrediccionDTO() {}

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
}
