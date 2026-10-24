package mx.edu.utez.veterinariaintegradora.model.cobro;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Getter
@Setter
@NoArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pago_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cargo_id", nullable = false, unique = true)
    private Cargo cargo;

    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pago", nullable = false, length = 15)
    private FormaPago formaPago;

    @Column(name = "monto_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoPagado;

    @Column(name = "fecha_pago", nullable = false, updatable = false)
    private LocalDateTime fechaPago;

    @PrePersist
    void asignarFechaPago() {
        if (fechaPago == null) {
            fechaPago = LocalDateTime.now();
        }
    }
}
