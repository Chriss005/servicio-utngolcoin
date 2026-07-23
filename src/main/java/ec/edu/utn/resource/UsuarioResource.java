package ec.edu.utn.resource;

import ec.edu.utn.model.Billetera;
import ec.edu.utn.model.MapeoUsuario;
import ec.edu.utn.model.Transaccion;
import ec.edu.utn.repository.BilleteraRepository;
import ec.edu.utn.repository.MapeoUsuarioRepository;
import ec.edu.utn.repository.TransaccionRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Random;
import jakarta.json.Json;
import jakarta.json.JsonObject;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject private MapeoUsuarioRepository mapeoRepo;
    @Inject private BilleteraRepository billeteraRepo;
    @Inject private TransaccionRepository transaccionRepo;

    @POST
    @Path("/registrar")
    public Response registrar(String jsonBody) {
        try {
            JsonObject json = Json.createReader(new StringReader(jsonBody)).readObject();
            String username = json.getString("username");
            
            if (username == null || username.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Username es requerido\"}").build();
            }
            
            if (mapeoRepo.existsByUsername(username)) {
                return Response.status(Response.Status.CONFLICT).entity("{\"error\":\"El usuario ya existe\"}").build();
            }
            
            int nuevoUsuarioId = 100 + new Random().nextInt(900);
            
            MapeoUsuario mapeo = new MapeoUsuario();
            mapeo.setUsername(username);
            mapeo.setUsuarioId(nuevoUsuarioId);
            mapeoRepo.save(mapeo);
            
            System.out.println(">>> Usuario registrado: " + username + " con ID: " + nuevoUsuarioId);
            
            Billetera billetera = new Billetera();
            billetera.setUsuarioId(nuevoUsuarioId);
            billetera.setSaldo(new BigDecimal("10.00"));
            Billetera guardada = billeteraRepo.save(billetera);
            
            Transaccion bono = new Transaccion();
            bono.setBilletera(guardada);
            bono.setTipo(Transaccion.TipoTransaccion.BONO_BIENVENIDA);
            bono.setMonto(new BigDecimal("10.00"));
            bono.setDescripcion("Bono de bienvenida");
            transaccionRepo.save(bono);
            
            System.out.println(">>> Billetera creada para usuario " + nuevoUsuarioId + " con saldo 10.00 UGC");
            
            return Response.status(Response.Status.CREATED).entity("{\"usuarioId\":" + nuevoUsuarioId + ",\"username\":\"" + username + "\"}").build();
            
        } catch (Exception e) {
            System.err.println(">>> Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Error interno: " + e.getMessage() + "\"}").build();
        }
    }

    @POST
    @Path("/login")
    public Response login(String jsonBody) {
        try {
            JsonObject json = Json.createReader(new StringReader(jsonBody)).readObject();
            String username = json.getString("username");
            
            // CORRECCIÓN: Usar orElseGet en lugar de orElse para la lambda
            return mapeoRepo.findByUsername(username)
                .map(m -> {
                    System.out.println(">>> Login exitoso para: " + username + " con ID: " + m.getUsuarioId());
                    return Response.ok("{\"usuarioId\":" + m.getUsuarioId() + ",\"username\":\"" + username + "\"}").build();
                })
                .orElseGet(() -> {
                    System.out.println(">>> Login fallido: usuario " + username + " no existe");
                    return Response.status(Response.Status.NOT_FOUND).entity("{\"error\":\"Usuario no encontrado\"}").build();
                });
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Error: " + e.getMessage() + "\"}").build();
        }
    }
    
    @GET
    @Path("/test")
    public Response test() {
        return Response.ok("{\"status\":\"API funcionando\"}").build();
    }
}
