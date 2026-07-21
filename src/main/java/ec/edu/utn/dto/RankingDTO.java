package ec.edu.utn.dto;

import java.math.BigDecimal;

public class RankingDTO {
    private Integer usuarioId;
    private BigDecimal saldo;
    private Long aciertos;

    public RankingDTO() {}

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public Long getAciertos() { return aciertos; }
    public void setAciertos(Long aciertos) { this.aciertos = aciertos; }
}
