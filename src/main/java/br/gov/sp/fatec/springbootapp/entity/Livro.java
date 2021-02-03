package br.gov.sp.fatec.springbootapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import br.gov.sp.fatec.springbootapp.controller.View;


// NESSA CLASSE, EU MAPEIO A TABELA "liv_livro" DO BD.
//POSSUO O RELACIONAMENTO N-1 COM A CLASSE USUARIO
@Entity //se eu so colocar @entity, vai dar erro, pois o nome da minha classe n eh o mesmo do bd
@Table(name = "liv_livro") //resolvendo problema de cima

public class Livro {
    @Id //mapeando a chave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //n posso receber o id, ele eh auto increment
    @Column(name = "liv_id") //mapeando o id de cada livro la da tabela liv_livro
    @JsonView(View.LivroCompleto.class)
    private Long id;

    @Column(name = "liv_titulo", length=150, nullable = false) //mapeando o titulo de cada livro la da tabela liv_livro
    @JsonView(View.LivroResumo.class)
    private String titulo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "usr_autor_id") //MAPEIO A CHAVE ESTRANGEIRA COM A ANOTACAO @JOINCOLUMN
    @JsonView(View.LivroCompleto.class)
    private Usuario autor;

    //GETTERS E SETTERS LOGO ABAIXO:

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }
    
}