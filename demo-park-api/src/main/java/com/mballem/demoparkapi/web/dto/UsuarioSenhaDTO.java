package com.mballem.demoparkapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UsuarioSenhaDTO {

    @NotBlank
    @Size(min = 6, max = 6)
    private String senhaAtual;

    @NotBlank
    @Size(min = 6, max = 6)
    private String novaSenha;

    @NotBlank
    @Size(min = 6, max = 6)
    private String confirmarSenha;
}
