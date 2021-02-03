package br.gov.sp.fatec.springbootapp.utils;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.GenericFilterBean;

/*Para acessar serviços protegidos, as requisições devem contar com um token JWT no header de nome Authorization.
 Nós também precisamos criar uma infraestrutura para cuidar desse tipo de header, visto que ele não é suportado
 pelo Spring Security. Para tanto, utilizaremos um filtro, uma classe capaz de interceptar todas as requisições HTTP
 e tratá-las antes que cheguem a seu destino (serviços). Nosso filtro é apresentado em Código 7.
 Ele verifica se a requisição possui um header do tipo Authorization (HttpHeaders.AUTHORIZATION).
 Se o header faz parte da requisição, o token contido é validado e lido, por meio do método parse de JwtUtils.
 Por padrão, o token pode vir precedido prefixo "Bearer " e,
por esse motivo, o removemos. Caso o token seja validado (não gere nenhuma Exception),
 recolhemos as informações do usuário e realizamos e realizamos uma autenticação com elas.*/

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
    }
}