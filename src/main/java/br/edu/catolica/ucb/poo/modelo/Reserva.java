package br.edu.catolica.ucb.poo.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "reserva")
public class Reserva implements Exportavel {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "area_id")
    private AreaComum area;

    @ManyToOne(optional = false)
    @JoinColumn(name = "morador_id")
    private Morador morador;

    @Column(name = "data_reserva", nullable = false)
    private LocalDate dataReserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusReserva status;

    protected Reserva() {
    }

    public Reserva(AreaComum area, Morador morador, LocalDate dataReserva) {
        this.area = area;
        this.morador = morador;
        this.dataReserva = dataReserva;
        this.status = StatusReserva.CONFIRMADA;
    }

    public void cancelar() {
        this.status = StatusReserva.CANCELADA;
    }

    @Override
    public String paraCsv() {
        return id + ";" + area.getNome() + ";" + morador.getNome() + ";"
                + dataReserva.format(FORMATO) + ";" + status;
    }

    public Long getId() {
        return id;
    }

    public AreaComum getArea() {
        return area;
    }

    public Morador getMorador() {
        return morador;
    }

    public LocalDate getDataReserva() {
        return dataReserva;
    }

    public StatusReserva getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + area.getNome() + " | " + dataReserva.format(FORMATO)
                + " | " + morador.getNome() + " | " + status;
    }
}
