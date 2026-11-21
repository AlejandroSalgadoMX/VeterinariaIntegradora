-- ATENCION: este script elimina completamente la base veterinaria_integradora
-- y todos sus datos. Usar solo para reiniciar el proyecto desde cero.
-- MySQL 8.0 | Modelo en Tercera Forma Normal (3FN)

DROP DATABASE IF EXISTS veterinaria_integradora;

CREATE DATABASE veterinaria_integradora
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE veterinaria_integradora;

-- ===== Seguridad y catálogo de especies =====

CREATE TABLE usuario (
  usuario_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  rol ENUM('ADMINISTRADOR', 'RECEPCION', 'VETERINARIO') NOT NULL,
  correo VARCHAR(120) NOT NULL,
  contrasena_hash VARCHAR(255) NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_usuario_correo UNIQUE (correo)
) ENGINE=InnoDB COMMENT='Usuarios del sistema; rol de dominio cerrado';

CREATE TABLE veterinario (
  veterinario_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  usuario_id INT UNSIGNED NOT NULL,
  nombres VARCHAR(80) NOT NULL,
  apellido_paterno VARCHAR(60) NOT NULL,
  apellido_materno VARCHAR(60) NULL,
  cedula_profesional VARCHAR(30) NULL,
  especialidad VARCHAR(100) NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uq_veterinario_usuario UNIQUE (usuario_id),
  CONSTRAINT uq_veterinario_cedula UNIQUE (cedula_profesional),
  CONSTRAINT fk_veterinario_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuario(usuario_id)
) ENGINE=InnoDB COMMENT='Perfil profesional del usuario con rol veterinario';

CREATE TABLE especie (
  especie_id SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL,
  CONSTRAINT uq_especie_nombre UNIQUE (nombre)
) ENGINE=InnoDB COMMENT='Catálogo de especies de mascotas; se conserva por la relación N:M';

-- ===== Dueños y pacientes =====

CREATE TABLE dueno (
  dueno_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombres VARCHAR(80) NOT NULL,
  apellido_paterno VARCHAR(60) NOT NULL,
  apellido_materno VARCHAR(60) NULL,
  telefono VARCHAR(20) NOT NULL,
  correo VARCHAR(120) NULL,
  direccion_corta VARCHAR(200) NULL,
  notas TEXT NULL,
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='Responsable de una o más mascotas';

CREATE TABLE mascota (
  mascota_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  dueno_id INT UNSIGNED NOT NULL,
  especie_id SMALLINT UNSIGNED NOT NULL,
  estado ENUM('ACTIVA', 'INACTIVA', 'FALLECIDA') NOT NULL DEFAULT 'ACTIVA',
  nombre VARCHAR(80) NOT NULL,
  raza VARCHAR(80) NULL,
  sexo ENUM('MACHO', 'HEMBRA') NOT NULL,
  fecha_nacimiento DATE NULL,
  edad_aproximada_meses SMALLINT UNSIGNED NULL,
  senas_particulares VARCHAR(255) NULL,
  creado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_mascota_edad_origen CHECK (
    (fecha_nacimiento IS NOT NULL AND edad_aproximada_meses IS NULL)
    OR (fecha_nacimiento IS NULL AND edad_aproximada_meses IS NOT NULL)
  ),
  CONSTRAINT fk_mascota_dueno
    FOREIGN KEY (dueno_id) REFERENCES dueno(dueno_id),
  CONSTRAINT fk_mascota_especie
    FOREIGN KEY (especie_id) REFERENCES especie(especie_id)
) ENGINE=InnoDB COMMENT='Paciente clínico; pertenece a un solo dueño';

-- ===== Catálogo de servicios e inventario =====

CREATE TABLE atencion (
  atencion_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  tipo ENUM('CONSULTA', 'VACUNA', 'ESTUDIO', 'PROCEDIMIENTO') NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255) NULL,
  precio_base DECIMAL(10,2) NOT NULL,
  duracion_minutos SMALLINT UNSIGNED NOT NULL,
  edad_minima_dias SMALLINT UNSIGNED NULL,
  intervalo_minimo_dias SMALLINT UNSIGNED NULL,
  activa BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uq_atencion_nombre UNIQUE (nombre),
  CONSTRAINT ck_atencion_precio CHECK (precio_base >= 0),
  CONSTRAINT ck_atencion_duracion CHECK (duracion_minutos > 0)
) ENGINE=InnoDB COMMENT='Catálogo de consultas, vacunas, estudios y procedimientos';

