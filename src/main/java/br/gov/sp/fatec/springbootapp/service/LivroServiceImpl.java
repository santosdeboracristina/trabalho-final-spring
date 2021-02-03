package br.gov.sp.fatec.springbootapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import br.gov.sp.fatec.springbootapp.entity.Livro;
import br.gov.sp.fatec.springbootapp.entity.Usuario;
import br.gov.sp.fatec.springbootapp.repository.LivroRepository;
import br.gov.sp.fatec.springbootapp.repository.UsuarioRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

//Implementa o método definido na interface "LivroService".
@Service("livroService")
public class LivroServiceImpl implements LivroService {

    @Autowired
    private LivroRepository livroRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    @Transactional //Devido às diversas operações realizadas no Banco de Dados utilizamos @Transactional para manter todas em uma única transação, evitando riscos de inconsistência de dados.
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')") /*No caso do método de adicionar um novo livro, apenas usuários autenticados e que possuam as permissões ROLE_ADMIN ou ROLE_USER podem acessá-lo.*/
    public Livro adicionarLivro(String identificadorUsuario, String titulo) {
        Usuario usuario = usuarioRepo.findTop1ByNomeOrEmail(
                identificadorUsuario, identificadorUsuario);
        if(usuario == null) {
            throw new UsernameNotFoundException(
                    "Usuário com identificador " +
                            identificadorUsuario +
                            " não foi encontrado");
        }
        Livro livro = new Livro();
        livro.setAutor(usuario);
        livro.setTitulo(titulo);
        livroRepo.save(livro);
        return livro;
    }
}
