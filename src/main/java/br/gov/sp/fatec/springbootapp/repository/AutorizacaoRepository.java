package br.gov.sp.fatec.springbootapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.gov.sp.fatec.springbootapp.entity.Autorizacao;

public interface AutorizacaoRepository extends JpaRepository<Autorizacao,Long> {

//Consulta que busca uma Autorização pelo Nome.
    public Autorizacao findByNome(String nome);
    
}