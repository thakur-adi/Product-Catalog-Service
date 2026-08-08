package dev.aditya.productcatalogservice.Security;

import dev.aditya.productcatalogservice.Exception.CustomAuthorizationException;
import dev.aditya.productcatalogservice.Validation.Validation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class AuthFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("LoadBalancedRestTemplate")
    RestTemplate restTemplate;

    @Autowired
    CustomAuthEntryPoint customAuthEntryPoint;

    private final String authURL = "http://User-Auth-Service/user/validate";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Only hit this code when authentication and authorization is actually required.
        if(!(request.getServletPath().equalsIgnoreCase("/search")) && !(request.getMethod().equalsIgnoreCase(HttpMethod.GET.toString()))) {

            String authToken = request.getHeader(HttpHeaders.AUTHORIZATION);// This needs to be collected and then passed forward otherwise it dies here.

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, authToken);

            HttpEntity<Void> requestHeaderEntity = new HttpEntity<>(headers);

            ResponseEntity<String> authenticatedResponse = restTemplate.postForEntity(authURL,requestHeaderEntity,String.class);

            if(authenticatedResponse.getStatusCode().is2xxSuccessful()) {
                if (!Validation.checkAuthorization(authenticatedResponse.getHeaders().getFirst("X-USER-ROLES"))) {
                    customAuthEntryPoint.commence(request, response, new CustomAuthorizationException("You are not authorized to perform this task!!"));
                }
                else{
                    //This here tells spring that the request is authenticated, So every filter needs this to pass the request which need to be authenticated.
                    UsernamePasswordAuthenticationToken authenticationToken =
                            UsernamePasswordAuthenticationToken.authenticated(authenticatedResponse.getHeaders().getFirst("X-USER-ID"),
                                    null,
                                    AuthorityUtils.createAuthorityList(authenticatedResponse.getHeaders().getFirst("X-USER-ROLES")));

                    SecurityContext newContext = SecurityContextHolder.createEmptyContext();
                    newContext.setAuthentication(authenticationToken);
                    SecurityContextHolder.setContext(newContext);

                    //Only go ahead if authorized otherwise it should hit entry point and stop execution
                    filterChain.doFilter(request, response);
                }
            }
            else{
               customAuthEntryPoint.commence(request, response, new CustomAuthorizationException("Authentication failed!! Possible Theft!"));
            }
        }
        else{
            //This should be under else, otherwise every request will including authorization failure one, and proper error message won't be shown.
            filterChain.doFilter(request, response);}
    }
}
