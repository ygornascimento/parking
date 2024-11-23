package com.mballem.demoparkapi.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private String role;
}
