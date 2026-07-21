package ec.edu.utn.resource;

import ec.edu.utn.dto.BilleteraDTO;
import ec.edu.utn.dto.RankingDTO;
import ec.edu.utn.dto.RegistroBilleteraDTO;
import ec.edu.utn.model.Billetera;
import ec.edu.utn.model.Transaccion;
import ec.edu.utn.repository.BilleteraRepository;
import ec.edu.utn.repository.BonoDiarioRepository;
import ec.edu.utn.repository.TransaccionRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Path("/billeteras")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BilleteraResource {

    @Inject private BilleteraRepository billeteraRepo;
    @Inject private TransaccionRepository transaccionRepo;
    @Inject private BonoDiarioRepository bonoDiarioRepo;

    @POST @Path("/registrar")
    public Response registrarBilletera(RegistroBilleteraDTO dto) {
        try {
            if (billeteraRepo.findByUsuarioId(dto.getUsuarioId()).isPresent()) {
                return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"La billetera ya existe\"}").build();
            }
            Billetera nueva = new Billetera();
            nueva.setUsuarioId(dto.getUsuarioId());
            nueva.setSaldo(new BigDecimal("10.00"));
            Billetera guardada = billeteraRepo.save(nueva);

            Transaccion bono = new Transaccion();
            bono.setBilletera(guardada);
            bono.setTipo(Transaccion.TipoTransaccion.BONO_BIENVENIDA);
            bono.setMonto(new BigDecimal("10.00"));
            bono.setDescripcion("Bono de bienvenida");
            transaccionRepo.save(bono);

            BilleteraDTO resp = new BilleteraDTO();
            resp.setUsuarioId(guardada.getUsuarioId());
            resp.setSaldo(guardada.getSaldo());
            return Response.status(Response.Status.CREATED).entity(resp).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error al registrar\"}").build();
        }
    }

    @GET @Path("/{usuarioId}")
    public Response obtenerBilletera(@PathParam("usuarioId") Integer usuarioId) {
        return billeteraRepo.findByUsuarioId(usuarioId)
                .map(b -> {
                    BilleteraDTO dto = new BilleteraDTO();
                    dto.setUsuarioId(b.getUsuarioId());
                    dto.setSaldo(b.getSaldo());
                    return Response.ok(dto).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"No encontrada\"}").build());
    }

    @GET @Path("/{usuarioId}/bono-diario")
    public Response reclamarBonoDiario(@PathParam("usuarioId") Integer usuarioId) {
        try {
            var opt = billeteraRepo.findByUsuarioId(usuarioId);
            if (opt.isEmpty()) return Response.status(Response.Status.NOT_FOUND).build();
            
            Billetera billetera = opt.get();
            if (billetera.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Solo si el saldo es 0\"}").build();
            }
            if (bonoDiarioRepo.yaReclamoHoy(usuarioId)) {
                return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"Ya reclamado hoy\"}").build();
            }

            billetera.setSaldo(billetera.getSaldo().add(new BigDecimal("1.00")));
            billeteraRepo.save(billetera);

            Transaccion t = new Transaccion();
            t.setBilletera(billetera);
            t.setTipo(Transaccion.TipoTransaccion.BONO_DIARIO);
            t.setMonto(new BigDecimal("1.00"));
            t.setDescripcion("Bono anti-bancarrota");
            transaccionRepo.save(t);
            bonoDiarioRepo.registrarBono(usuarioId);

            BilleteraDTO resp = new BilleteraDTO();
            resp.setUsuarioId(billetera.getUsuarioId());
            resp.setSaldo(billetera.getSaldo());
            return Response.ok(resp).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error en bono\"}").build();
        }
    }

    // ── RF21: Ranking ──
    @GET
    @Path("/ranking")
    public Response getRanking() {
        List<Object[]> resultados = billeteraRepo.getRanking();
        List<RankingDTO> ranking = new ArrayList<>();
        
        for (Object[] fila : resultados) {
            RankingDTO dto = new RankingDTO();
            dto.setUsuarioId((Integer) fila[0]);
            dto.setSaldo((BigDecimal) fila[1]);
            dto.setAciertos((Long) fila[2]);
            ranking.add(dto);
        }
        return Response.ok(ranking).build();
    }
}
