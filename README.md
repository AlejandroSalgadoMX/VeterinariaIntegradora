# Veterinaria Integradora — API REST

API en Spring Boot 4 (Java 21, JPA, MySQL 8) para el **Sistema de Veterinaria**, conforme al
DFR v3.0 (historias de usuario). Cubre las 11 historias de usuario y todas las reglas de negocio.

## Cómo ejecutarlo

1. Configurar la conexión a MySQL 8 (por variables de entorno o en `application-local.properties`, que no se sube al repo):
   ```properties
   spring.datasource.username=root
   spring.datasource.password=tu_password
   ```
2. Ejecutar `VeterinariaIntegradoraApplication` (o `mvn spring-boot:run`). Al arrancar se crea la base
   `veterinaria_integradora` y sus tablas si no existen (`createDatabaseIfNotExist` + `ddl-auto=update`).
   Para crearla manualmente con el modelo 3FN completo (vistas y restricciones CHECK) está el script
   `database/veterinaria_integradora.sql` (elimina la base si ya existe).
3. Si la tabla `usuario` está vacía se crea un administrador inicial:
   `admin@veterinaria.mx` / `Admin1234` (configurable con `ADMIN_CORREO` y `ADMIN_CONTRASENA`).
4. Importar `postman/Veterinaria-Integradora.postman_collection.json` y ejecutar **Auth > Login**
   (guarda el token en la colección automáticamente).

## Autenticación y roles

- `POST /api/auth/login` devuelve un token; enviarlo en cada petición como `Authorization: Bearer <token>`.
- Solo usuarios **activos** y con rol operan (regla del DFR). Si se desactiva un usuario, su token deja de servir.
- El **ADMINISTRADOR** tiene acceso a todo; **RECEPCION** y **VETERINARIO** solo a lo de su rol.
- Errores: `400` datos inválidos, `401` sin sesión, `403` rol sin permiso, `404` no existe, `409` regla de negocio.

## Endpoints por historia de usuario

| HU | Rol | Endpoints |
|---|---|---|
| — Autenticación | Todos | `POST /api/auth/login`, `GET /api/auth/me`, `POST /api/auth/logout` |
| HU-ADM-01 Usuarios | Admin | `GET/POST /api/usuarios`, `GET/PUT /api/usuarios/{id}`, `PATCH /api/usuarios/{id}/estado` |
| HU-ADM-02 Veterinarios | Admin (consulta: todos) | `GET/POST /api/veterinarios`, `GET/PUT /api/veterinarios/{id}`, `PATCH /api/veterinarios/{id}/estado` |
| Especies (catálogo) | Admin (consulta: todos) | `GET/POST /api/especies`, `GET/PUT /api/especies/{id}` |
| HU-ADM-03 Atenciones | Admin (consulta: todos) | `GET/POST /api/atenciones`, `GET/PUT /api/atenciones/{id}`, `PATCH /api/atenciones/{id}/estado` |
| HU-ADM-04 Insumos y lotes | Admin (consulta: veterinario) | `GET/POST /api/insumos`, `GET/PUT /api/insumos/{id}`, `PATCH /api/insumos/{id}/estado`, `GET/POST /api/insumos/{id}/lotes`, `GET /api/insumos/lotes/{loteId}`, `GET /api/insumos/lotes/por-caducar?dias=30` |
| HU-REC-01 Dueños | Recepción | `GET /api/duenos?q=`, `POST /api/duenos`, `GET/PUT/DELETE /api/duenos/{id}` |
| HU-REC-02 Mascotas | Recepción | `GET/POST /api/mascotas`, `GET/PUT /api/mascotas/{id}`, `PATCH /api/mascotas/{id}/estado`, `GET /api/duenos/{id}/mascotas` |
| HU-REC-03 Agenda | Recepción | `GET /api/citas?fecha=&veterinarioId=&mascotaId=&estado=`, `POST /api/citas`, `GET/PUT /api/citas/{id}`, `PATCH /api/citas/{id}/estado` |
| HU-VET-01 Historial | Veterinario | `GET/PUT /api/citas/{id}/historial` |
| HU-VET-02 Atenciones aplicadas | Veterinario | `GET/POST /api/citas/{id}/atenciones`, `DELETE /api/citas/{id}/atenciones/{citaAtencionId}`, `POST /api/citas/{id}/finalizar` |
| HU-VET-03 Uso de insumos | Veterinario | `POST /api/citas/{id}/atenciones/{citaAtencionId}/insumos`, `GET /api/citas/{id}/insumos` |
| HU-REC-04 Cobros | Recepción | `GET /api/cobros/pendientes`, `GET /api/citas/{id}/cargo`, `POST /api/citas/{id}/pago` |
| HU-VET-04 Expediente | Veterinario | `GET /api/mascotas/{id}/expediente` |

## Reglas de negocio implementadas

| Regla del DFR | Dónde se valida |
|---|---|
| Toda mascota pertenece a un dueño; historial por mascota | `MascotaService`, `ExpedienteService` |
| Sin citas traslapadas para el mismo veterinario (y la misma mascota) | `CitaService` (con bloqueo del veterinario) |
| Veterinario inactivo no recibe citas nuevas | `CitaService.agendar` / `reprogramar` |
| Especie permitida, edad mínima e intervalo desde la última aplicación | `AtencionClinicaService` |
| Atención con insumo obligatorio solo si se descuenta un lote vigente | `AtencionClinicaService` + `InventarioService` (FEFO o lote elegido) |
| No se usa lote caducado ni hay existencia negativa | `InventarioService.reservarLote*` (bloqueo pesimista del lote) |
| Al documentar la visita se arma el cargo y la cita queda POR_COBRAR | `POST /api/citas/{id}/finalizar` |
| Pago único por el total; la cita pasa a CERRADA; sin pagos parciales | `CobroService.registrarPago` |
| Solo usuarios activos y con rol operan según permisos | `AutenticacionInterceptor` + `@RolesPermitidos` |

## Flujo de una visita

```
Recepción: POST /api/citas                         -> PROGRAMADA
Vet/Rec:   PATCH /api/citas/{id}/estado EN_ATENCION -> EN_ATENCION
Vet:       PUT  /api/citas/{id}/historial           (peso, diagnóstico, indicaciones)
Vet:       POST /api/citas/{id}/atenciones          (valida especie/edad/intervalo y descuenta lote)
Vet:       POST /api/citas/{id}/atenciones/{x}/insumos (insumos adicionales cobrables)
Vet:       POST /api/citas/{id}/finalizar           -> POR_COBRAR (genera cargo)
Recepción: POST /api/citas/{id}/pago {formaPago, montoPagado = total} -> CERRADA
```

Notas de cobro: el total del cargo es la suma de los precios de las atenciones aplicadas más los
insumos adicionales cobrables (cantidad × precio de venta). Los insumos obligatorios de una atención
ya están incluidos en su precio.

## Estructura

```
config/       WebConfig (interceptor), DatosInicialesConfig (admin inicial)
controller/   un controlador por módulo (@RestController, @CrossOrigin)
dto/          requests con validaciones jakarta y responses (records)
exception/    ErrorHandler (@RestControllerAdvice) y excepciones de negocio
model/        entidades JPA (modelo 3FN)
repository/   repositorios Spring Data JPA
security/     login con token, hash PBKDF2, roles por anotación
service/      lógica y reglas de negocio transaccionales
```
