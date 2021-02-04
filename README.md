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
@RestController
@RequestMapping (value = "/usuario", method = RequestMethod.GET)

OUTRO EXEMPLO:

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

"Cross Origin Resource Sharing é uma especificação de uma tecnologia de navegadores que define meios para um servidor permitir que seus recursos sejam acessados por uma página web de um domínio diferente"

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

"Spring Security não suporta autenticação por JWT (JSON Web Token). Para incluir essa funcionalidade, foi preciso criar, manualmente, formas de gerar e receber esses tokens" - Professor Mineda

* Passo 1. Implementando um serviço que busca informações de usuário para autenticação. Essa busca foi feita no serviço existente UsuarioServiceImpl com a implementação do método loadUserByUsername:
```
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
        
```

Utilizo o repositório para buscar um usuário, qual nome ou e-mail seja igual ao buscado (tanto faz para login) e, caso encontremos, geramos uma instância da classe User do Spring Security:
```
...
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
```

Para que o Spring Security possa reconhecer esse método alteramos a interface UsuarioService, por isso ela estende a interface UserDetailsService.

* Passo 2: Usando @Autowired no atributo declarado com o tipo da interface UserDetailsService, carregando então, a implementação do serviço:
**Arquivo: Security > SecurityConfiguration.java
```
@Autowired
private UserDetailsService userDetailsService;
```
O segundo método configure indica ao Spring Security para utilizar a implementação do método que busca detalhes do usuário tentando autenticar. 
A tag @Bean disponibiliza, para uso em injeção com @Autowired, uma instância do AuthenticationManager, que realiza o processo de autenticação (verifica se usuário e senha estão corretos):
```
@Override
public void configure(AuthenticationManagerBuilder auth)
throws Exception {
auth.userDetailsService(userDetailsService);
}

@Bean
@Override
public AuthenticationManager authenticationManagerBean()
throws Exception {
return super.authenticationManagerBean();
}
```
* Passo 3: Incluir o atributo "token" em UsuarioDTO: 

```
public class UsuarioDTO {
    private String token;
```
* Passo 4: Criaçao da classe JwtUtils com os métodos para criar, validar e ler tokens JWT:

**A classe conta com um método de criação de token (generateToken):**
```
public static String generateToken(User usuario) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        UsuarioDTO usuarioSemSenha = new UsuarioDTO();
        usuarioSemSenha.setNome(usuario.getUsername());
        if (!usuario.getAuthorities().isEmpty()) {
            usuarioSemSenha.setAutorizacao(usuario.getAuthorities().iterator().next().getAuthority());
        }
```
**E um método para ler e validar tokens (parseToken):**
```
public static User parseToken(String token) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        String credentialsJson = Jwts.parser().setSigningKey(KEY).parseClaimsJws(token).getBody().get("userDetails",
                String.class);
        UsuarioDTO usuario = mapper.readValue(credentialsJson, UsuarioDTO.class);
        return (User) User.builder().username(usuario.getNome()).password("secret")
                .authorities(usuario.getAutorizacao()).build();
```
* Passo 5: Criação do serviço de login que possa retornar um JWT: 
**Criamos a classe "LoginController.java", onde injetamos (com @AutoWired) o AuthenticationManager configurado anteriormente:

```
public class LoginController {
    @Autowired
    private AuthenticationManager auth;
    @PostMapping(path = "/login")
    public UsuarioDTO login(@RequestBody UsuarioDTO login)
            throws JsonProcessingException {
        String username = login.getNome();
        if(username == null) {
            username = login.getEmail();
        }
        Authentication credentials = new UsernamePasswordAuthenticationToken(username, login.getSenha());
        User usuario = (User) auth.authenticate(credentials).getPrincipal();
        login.setAutorizacao((usuario.getAuthorities().toString()));
        login.setSenha(null);
        login.setToken(JwtUtils.generateToken(usuario));
        return login;
    }
}
```
Note que para acessar serviços protegidos, as requisições (seja qual for) têm que ter um token JWT com o nome "Authorization", aí que entra o passo seguinte:

