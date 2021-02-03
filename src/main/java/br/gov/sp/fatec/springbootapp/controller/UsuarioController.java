package br.gov.sp.fatec.springbootapp.controller;

import br.gov.sp.fatec.springbootapp.entity.Autorizacao;
import br.gov.sp.fatec.springbootapp.entity.Livro;
import br.gov.sp.fatec.springbootapp.repository.AutorizacaoRepository;
import br.gov.sp.fatec.springbootapp.repository.LivroRepository;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import br.gov.sp.fatec.springbootapp.entity.Usuario;
import br.gov.sp.fatec.springbootapp.service.UsuarioService;
import br.gov.sp.fatec.springbootapp.repository.UsuarioRepository;
import java.util.List;

@RestController
@RequestMapping (value = "/usuario", method = RequestMethod.GET) //Indica a URI base de todos os serviços definidos nesse Controller.
                                                            // Nesse caso, máquina local com porta 8080, a URI base seria "http://localhost:8080/livros/usuario"
@CrossOrigin //Permite que os serviços definidos nessa classe possam ser acessados por aplicações JavaScript hospedadas em outros servidores;
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository userRepo;

    @PostMapping(value = "/novo") //Define que o método a seguir é um serviço que responde a requisições do tipo POST.
    // O parâmetro "value" define o URI específico desse serviço. Seguindo o exemplo, seria "http://localhost:8080/livros/usuario/novo";
    public Usuario cadastrarUsuario(@RequestBody UsuarioDTO usuario) { //Essa anotação acompanha o parâmetro "usuario" do método
        // e indica que ele deve ser retirado do "corpo" da requisição.


        // Nesse caso, os dados do usuário devem vir em um JSON com formato compatível à classe UsuarioDTO
        //O retorno do método será um JSON gerado a partir da classe Usuario (retorno do método novoUsuario do serviço UsuarioService).
        return usuarioService.novoUsuario(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getAutorizacao());
    }

    //AQUI ABAIXO EU UTILIZO A BUSCA EM 2 ENTIDADES (inner join) QUE FIZ NO USUARIO REPOSITORY \o/
    @GetMapping(value = "/busca/{autorizacao}")
    @JsonView(View.LivroResumo.class)
    public List<Usuario> buscarPorAutorizacao(
            @PathVariable("autorizacao") String autorizacao) {
        return userRepo.buscaPorNomeAutorizacao(autorizacao);
    }
}



