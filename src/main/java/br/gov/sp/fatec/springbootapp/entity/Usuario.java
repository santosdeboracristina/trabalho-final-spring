package br.gov.sp.fatec.springbootapp.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonView;

import br.gov.sp.fatec.springbootapp.controller.View;

//
@Entity // se eu so colocar @entity, vai dar erro, pois o nome da minha classe n eh o
        // mesmo do bd
@Table(name = "usr_usuario") // resolvendo problema de cima
public class Usuario {

    @Id // mapeando a chave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // n posso receber o id, ele eh auto increment
    @Column(name = "usr_id") // mapeando a tabela do banco de dados
    @JsonView(View.LivroCompleto.class)
    private Long id;

    @Column(name = "usr_nome", unique=true, length=20, nullable=false)
    @JsonView(View.LivroCompleto.class)
    private String nome;

    @Column(name = "usr_email", unique=true, length = 100, nullable=false)
    @JsonView(View.LivroCompleto.class)
    private String email;

    @Column(name = "usr_senha", unique=true, length=100, nullable=false)
    private String senha;

    //NESSE RELACIONAMENTO ABAIXO, UM USUARIO PODE TER VARIAS AUTORIZACOES
    // (USER E ADMIN) E UMA AUTORIZACAO PODE ESTAR ASSOCIADA A VARIOS USUARIOS
    //ESSE RELACIONAMENTO N-N, EH MAPEADO PELA TABELA "UAU_USUARIO_AUTORIZACAO"
    //POREM,ESSE RELACIONAMENTO PRECISA SER MAPEADO EM APENAS UMA TABELA,
    // E ELA JA ESTA SENDO MAPEADA NESSA CLASSE (Usuario)
    // ELA SO FAZ A LIGACAO DAS COLUNAS DE 2 TABELAS DO BD DIFERENTES.
    @ManyToMany(fetch = FetchType.EAGER) //quando eu procurar um usuario, o EAGER vai fazer um join e ja trazer as autorizacoes tambem
    @JoinTable(name = "uau_usuario_autorizacao",
        joinColumns = { @JoinColumn(name="usr_id") }, //junta o id do usuario com o id da autorizacao
        inverseJoinColumns = { @JoinColumn(name = "aut_id")}) //junta o id da autorizacao com o id do usuario
    private Set<Autorizacao> autorizacoes;

    //AQUI EM BAIXO ELE TEM O RELACIONAMENTO 1-N (UM USUARIO PODE CRIAR VARIOS TITULOS, MAS UM TITULO SÓ PODE SER CRIADO POR UM USUARIO)
    // COM A CLASSE LIVRO, ele é mapeado pelo atributo "autor" la da minha classe LIVRO.
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "autor") //No Lazy, ele tras apenas quando eu solicitar explicitamente o carregamento desse objeto
	private Set<Livro> livros; //UTILIZO O SET E NAO LIST, PQ A LISTA PODE ADD REGISTROS REPETIDOS NO BD E AQUI EU NAO POSSO, POIS OS LIVROS SAO UNICOS

    //GETTERS E SETTERS LOGO ABAIXO: 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
		this.id = id;
	}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
		this.nome = nome;
	}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
		this.email = email;
	}

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
		this.senha = senha;
	}

    public Set<Autorizacao> getAutorizacoes() {
        return autorizacoes;
    }

    public void setAutorizacoes(Set<Autorizacao> autorizacoes) {
		this.autorizacoes = autorizacoes;
	}

    public Set<Livro> getLivros() {
        return livros;
    }

    public void setLivros(Set<Livro> livros) {
		this.livros = livros;
    }
}
