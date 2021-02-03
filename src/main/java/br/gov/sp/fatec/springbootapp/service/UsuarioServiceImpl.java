package br.gov.sp.fatec.springbootapp.service;

import java.util.HashSet;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import br.gov.sp.fatec.springbootapp.entity.Autorizacao;
import br.gov.sp.fatec.springbootapp.entity.Usuario;
import br.gov.sp.fatec.springbootapp.repository.AutorizacaoRepository;
import br.gov.sp.fatec.springbootapp.repository.UsuarioRepository;


//implementa o método (novoUsuario) definido na interface. "Usuario Service"
@Service("usuarioService") //A anotação @Service indica que a instância da classe é gerenciada pelo Spring
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired //Os repositórios acessados são injetados ("setados") por meio da anotação @Autowired.
    private AutorizacaoRepository autorizacaoRepo;

    @Autowired
    private PasswordEncoder passEncoder;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    @Transactional //Devido às diversas operações realizadas no Banco de Dados utilizamos @Transactional para manter todas em uma única transaçao evitando risco de inconsistencia de dados
    @PreAuthorize("hasRole('ROLE_ADMIN')") /*O método de criar novos usuários é restrito apenas aos usuários autenticados que possuam a permissão ROLE_ADMIN.*/
    public Usuario novoUsuario(String nome, String email,
                               String senha, String nomeAutorizacao) {
        Autorizacao autorizacao =
                autorizacaoRepo.findByNome(nomeAutorizacao);
        if(autorizacao == null) {
            autorizacao = new Autorizacao();
            autorizacao.setNome(nomeAutorizacao);
            autorizacaoRepo.save(autorizacao);
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passEncoder.encode(senha)); //aqui codifico a senha antes de salvar, entao no BD as senhas sao gravadas com bcrypt
        usuario.setAutorizacoes(new HashSet<Autorizacao>());
        usuario.getAutorizacoes().add(autorizacao);
        usuarioRepo.save(usuario);
        return usuario;
    }
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Usuario usuario =
                usuarioRepo.findTop1ByNomeOrEmail( /*Utilizamos o repositório para buscar um usuário cujo nome ou e-mail
                 seja igual ao buscado (ambos são aceitos para login) e, caso encontremos...*/
                        username, username);
        if(usuario == null) {
            throw new UsernameNotFoundException("Usuário "
                    + username
                    + " não encontrado");
        }
        return User.builder().username(username) //..., geramos uma instância da classe User do Spring Security.
                .password(usuario.getSenha())
                .authorities(usuario.getAutorizacoes()
                        .stream()
                        .map(Autorizacao::getNome)
                        .collect(Collectors.toList())
                        .toArray(new String[usuario
                                .getAutorizacoes()
                                .size()]))
                .build();
    }
}