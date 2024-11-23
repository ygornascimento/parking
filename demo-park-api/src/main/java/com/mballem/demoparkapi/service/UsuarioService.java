package com.mballem.demoparkapi.service;

import com.mballem.demoparkapi.entity.Usuario;
import com.mballem.demoparkapi.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado")
        );
    }

    @Transactional
    public Usuario editarSenha(Long id, String senhaAtual, String novaSenha, String confirmarSenha) {
        if (!novaSenha.equals(confirmarSenha)) {
            throw new RuntimeException("Nova Senha não confere com confirmação de senha");
        }

        Usuario user = buscarPorId(id);
        if (!user.getPassword().equals(senhaAtual)) {
            throw new RuntimeException("Sua senha não confere.");
        }
        user.setPassword(novaSenha);

        return user;
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }
}
