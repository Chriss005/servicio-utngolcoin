package ec.edu.utn.resource;

import ec.edu.utn.dto.PrediccionDTO;
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
import java.util.Optional;

@Path("/predicciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PrediccionResource {

    @Inject
    private BilleteraRepository billeteraRepo;
    
    @Inject
    private PrediccionRepository prediccionRepo;
    
    @Inject
    private TransaccionRepository transaccionRepo;

    @POST
    public Response crearPrediccion(PrediccionDTO dto) {
        try {
            // 1. Verificar que el usuario tiene saldo suficiente
            Optional<Billetera> billeteraOpt = billeteraRepo.findByUsuarioId(dto.getUsuarioId());
            if (billeteraOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Billetera no encontrada\"}")
                        .build();
            }
            Billetera billetera = billeteraOpt.get();
            if (billetera.getSaldo().compareTo(dto.getMontoApostado()) < 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\":\"Saldo insuficiente para apostar\"}")
                        .build();
            }

            // 2. Verificar que no hay una predicción existente para este partido
            if (prediccionRepo.existePrediccion(dto.getUsuarioId(), dto.getPartidoId())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"Ya has realizado una predicción para este partido\"}")
                        .build();
            }

            // 3. Crear la predicción
            Prediccion prediccion = new Prediccion();
            prediccion.setUsuarioId(dto.getUsuarioId());
            prediccion.setPartidoId(dto.getPartidoId());
            prediccion.setResultadoPronosticado(dto.getResultadoPronosticado());
            prediccion.setMontoApostado(dto.getMontoApostado());
            prediccion.setCuota(dto.getCuota());
            prediccion.setEstado(Prediccion.EstadoPrediccion.PENDIENTE);
            
            Prediccion guardada = prediccionRepo.save(prediccion);

            // 4. Registrar la transacción de apuesta
            Transaccion transaccion = new Transaccion();
            transaccion.setBilletera(billetera);
            transaccion.setTipo(Transaccion.TipoTransaccion.APUESTA);
            transaccion.setMonto(dto.getMontoApostado());
            transaccion.setDescripcion("Apuesta al partido " + dto.getPartidoId());
            
            transaccionRepo.save(transaccion);

            // 5. Reducir el saldo de la billetera
            billetera.setSaldo(billetera.getSaldo().subtract(dto.getMontoApostado()));
            billeteraRepo.save(billetera);

            // 6. Retornar respuesta
            return Response.status(Response.Status.CREATED).entity(guardada).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error al procesar la predicción\"}")
                    .build();
        }
    }
}