CREATE TABLE atencion_especie (
  atencion_id INT UNSIGNED NOT NULL,
  especie_id SMALLINT UNSIGNED NOT NULL,
  PRIMARY KEY (atencion_id, especie_id),
  CONSTRAINT fk_atencion_especie_atencion
    FOREIGN KEY (atencion_id) REFERENCES atencion(atencion_id),
  CONSTRAINT fk_atencion_especie_especie
    FOREIGN KEY (especie_id) REFERENCES especie(especie_id)
) ENGINE=InnoDB COMMENT='Especies compatibles con cada atención (relación N:M)';

CREATE TABLE insumo (
  insumo_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255) NULL,
  precio_venta_base DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT uq_insumo_nombre UNIQUE (nombre),
  CONSTRAINT ck_insumo_precio CHECK (precio_venta_base >= 0)
) ENGINE=InnoDB COMMENT='Medicamento, vacuna o consumible controlado';

CREATE TABLE atencion_insumo (
  atencion_id INT UNSIGNED NOT NULL,
  insumo_id INT UNSIGNED NOT NULL,
  cantidad_requerida DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (atencion_id, insumo_id),
  CONSTRAINT ck_atencion_insumo_cantidad CHECK (cantidad_requerida > 0),
  CONSTRAINT fk_atencion_insumo_atencion
    FOREIGN KEY (atencion_id) REFERENCES atencion(atencion_id),
  CONSTRAINT fk_atencion_insumo_insumo
    FOREIGN KEY (insumo_id) REFERENCES insumo(insumo_id)
) ENGINE=InnoDB COMMENT='Insumos obligatorios requeridos por una atención (relación N:M)';

CREATE TABLE lote_insumo (
  lote_insumo_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  insumo_id INT UNSIGNED NOT NULL,
  unidad_medida ENUM('PIEZA', 'ML', 'MG', 'G', 'KG', 'DOSIS', 'TABLETA', 'FRASCO') NOT NULL,
  codigo_lote VARCHAR(50) NOT NULL,
  cantidad_ingresada DECIMAL(10,2) NOT NULL,
  fecha_ingreso DATE NOT NULL,
  fecha_caducidad DATE NOT NULL,
  CONSTRAINT uq_lote_insumo_codigo UNIQUE (insumo_id, codigo_lote),
  CONSTRAINT ck_lote_cantidad CHECK (cantidad_ingresada > 0),
  CONSTRAINT ck_lote_caducidad CHECK (fecha_caducidad >= fecha_ingreso),
  CONSTRAINT fk_lote_insumo_insumo
    FOREIGN KEY (insumo_id) REFERENCES insumo(insumo_id)
) ENGINE=InnoDB COMMENT='Entradas de inventario por lote y fecha de caducidad';

-- ===== Agenda y expediente =====

CREATE TABLE cita (
  cita_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  mascota_id INT UNSIGNED NOT NULL,
  veterinario_id INT UNSIGNED NOT NULL,
  estado ENUM('PROGRAMADA', 'EN_ATENCION', 'POR_COBRAR', 'CERRADA', 'CANCELADA', 'NO_ASISTIO') NOT NULL DEFAULT 'PROGRAMADA',
  fecha_hora_inicio DATETIME NOT NULL,
  fecha_hora_fin DATETIME NOT NULL,
  motivo VARCHAR(255) NOT NULL,
  creada_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_cita_rango_horario CHECK (fecha_hora_fin > fecha_hora_inicio),
  CONSTRAINT fk_cita_mascota
    FOREIGN KEY (mascota_id) REFERENCES mascota(mascota_id),
  CONSTRAINT fk_cita_veterinario
    FOREIGN KEY (veterinario_id) REFERENCES veterinario(veterinario_id),
  INDEX ix_cita_veterinario_horario (veterinario_id, fecha_hora_inicio, fecha_hora_fin),
  INDEX ix_cita_mascota (mascota_id)
) ENGINE=InnoDB COMMENT='Agenda; los traslapes se validan con la regla de negocio';

CREATE TABLE historial_clinico (
  historial_clinico_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  cita_id INT UNSIGNED NOT NULL,
  peso_kg DECIMAL(5,2) NULL,
  diagnostico TEXT NULL,
  indicaciones TEXT NULL,
  observaciones TEXT NULL,
  registrado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_historial_cita UNIQUE (cita_id),
  CONSTRAINT ck_historial_peso CHECK (peso_kg IS NULL OR peso_kg > 0),
  CONSTRAINT fk_historial_cita
    FOREIGN KEY (cita_id) REFERENCES cita(cita_id)
) ENGINE=InnoDB COMMENT='Nota clínica por cita; la mascota se obtiene desde cita para evitar redundancia';

