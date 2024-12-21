package com.mballem.demoparkapi.web.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClienteCreateDTO {

    @NonNull
    @Size(min = 5, max = 20)
    private String nome;

    @Size(min = 11, max = 11)
    @CPF
    private String cpf;
}
