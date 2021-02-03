package br.gov.sp.fatec.springbootapp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


//A MINHA CLASSE AUTORIZACAO MAPEIA A TABELA DO BD "aut_autoricacao",
// COMO JA MAPEEI A TABELA N-N NA CLASSE USUARIO, NAO PRECISO MAPEAR DE NOVO AQUI
//DITO ISSO,  ESSA CLASSE NAO PRECISA DE RELACIONAMENTO ALGUM.
@Entity //se eu so colocar @entity, vai dar erro, pois o nome da minha classe n eh o mesmo do bd
@Table(name = "aut_autorizacao") //resolvendo problema de cima com a anotacao @table
public class Autorizacao {

    @Id //mapeando a chave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //n posso receber o id, ele eh auto increment
    @Column(name = "aut_id") //mapeando a tabela, espeficicamente o id
    private Long id;

    @Column(name = "aut_nome", unique=true, length=20, nullable=false) //mapeando a tabela, especificamente o nome da autoricao
    private String nome;


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

}