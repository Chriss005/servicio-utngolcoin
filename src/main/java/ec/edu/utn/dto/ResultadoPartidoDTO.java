package ec.edu.utn.dto;

public class ResultadoPartidoDTO {
    private Integer partidoId;
    private String resultadoOficial;

    public ResultadoPartidoDTO() {}

    public Integer getPartidoId() { return partidoId; }
    public void setPartidoId(Integer partidoId) { this.partidoId = partidoId; }
    
    public String getResultadoOficial() { return resultadoOficial; }
    public void setResultadoOficial(String resultadoOficial) { this.resultadoOficial = resultadoOficial; }
}
