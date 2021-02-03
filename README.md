[![Gitpod Ready-to-Code](https://img.shields.io/badge/Gitpod-Ready--to--Code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/santosdeboracristina/aulaminedarepositorio)

#### Aplicação de Livros com Spring
Para rodar utilize: mvn spring-boot:run

## MUST DO: 
### Spring Boot + Spring Data JPA
- [X] Aplicação com acesso ao BD relacional utilizando JPA;
O Spring Data JPA é um framework que nasceu para facilitar a tarefa de persistir dados com JPA.
Arquivo: pom.xml > Adicionando dependência do Spring Data JPA
```
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
<dependency>
  ```
- [X] Possuir no mínimo duas entidades mapeadas e relacionadas de forma bidirecional;
* As entidades criadas dentro da pasta Entity: **Livro, Usuario e Autorizacao**
* Relacionamentos criados na entidade Usuario: 
@ManytoMany: Um usuário pode ter mais de uma autorização e uma determinada autorizaçao pode pertercer a vários usuários.
```
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(name = "uau_usuario_autorizacao",
    joinColumns = { @JoinColumn(name="usr_id") }, 
    inverseJoinColumns = { @JoinColumn(name = "aut_id")}) 
private Set<Autorizacao> autorizacoes;
 ```
 e @OneToMany: Um usuário pode criar vários Livros e um livro só pode ser criado por um único usuário.
 ```
 @OneToMany(fetch = FetchType.LAZY, mappedBy = "autor")
 private Set<Livro> livros;
 ```
 * Relacionamento criado na entidade Livro: @ManyToOne
 ```
  @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn (name = "usr_autor_id")
    @JsonView(View.LivroCompleto.class)
    private Usuario autor;
 ```
- [X] Criar repositórios;

* AutorizaçãoRepository:
```
public interface AutorizacaoRepository extends JpaRepository<Autorizacao,Long> {
//Consulta que busca uma Autorização pelo Nome.
    public Autorizacao findByNome(String nome);
}
```
* Livro Repository:
```
//Busca todas as frases de um Usuário com determinado Nome ou Endereço de E-mail.
public interface LivroRepository extends JpaRepository<Livro,Long> {
    //A anotacao PreAuthorize, pede para que o usuario esteja autenticado
    //Aqui Utilizo mais de um parametro para consulta.

    @PreAuthorize("isAuthenticated()")
    public List <Livro> findByAutorNomeOrAutorEmail (String nome, String email);
}
```
* UsuárioRepository: 
```
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{ 

    /*O Repositório da entidade Usuario, possui uma consulta que busca e tras O PRIMEIRO REGISTRO de um Usuário pelo Nome ou Endereço de E-mail. */
    public Usuario findTop1ByNomeOrEmail (String nome, String email);

    /*E a anotacao @Query faz a consulta buscando todos os usuários que possuem o texto contido no parâmetro "nome" em qualquer lugar de seu atributo nome e é equivalente ao query method findByNomeContains String nome)*/

    //OBS: CONSULTAS JPQL USAM NOMES DE CLASSES E SEUS ATRIBUTOS E NAO AS TABELAS DO BANCO DE DADOS.

    @Query("select u from Usuario u where u.nome = ?1") //dentro dele a gente coloca a consulta, SEM USAR o SQL, e sim usar o jpql.
    public List<Usuario> buscaUsuarioPorNome(String nome); //Por eu nao estar usando o padrao do spring data, ele nao vai gerar o metodo, para isso uso a @QUERY

    @Query("select u from Usuario u inner join u.autorizacoes a where a.nome =?1")
    public List<Usuario> buscaPorNomeAutorizacao(String autorizacao);
}
```
- [X] Consultas com mais de um parâmetro e com duas entidades (join);

Mais de um parâmetro: LivroRepository
```
public List <Livro> findByAutorNomeOrAutorEmail (String nome, String email);
```
Duas entidades (join): UsuarioRepository
```
@Query("select u from Usuario u inner join u.autorizacoes a where a.nome =?1")
    public List<Usuario> buscaPorNomeAutorizacao(String autorizacao);
```
- [X] Proteger um método que leia e escreva no BD com a anotação @Transactional.
```
@Override
@Transactional //PROTEÇÃO UTILIZANDO TRANSAÇÃO
@PreAuthorize("hasRole('ROLE_ADMIN')")
public Usuario novoUsuario(String nome, String email, String senha, String nomeAutorizacao) {
        Autorizacao autorizacao = autorizacaoRepo.findByNome(nomeAutorizacao); //MÉTODO DE LEITURA
        if(autorizacao == null) {
            autorizacao = new Autorizacao();
            autorizacao.setNome(nomeAutorizacao);
            autorizacaoRepo.save(autorizacao);
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passEncoder.encode(senha)); 
        usuario.setAutorizacoes(new HashSet<Autorizacao>());
        usuario.getAutorizacoes().add(autorizacao);
        usuarioRepo.save(usuario); //MÉTODO DE ESCRITA
        return usuario;
```
### REST
- [X] Rotas REST com múltiplos métodos (GET, POST, etc);
 ```
   @PostMapping(value = "/novo") //Define que o método a seguir é um serviço que responde a requisições do tipo POST.
    // O parâmetro "value" define o URI específico desse serviço. Seguindo o exemplo, seria "http://localhost:8080/livros/usuario/novo";
    public Usuario cadastrarUsuario(@RequestBody UsuarioDTO usuario) { //Essa anotação @RequestBody indica que os parametros viram como um Json no body da requisicao.
        // e indica que ele deve ser retirado do "corpo" da requisição.
        // Nesse caso, os dados do usuário devem vir em um JSON com formato compatível à classe UsuarioDTO
        //O retorno do método será um JSON gerado a partir da classe Usuario (retorno do método novoUsuario do serviço UsuarioService).
        return usuarioService.novoUsuario(
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getAutorizacao());
    }
 ```
- [X] Formatar o resultado utilizando @JsonView;
Formatando o resultado da requisição utilizando @JsonView:

```
@Id 
@GeneratedValue(strategy = GenerationType.IDENTITY) // n posso receber o id, ele eh auto increment
@Column(name = "usr_id") // mapeando a tabela do banco de dados
@JsonView(View.LivroCompleto.class)
private Long id;

@Column(name = "usr_nome", unique=true, length=20, nullable=false)
@JsonView(View.LivroResumo.class)
private String nome;
```
Note que: Onde utilizei a anotação @ JsonView, foram os atributos que queria serializar (que aparecesse no corpo da resposta JSON).
* O atributo "id" foi serializado quando a classe de View escolhida foi View.LivroCompleto
* O atributo "nome" foi serializado quando a classe de View escolhida foi View.LivroResumo.

- [X] Habilitar o tratamento de CORS.
" Cross Origin Resource Sharing é uma especificação de uma tecnologia de navegadores que define meios para um servidor permitir que seus recursos sejam acessados por uma página web de um domínio diferente"

Basicamente impede que uma rota em um servidor seja acessada por uma página (ou JavaScript) de outro servidor, no meu caso, no frontend VUE quando eu tentava acessar a rota "/livro" do backend, sem anotar com @CrossOrigin eu obtia esses erros: 

<img src="https://user-images.githubusercontent.com/45819790/106797034-e3263380-663a-11eb-8ed7-e2e2819041e0.png" alt="Fluxograma" style=max-width:100%>

Erro desapareceu ao anotar minhas rotas com @CrossOrigin, como mostra o trecho de código abaixo: 
```
@RestController
@CrossOrigin
public class LoginController {
    @Autowired
    private AuthenticationManager auth;
    @PostMapping(path = "/login")
    public UsuarioDTO login(@RequestBody UsuarioDTO login)
    .
    .
    .
```
### SEGURANÇA
- [X] Incluir JWT (token);
- [X] Proteger recursos utilizando @annotations;
- [X] Nao anotar método algum no Controller);
- [X] Usar no mínimo dois níveis de acesso (Usuário e Admin).

### VUE.JS
- [X] Consumir rotas criadas no back.
- [X] Conter: Controle de estado com Vuex;
- [X] Várias rotas definidas (router);
- [X] Tratamento de erro em requisições (axios);
- [X] E elementos visuais diferentes por nível de acesso.