* Passo 6: Criação do filtro Jwt (nome do arquivo: JwtAuthenticationFilter), esse filtro vai interceptar todas as requisições HTTP e lidar com elas antes que cheguem ao seu destino final (no caso, os serviços). Esse filtro faz assim: verifica se a requisição tem um header do tipo Authorization, se houver, o token do header é lido (pelo método generateToken dentro de JwtUtils) e validado. SE FOR VALIDADO, guess what? É feita a autenticação com o resto das informações do usuário. Cool, huh?

```
public class JwtAuthenticationFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest servletRequest = (HttpServletRequest) request;
            String authorization = servletRequest.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization != null) {
                User usuario = JwtUtils.parseToken(authorization.replaceAll("Bearer ", ""));
                Authentication credentials = new UsernamePasswordAuthenticationToken(usuario.getUsername(),
                        usuario.getPassword(), usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(credentials);
            }
            chain.doFilter(request, response);
        } catch (Throwable t) {
            HttpServletResponse servletResponse = (HttpServletResponse) response;
            servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, t.getMessage());
        }
```

- [X] Proteger recursos utilizando @annotations;

No caso do método de adicionar um novo livro, apenas usuários autenticados e que possuam as permissões ROLE_ADMIN ou ROLE_USER podem acessá-lo. 

* @PreAuthorize: 
```
                          ...
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')") 
    public Livro adicionarLivro(String identificadorUsuario, String titulo) {
        Usuario usuario = usuarioRepo.findTop1ByNomeOrEmail(
                identificadorUsuario, identificadorUsuario);
                          ...
```
Para proteger o método de criaçao de um novo usuário, limitando apenas aos Administradores (ROLE_ADMIN), anotei o método assim: 

```
                               ...
 @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Usuario novoUsuario(String nome, String email,
                               String senha, String nomeAutorizacao) {
                               ...
```

- [X] Nao anotar método algum no Controller;

**Todas as anotações de proteçao de método foram anotadas nos serviços.**

- [X] Usar no mínimo dois níveis de acesso (Usuário e Admin).

**A aplicação possui acesso tanto pelos Administradores (ROLE_ADMIN), quanto pelos os Usuários Comuns (ROLE_USER).**

### VUE.JS

- [X] Consumir rotas criadas no back.

* No arquivo **Livros.vue**, consumo a rota /livro/novo criada no back > Controller > LivroController.java
```
 methods: {
    cadastrar() {
      axios
        .post(
          '/livro/novo',
          {
            titulo: this.titulo,
            usuario: this.usuario
          })
```
* E a rota de busca também criada no back > Controller > LivroController.java: 

```
 atualizar() {
      axios.get('/livro/busca/' + this.usuario,
       { headers: { Accept: 'application/json' }
       })
```

- [X] Controle de estado com Vuex;

Essa é a parte do Vuex que controla o estado da aplicação. Este estado é imutável e único para toda a aplicação. Para começar a implementar o vuex na aplicação basta fazer um import e um use da biblioteca como no exemplo abaixo:

```
import Vuex from 'vuex'

Vue.use(Vuex)
```
EXPLICAR O RESTO AQUI
```
import { mapState } from 'vuex'
export default {
  name: 'Home',
  computed: {
    ...mapState(['role'])
  }
}
```
- [X] Várias rotas definidas (router);

Abaixo tenho minhas rotas de Login, Home e Livros. 
```
Vue.use(VueRouter);

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import("../views/Home.vue")
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import ('../views/Login.vue')
  },
  {
    path: '/livros',
    name: 'Livros',
    component: () => import ('../views/Livros.vue')
  }
];

```
- [X] Tratamento de erro em requisições (axios);

