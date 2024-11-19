package com.mballem.demoparkapi.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@ToString
public class UsuarioCreateDTO {
    private String username;
    private String password;
}
