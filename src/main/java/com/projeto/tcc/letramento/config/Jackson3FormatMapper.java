package com.projeto.tcc.letramento.config;

import org.hibernate.type.format.AbstractJsonFormatMapper;
import tools.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;

public class Jackson3FormatMapper extends AbstractJsonFormatMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T fromString(CharSequence charSequence, Type type) {
        if (charSequence == null) {
            return null;
        }
        try {
            // Usa o type system do Jackson 3 para mapear o tipo dinâmico do Hibernate
            return objectMapper.readValue(charSequence.toString(), objectMapper.constructType(type));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desserializar JSON com Jackson 3 no Hibernate", e);
        }
    }

    @Override
    public <T> String toString(T value, Type type) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar objeto com Jackson 3 no Hibernate", e);
        }
    }
}
