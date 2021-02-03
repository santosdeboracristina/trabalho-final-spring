package br.gov.sp.fatec.springbootapp.controller;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import br.gov.sp.fatec.springbootapp.entity.Livro;
import br.gov.sp.fatec.springbootapp.repository.LivroRepository;
import br.gov.sp.fatec.springbootapp.service.LivroService;

@CrossOrigin
@RestController
@RequestMapping(value ="/livro", method = RequestMethod.GET)
public class LivroController {
    @Autowired
    private LivroService livroService;

    @Autowired
    private LivroRepository livroRepo;

    @PostMapping(value = "/novo")
    @JsonView(View.LivroCompleto.class) //Para evitar o erro do loop, precisamos informar ao nosso programa que, ao gerar o JSON de uma frase, não nos interessa saber todos os livros já criadas pelo usuário, evitando o problema.
    public Livro cadastrarLivro(@RequestBody LivroDTO livro) {
        return livroService.adicionarLivro(livro.getUsuario(),
                livro.getTitulo());
    }

    @GetMapping (value = "/busca/{autor}") //@Getmapping indica que o método de requisição aceito é GET.
                                            // O trecho "{autor}" na URI indica que o parâmetro virá diretamente nela.
    //                                        Para mapear esse parâmetro usamos a anotação @PathVariable("autor").
    //                                        Usaremos a etiqueta View.LivroCompleto.class para formatação,
    //                                        o que deve incluir todos os atributos da classe Livro.
    @JsonView(View.LivroCompleto.class)
    public List<Livro>buscarPorTitulo(
            @PathVariable("autor") String autor){
        return livroRepo.findByAutorNomeOrAutorEmail(autor, autor);
    }
}

