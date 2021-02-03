package br.gov.sp.fatec.springbootapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;

import br.gov.sp.fatec.springbootapp.entity.Livro;


// Busca todas as frases de um Usuário com determinado Nome ou Endereço de E-mail.
public interface LivroRepository extends JpaRepository<Livro,Long> {

    //A anotacao PreAuthorize, pede para que o usuario esteja autenticado
    //Aqui Utilizo mais de um parametro para consulta.

    @PreAuthorize("isAuthenticated()")
    public List <Livro> findByAutorNomeOrAutorEmail (String nome, String email);
    
}