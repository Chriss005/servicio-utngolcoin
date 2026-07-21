package ec.edu.utn.resource;

import ec.edu.utn.dto.HistorialDTO;
import ec.edu.utn.dto.PrediccionDTO;
import ec.edu.utn.dto.ResultadoPartidoDTO;
import ec.edu.utn.model.Billetera;
import ec.edu.utn.model.Prediccion;
import ec.edu.utn.model.Transaccion;
import ec.edu.utn.repository.BilleteraRepository;
import ec.edu.utn.repository.PrediccionRepository;
import ec.edu.utn.repository.TransaccionRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/predicciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PrediccionResource {

    @Inject private BilleteraRepository billeteraRepo;
    @Inject private PrediccionRepository prediccionRepo;
    @Inject private TransaccionRepository transaccionRepo;

    @POST
    public Response crearPrediccion(PrediccionDTO dto) {
        try {
            Optional<Billetera> billeteraOpt = billeteraRepo.findByUsuarioId(dto.getUsuarioId());
            if (billeteraOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Billetera no encontrada\"}").build();
            }
            Billetera billetera = billeteraOpt.get();
            if (billetera.getSaldo().compareTo(dto.getMontoApostado()) < 0) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Saldo insuficiente para apostar\"}").build();
            }

            if (prediccionRepo.existePrediccion(dto.getUsuarioId(), dto.getPartidoId())) {
                return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"Ya has realizado una predicción para este partido\"}").build();
            }

            Prediccion prediccion = new Prediccion();
            prediccion.setUsuarioId(dto.getUsuarioId());
            prediccion.setPartidoId(dto.getPartidoId());
            prediccion.setResultadoPronosticado(dto.getResultadoPronosticado());
            prediccion.setMontoApostado(dto.getMontoApostado());
            prediccion.setCuota(dto.getCuota());
            prediccion.setEstado(Prediccion.EstadoPrediccion.PENDIENTE);
            
            Prediccion guardada = prediccionRepo.save(prediccion);

            Transaccion transaccion = new Transaccion();
            transaccion.setBilletera(billetera);
            transaccion.setTipo(Transaccion.TipoTransaccion.APUESTA);
            transaccion.setMonto(dto.getMontoApostado());
            transaccion.setDescripcion("Apuesta al partido " + dto.getPartidoId());
            transaccionRepo.save(transaccion);

            billetera.setSaldo(billetera.getSaldo().subtract(dto.getMontoApostado()));
            billeteraRepo.save(billetera);

            return Response.status(Response.Status.CREATED).entity(guardada).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error al procesar la predicción\"}").build();
        }
    }

    @POST
    @Path("/liquidar")
    public Response liquidarPartido(ResultadoPartidoDTO dto) {
        try {
            List<Prediccion> predicciones = prediccionRepo.findPendingByPartidoId(dto.getPartidoId());
            int ganadores = 0, perdedores = 0;

            for (Prediccion p : predicciones) {
                if (p.getResultadoPronosticado().equals(dto.getResultadoOficial())) {
                    p.setEstado(Prediccion.EstadoPrediccion.GANADA);
                    prediccionRepo.save(p);

                    BigDecimal premio = p.getMontoApostado().multiply(p.getCuota());
                    Billetera billetera = billeteraRepo.findByUsuarioId(p.getUsuarioId()).get();
                    billetera.setSaldo(billetera.getSaldo().add(premio));
                    billeteraRepo.save(billetera);

                    Transaccion transaccion = new Transaccion();
                    transaccion.setBilletera(billetera);
                    transaccion.setTipo(Transaccion.TipoTransaccion.PREMIO);
                    transaccion.setMonto(premio);
                    transaccion.setDescripcion("Premio por acertar partido " + dto.getPartidoId());
                    transaccionRepo.save(transaccion);
                    ganadores++;
                } else {
                    p.setEstado(Prediccion.EstadoPrediccion.PERDIDA);
                    prediccionRepo.save(p);
                    perdedores++;
                }
            }
            return Response.ok("{\"mensaje\":\"Partido " + dto.getPartidoId() + " liquidado. Ganadores: " + ganadores + ", Perdedores: " + perdedores + "\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error al liquidar el partido\"}").build();
        }
    }

    // ── RF22: Historial de Predicciones ──
    @GET
    @Path("/usuario/{usuarioId}")
    public Response getHistorial(@PathParam("usuarioId") Integer usuarioId) {
        List<Prediccion> predicciones = prediccionRepo.findByUsuarioId(usuarioId);
        List<HistorialDTO> historial = new ArrayList<>();
        
        for (Prediccion p : predicciones) {
            HistorialDTO dto = new HistorialDTO();
            dto.setPartidoId(p.getPartidoId());
            dto.setResultadoPronosticado(p.getResultadoPronosticado());
            dto.setMontoApostado(p.getMontoApostado());
            dto.setCuota(p.getCuota());
            dto.setEstado(p.getEstado().name());
            dto.setFechaCreacion(p.getFechaCreacion());
            historial.add(dto);
        }
        return Response.ok(historial).build();
    }
}
