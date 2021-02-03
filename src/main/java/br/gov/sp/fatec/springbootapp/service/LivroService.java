package br.gov.sp.fatec.springbootapp.service;

import br.gov.sp.fatec.springbootapp.entity.Livro;

public interface LivroService {

    //Existe apenas um método para adicionar uma frase a um usuário com base em alguns parâmetros, no caso, o id do usuario e o titulo do livro
    public Livro adicionarLivro(String identificadorUsuario, String titulo);

}
