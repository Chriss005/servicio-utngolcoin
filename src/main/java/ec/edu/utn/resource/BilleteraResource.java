package ec.edu.utn.resource;

import ec.edu.utn.dto.BilleteraDTO;
import ec.edu.utn.dto.RegistroBilleteraDTO;
import ec.edu.utn.model.Billetera;
import ec.edu.utn.model.Transaccion;
import ec.edu.utn.repository.BilleteraRepository;
import ec.edu.utn.repository.TransaccionRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;

@Path("/billeteras")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BilleteraResource {

    @Inject
    private BilleteraRepository billeteraRepo;

    @Inject
    private TransaccionRepository transaccionRepo;

    // ── POST /api/billeteras/registrar ──
    // Registra un usuario y le da el bono de bienvenida de 10 UGC
    @POST
    @Path("/registrar")
    public Response registrarBilletera(RegistroBilleteraDTO dto) {
        try {
            // 1. Verificar si ya existe
            if (billeteraRepo.findByUsuarioId(dto.getUsuarioId()).isPresent()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\":\"La billetera ya existe para este usuario\"}")
                        .build();
            }

            // 2. Crear la entidad Billetera
            Billetera nuevaBilletera = new Billetera();
            nuevaBilletera.setUsuarioId(dto.getUsuarioId());
            nuevaBilletera.setSaldo(new BigDecimal("10.00")); // Bono de bienvenida
            
            Billetera guardada = billeteraRepo.save(nuevaBilletera);

            // 3. Registrar la transacción en el ledger (inmutable)
            Transaccion bono = new Transaccion();
            bono.setBilletera(guardada);
            bono.setTipo(Transaccion.TipoTransaccion.BONO_BIENVENIDA);
            bono.setMonto(new BigDecimal("10.00"));
            bono.setDescripcion("Bono de bienvenida por registro inicial");
            
            transaccionRepo.save(bono);

            // 4. Retornar respuesta exitosa con DTO
            BilleteraDTO respuesta = new BilleteraDTO();
            respuesta.setUsuarioId(guardada.getUsuarioId());
            respuesta.setSaldo(guardada.getSaldo());

            return Response.status(Response.Status.CREATED).entity(respuesta).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\":\"Error al registrar la billetera\"}")
                    .build();
        }
    }

    // ── GET /api/billeteras/{usuarioId} ──
    @GET
    @Path("/{usuarioId}")
    public Response obtenerBilletera(@PathParam("usuarioId") Integer usuarioId) {
        return billeteraRepo.findByUsuarioId(usuarioId)
                .map(b -> {
                    BilleteraDTO dto = new BilleteraDTO();
                    dto.setUsuarioId(b.getUsuarioId());
                    dto.setSaldo(b.getSaldo());
                    return Response.ok(dto).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Billetera no encontrada\"}")
                        .build());
    }
}
