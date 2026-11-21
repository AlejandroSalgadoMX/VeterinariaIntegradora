# Veterinaria Integradora

API del sistema de veterinaria para la integradora de Aplicaciones Web. Está hecha con Spring Boot, JPA y MySQL.

## Requisitos

- Java 21
- MySQL 8
- Maven o IntelliJ

## Configuración

1. Crear el archivo `application-local.properties` en la raíz del proyecto con los datos de su MySQL (este archivo no se sube a GitHub):

```properties
spring.datasource.username=root
spring.datasource.password=su_password
```

2. Correr `VeterinariaIntegradoraApplication`.

No hace falta crear la base de datos antes: al arrancar se crea `veterinaria_integradora` con sus tablas. Si prefieren crearla a mano, el script está en `database/veterinaria_integradora.sql` (ojo: borra la base si ya existe).

La primera vez que se corre, si no hay usuarios en la base, se crea un administrador:

- Correo: `admin@veterinaria.mx`
- Contraseña: `Admin1234`

## Pruebas con Postman

En la carpeta `postman` está la colección con todas las peticiones. Primero se hace el login (Auth > Login) y el token se guarda solo en la colección.

Todas las peticiones, menos el login, necesitan el header `Authorization: Bearer <token>`.

## Roles

- **ADMINISTRADOR:** tiene acceso a todo.
- **RECEPCION:** dueños, mascotas, citas y cobros.
- **VETERINARIO:** historial clínico, atenciones, uso de insumos y expediente.

Si un usuario está desactivado ya no puede entrar.

## Servicios

### Login
- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/auth/logout`

### Administrador
- Usuarios: `/api/usuarios`
- Veterinarios: `/api/veterinarios`
- Especies: `/api/especies`
- Catálogo de atenciones: `/api/atenciones`
- Insumos: `/api/insumos`
- Lotes de un insumo: `/api/insumos/{id}/lotes`
- Lotes por caducar: `/api/insumos/lotes/por-caducar?dias=30`

Para activar o desactivar se usa `PATCH /{id}/estado`.

### Recepción
- Dueños: `/api/duenos` (se puede buscar con `?q=`)
- Mascotas: `/api/mascotas`
- Mascotas de un dueño: `/api/duenos/{id}/mascotas`
- Citas: `/api/citas` (se puede filtrar por `fecha`, `veterinarioId`, `mascotaId` y `estado`)
- Cambiar estado de una cita: `PATCH /api/citas/{id}/estado`
- Citas pendientes de pago: `GET /api/cobros/pendientes`
- Ver el cargo: `GET /api/citas/{id}/cargo`
- Pagar: `POST /api/citas/{id}/pago`

### Veterinario
- Nota clínica: `GET` y `PUT /api/citas/{id}/historial`
- Atenciones de la cita: `GET` y `POST /api/citas/{id}/atenciones`
- Quitar una atención: `DELETE /api/citas/{id}/atenciones/{citaAtencionId}`
- Usar un insumo: `POST /api/citas/{id}/atenciones/{citaAtencionId}/insumos`
- Insumos usados: `GET /api/citas/{id}/insumos`
- Terminar la consulta: `POST /api/citas/{id}/finalizar`
- Expediente de la mascota: `GET /api/mascotas/{id}/expediente`

## Cómo funciona una cita

1. Recepción agenda la cita y queda en `PROGRAMADA`. No se puede agendar si el veterinario ya tiene otra cita a esa hora o si está inactivo.
2. Cuando llega la mascota la cita se pasa a `EN_ATENCION`.
3. El veterinario registra la nota clínica y las atenciones. Antes de guardar una atención se revisa que sea para la especie de la mascota, que tenga la edad mínima y que ya haya pasado el intervalo desde la última vez que se le aplicó.
4. Si la atención necesita un insumo, se descuenta de un lote que no esté caducado. Si no hay existencia, no se registra.
5. Al finalizar se genera el cargo y la cita queda en `POR_COBRAR`.
6. Recepción registra el pago por el total completo (no hay pagos parciales) y la cita queda `CERRADA`.

Una cita también se puede marcar como `CANCELADA` o `NO_ASISTIO`.

El total del cargo es la suma de las atenciones más los insumos extra que se cobren. El insumo obligatorio de una atención ya va incluido en su precio.
