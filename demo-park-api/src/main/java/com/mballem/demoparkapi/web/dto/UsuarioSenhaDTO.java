package com.mballem.demoparkapi.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UsuarioSenhaDTO {
    private String senhaAtual;
    private String novaSenha;
    private String confirmarSenha;
}
