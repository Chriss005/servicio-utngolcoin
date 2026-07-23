package ec.edu.utn.dto;

import java.math.BigDecimal;

public class BilleteraDTO {
    private Integer usuarioId;
    private BigDecimal saldo;

    public BilleteraDTO() {}

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

}
