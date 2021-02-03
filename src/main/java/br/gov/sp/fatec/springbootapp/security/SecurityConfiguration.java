package br.gov.sp.fatec.springbootapp.security;

import br.gov.sp.fatec.springbootapp.utils.JwtAuthenticationFilter;
import org.apache.tomcat.jni.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity //realiza a configuração básica de segurança.
@EnableGlobalMethodSecurity(prePostEnabled = true) //Habilita o uso de anotações (@PreAuthorize e @PostAuthorize) para limitar o acesso a métodos.
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService; /*Criado o método loadbyusername (UsuarioServiceImpl.java), configuramos o Spring Security para utilizá-lo com a anotacao @autowired,*/


    //No metodo abaixo, desabilitamos o uso de sessão (STATELESS)
    // para armazenamento de dados entre requisições e desabilitamos
    // a proteção CSRF (Cross Site Request Forgery), que será desnecessária
    // para esse projeto (usaremos uma proteção própria futuramente).
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) /*Incluímos uma instância de nosso filtro (jwt authenticationfilter) antes de todos os filtros do Spring Security, de forma a garantir que ele enxergue o usuário que autenticarmos por meio de JWT.*/
// this disables session creation on Spring Security
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) /*indica ao Spring Security para utilizar
                                                            nossa implementação do método que busca detalhes
                                                            do usuário tentando autenticar.*/
            throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Bean /*tag @Bean disponibiliza, para uso em injeção com @Autowired,
     uma instância do AuthenticationManager, que realiza o processo de
     autenticação (verifica se usuário e senha estão corretos).*/
    @Override
    public AuthenticationManager authenticationManagerBean()
            throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoderBean() { //configurando uma codificação padrão de senha, BCrypt
        return new BCryptPasswordEncoder();
    }
}
