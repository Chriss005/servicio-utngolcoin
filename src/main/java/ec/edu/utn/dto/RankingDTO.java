package ec.edu.utn.dto;

import java.math.BigDecimal;

public class RankingDTO {
    private Integer posicion;
    private String usuario;
    private BigDecimal saldo;
    private Integer aciertos;

    public Integer getPosicion() { return posicion; }
    public void setPosicion(Integer posicion) { this.posicion = posicion; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }
    public Integer getAciertos() { return aciertos; }
    public void setAciertos(Integer aciertos) { this.aciertos = aciertos; }
}
