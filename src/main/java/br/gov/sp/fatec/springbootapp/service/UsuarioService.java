package br.gov.sp.fatec.springbootapp.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import br.gov.sp.fatec.springbootapp.entity.Usuario;

//minha interface UsuarioService implementa a c...COMENTAR MAIS AQUI
public interface UsuarioService extends UserDetailsService {

    //metodo novoUsuario usado parar criar um usuario com base nos parametros abaixo
    public Usuario novoUsuario(String nome, String email, String senha, String nomeAutorizacao);

}