Em uma promessa JavaScript que é resultado de uma requisição AJAX, no then() recebemos respostas HTTP na casa de 2XX (200, 201, 204, 206, etc) e no catch(), recebemos status code na casa de 4XX (400, 404, 422) e 5xx (500, 502, 503), logo as verificações de erro devem ser feitas no catch(). Exemplo:

```
axios
        .post(
          '/livro/novo',
          {
            titulo: this.titulo,
            usuario: this.usuario
          })
        .then((res) => {
          console.log(res);
          this.titulo = "";
          this.atualizar();
        })
        .catch((error) => console.log(error))
```
- [X] E elementos visuais diferentes por nível de acesso.

**Arquivo: Home.vue**, neste arquivo, eu faço uso da diretiva "v-if", ou seja, se o estado do 'role' for ROLE_ADMIN, ou se for ROLE_USER, meu componente html se comportará de forma diferente.
```
<template>
  <div class="text-center">
    <h1> BOOKWORM </h1>
  <h2  v-if='role === "[ROLE_ADMIN]"'> BEM VINDO, ADMIN </h2>  
<img v-if='role === "[ROLE_ADMIN]"' src="https://media.giphy.com/media/gfO1UMUVlM6i1BmKD2/giphy.gif" width="50%" height="50%">
  <h2  v-if='role === "[ROLE_USER]"'> BEM VINDO, USER</h2> 

  </div>
</template>

<script>
import { mapState } from 'vuex'
export default {
  name: 'Home',
  computed: {
    ...mapState(['role']) //Para se acessar um atributo do state a partir de um componente é preciso utilizar atributos computados (mapState)
    //Inclusive, aqui em computed, eu poderia passar um vetor cheio de atributos que eu gostaria de mapear, por exemplo, se eu quisesse mapear o token e o usuario, eu poderia.
  }
}
</script>

```
**Index.js**
* State: A seção state de uma store Vuex contém atributos acessíveis a toda a aplicação.

* Actions: as ações são semelhantes às mutações, as diferenças são as seguintes: Em vez de mudar o estado, as ações confirmam (ou fazem commit de) mutações.
Os manipuladores de ação recebem um objeto context que expõe o mesmo conjunto de métodos/propriedades na instância do store, para que você se possa chamar context.commit para confirmar uma mutação ou acessar o estado através do context.state. Podemos considerar actions como a camada de serviços da aplicação.

* Mutations: Assim como getters, as mutations recebem o state como parâmetro, mas podem receber um parâmetro extra, passado manualmente em sua chamada. Mutations commitam " alterações. Não é possível realizar operações assíncronas (como requisições HTTP) dentro de mutations.
```
Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: null,
    usuario: null,
    role: null
  },
  mutations: {
    setUsuario(state, usuario){
      state.usuario = usuario;
    },
    setToken(state, token){
      state.token = token;
    },
    setRole(state, role){
      state.role = role;
    },
    logout(state){
      state.token = null;
      state.usuario = null;
    }
  },
  actions: {
    login(context, { usuario, senha }){
      axios
      .post("login", {
        nome: usuario, 
        senha: senha
      })
      .then(res => {
        console.log(res);
        context.commit('setUsuario', usuario);
        context.commit('setToken', res.data.token);
        context.commit('setRole', res.data.autorizacao);
        console.log(res.data.autorizacao)
        router.push('/');
      }).catch(error => console.log(error));
    }
  },
  modules: {
  }
})

```
Dada a explicação acima, temos visualmente os seguintes resultados:

**Quando o usuário é autenticado como ADMIN, a home page fica assim:**
<img src="https://user-images.githubusercontent.com/45819790/106807100-84b38200-6647-11eb-88a1-9bdad6669895.png" alt="Admin" style=max-width:100%>

**Quando o usuário é autenticado como USUÁRIO COMUM, a home page fica assim:**
<img src="https://user-images.githubusercontent.com/45819790/106807921-7ade4e80-6648-11eb-8fde-dd548fb05cc0.png" alt="UsuárioComum" style=max-width:100%>

