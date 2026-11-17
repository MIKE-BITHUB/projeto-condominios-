package br.edu.catolica.ucb.poo.persistencia;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;

/**
 * Grava LocalDate como texto ISO (2026-11-10) no SQLite.
 */
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate data) {
        return data == null ? null : data.toString();
    }

    @Override
    public LocalDate convertToEntityAttribute(String texto) {
        return texto == null ? null : LocalDate.parse(texto);
    }
}
