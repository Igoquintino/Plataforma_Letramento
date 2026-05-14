package com.projeto.tcc.letramento.dto;

import jakarta.validation.constraints.NotBlank;

public record TrailRequestDTO(@NotBlank String title, @NotBlank String description) {
}