CREATE TABLE cita_atencion (
  cita_atencion_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  cita_id INT UNSIGNED NOT NULL,
  atencion_id INT UNSIGNED NOT NULL,
  precio_aplicado DECIMAL(10,2) NOT NULL,
  observaciones VARCHAR(255) NULL,
  aplicada_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_cita_atencion_precio CHECK (precio_aplicado >= 0),
  CONSTRAINT fk_cita_atencion_cita
    FOREIGN KEY (cita_id) REFERENCES cita(cita_id),
  CONSTRAINT fk_cita_atencion_atencion
    FOREIGN KEY (atencion_id) REFERENCES atencion(atencion_id),
  INDEX ix_cita_atencion_historial (atencion_id, aplicada_en)
) ENGINE=InnoDB COMMENT='Atenciones aplicadas durante una cita; conserva el precio histórico';

CREATE TABLE uso_insumo (
  uso_insumo_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  cita_atencion_id INT UNSIGNED NOT NULL,
  lote_insumo_id INT UNSIGNED NOT NULL,
  cantidad_usada DECIMAL(10,2) NOT NULL,
  precio_cobrado_unitario DECIMAL(10,2) NOT NULL DEFAULT 0.00,
  usado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT ck_uso_cantidad CHECK (cantidad_usada > 0),
  CONSTRAINT ck_uso_precio CHECK (precio_cobrado_unitario >= 0),
  CONSTRAINT fk_uso_cita_atencion
    FOREIGN KEY (cita_atencion_id) REFERENCES cita_atencion(cita_atencion_id),
  CONSTRAINT fk_uso_lote
    FOREIGN KEY (lote_insumo_id) REFERENCES lote_insumo(lote_insumo_id),
  INDEX ix_uso_lote (lote_insumo_id)
) ENGINE=InnoDB COMMENT='Salida de un lote usada en una atención aplicada';

-- ===== Cobro =====

CREATE TABLE cargo (
  cargo_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  cita_id INT UNSIGNED NOT NULL,
  generado_en DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_cargo_cita UNIQUE (cita_id),
  CONSTRAINT fk_cargo_cita
    FOREIGN KEY (cita_id) REFERENCES cita(cita_id)
) ENGINE=InnoDB COMMENT='Cargo generado al documentar la atención; su total se calcula desde los detalles';

CREATE TABLE pago (
  pago_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  cargo_id INT UNSIGNED NOT NULL,
  forma_pago ENUM('EFECTIVO', 'TARJETA', 'TRANSFERENCIA') NOT NULL,
  monto_pagado DECIMAL(10,2) NOT NULL,
  fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_pago_cargo UNIQUE (cargo_id),
  CONSTRAINT ck_pago_monto CHECK (monto_pagado >= 0),
  CONSTRAINT fk_pago_cargo
    FOREIGN KEY (cargo_id) REFERENCES cargo(cargo_id)
) ENGINE=InnoDB COMMENT='Un solo pago por cargo; no se permiten pagos parciales';

-- ===== Vistas de datos derivados (no duplican información) =====

CREATE OR REPLACE VIEW vw_existencia_lote AS
SELECT
  li.lote_insumo_id,
  li.insumo_id,
  li.codigo_lote,
  li.unidad_medida,
  li.cantidad_ingresada,
  COALESCE(SUM(ui.cantidad_usada), 0) AS cantidad_utilizada,
  li.cantidad_ingresada - COALESCE(SUM(ui.cantidad_usada), 0) AS existencia_disponible,
  li.fecha_caducidad
FROM lote_insumo AS li
LEFT JOIN uso_insumo AS ui ON ui.lote_insumo_id = li.lote_insumo_id
GROUP BY
  li.lote_insumo_id, li.insumo_id, li.codigo_lote, li.unidad_medida,
  li.cantidad_ingresada, li.fecha_caducidad;

CREATE OR REPLACE VIEW vw_total_cargo AS
SELECT
  c.cargo_id,
  c.cita_id,
  COALESCE((
    SELECT SUM(ca.precio_aplicado)
    FROM cita_atencion AS ca
    WHERE ca.cita_id = c.cita_id
  ), 0) + COALESCE((
    SELECT SUM(ui.cantidad_usada * ui.precio_cobrado_unitario)
    FROM uso_insumo AS ui
    INNER JOIN cita_atencion AS ca ON ca.cita_atencion_id = ui.cita_atencion_id
    WHERE ca.cita_id = c.cita_id
  ), 0) AS total_cargo
FROM cargo AS c;

-- Reglas de negocio que se validan en el servicio o procedimiento transaccional:
-- 1. Un veterinario activo no puede tener citas traslapadas.
-- 2. La especie, edad mínima e intervalo de la atención deben ser válidos para la mascota.
-- 3. Cada uso debe provenir de un lote vigente, del insumo requerido y con existencia suficiente.
-- 4. Al registrar las atenciones se genera un cargo y la cita pasa a "POR_COBRAR".
-- 5. El pago debe ser igual a vw_total_cargo.total_cargo; entonces la cita pasa a "CERRADA".
