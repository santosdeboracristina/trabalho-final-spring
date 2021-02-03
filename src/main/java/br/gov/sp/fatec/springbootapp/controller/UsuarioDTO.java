package br.gov.sp.fatec.springbootapp.controller;


/*A ideia consiste basicamente em agrupar um conjunto de
        atributos numa classe simples de forma a otimizar a comunicação.
        Nesse caso, os dados do usuário devem vir em um JSON com formato compatível à classe UsuarioDTO
        */
public class UsuarioDTO {

    private String nome;
    private String email;
    private String senha;
    private String autorizacao;
    private String token;
   // private String token;

//GETTES E SETTERS LOGO ABAIXO:


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

    public String getAutorizacao() {
        return autorizacao;
    }

    public void setAutorizacao(String autorizacao) {
        this.autorizacao = autorizacao;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}