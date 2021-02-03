package br.gov.sp.fatec.springbootapp.controller;


/*A ideia consiste basicamente em agrupar um conjunto de
 atributos numa classe simples de forma a otimizar a comunicação.
 Nesse caso, os dados do usuário devem vir em um JSON com formato compatível à classe LivroDTO
 */

public class LivroDTO {

    private String usuario;
    private String titulo;

    //GETTERS AND SETTERS

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    
}