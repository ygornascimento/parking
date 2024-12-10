package com.mballem.demoparkapi.web.dto.mapper;

import com.mballem.demoparkapi.entity.Cliente;
import com.mballem.demoparkapi.web.dto.ClienteCreateDTO;
import com.mballem.demoparkapi.web.dto.ClienteResponseDTO;
import lombok.*;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
public class ClienteMapper {
    public static Cliente toCliente(ClienteCreateDTO dto) {
        return new ModelMapper().map(dto, Cliente.class);
    }
    public static ClienteResponseDTO toDto(Cliente cliente) {
        return new ModelMapper().map(cliente, ClienteResponseDTO.class);
    }
}
