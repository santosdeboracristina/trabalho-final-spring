package br.gov.sp.fatec.springbootapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.gov.sp.fatec.springbootapp.entity.Usuario;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario,Long>{ //AQUI EU PRECISO PASSAR 2 PARAMETROS,E O REPOSITORIO DO QUE? (NO CASO ENTITY>USUARIO) E O TIPO DA CHAVE PRIMARIA

    /*O Repositório da entidade Usuario, possui uma consulta
 que busca e tras O PRIMEIRO REGISTRO de um Usuário pelo Nome ou Endereço de E-mail. */
    public Usuario findTop1ByNomeOrEmail (String nome, String email);

    /*E a anotacao @Query faz a consulta buscando todos os usuários que possuem o texto contido
no parâmetro "nome" em qualquer lugar de seu atributo nome e é
equivalente ao query method findByNomeContains String nome)*/

    //OBS: CONSULTAS JPQL USAM NOMES DE CLASSES E SEUS ATRIBUTOS.

    @Query("select u from Usuario u where u.nome = ?1") //dentro dele a gente coloca a consulta, SEM USAR o SQL, e sim usar o jpql.
    public List<Usuario> buscaUsuarioPorNome(String nome); //Por eu nao estar usando o padrao do spring data, ele nao vai gerar o metodo, para isso uso a @QUERY

    @Query("select u from Usuario u inner join u.autorizacoes a where a.nome =?1")
    public List<Usuario> buscaPorNomeAutorizacao(String autorizacao);
}