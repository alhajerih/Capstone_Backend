package com.example.Shares.config;


import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.example.Shares.exception.UserNotFoundException;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.Shares.service.auth.CustomUserDetailsService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
public class JwtAuthFilter extends OncePerRequestFilter {

    /*
    The JwtAuthFilter class defines two instance variables: jwtUtil and userDetailsService. These are required for JWT (JSON Web Token) authentication. The class has a constructor that receives these dependencies, allowing them to be injected when the filter is created.
     */
    private static final String BEARER = "Bearer ";

    private final JWTUtil jwtUtil;

    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JWTUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }


    /*
    This method doFilterInternal is the heart of the filter. It is called for each HTTP request to process JWT authentication.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Here, we retrieve the Authorization header from the HTTP request, which is where the JWT token is typically included.
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        // This conditional statement checks several conditions before proceeding with JWT authentication. It ensures that the filter is not applied to the login endpoint (to avoid authentication loops) and that the Authorization header exists and starts with "Bearer ".
        if(!request.getServletPath().equals(SecurityConfig.AUTH_PATH + "/login") && authorizationHeader != null && authorizationHeader.startsWith(BEARER)){

            // If the conditions are met, this line extracts the token part from the Authorization header, removing the "Bearer " prefix.
            String token = authorizationHeader.substring(7);

            // Here, it checks if the extracted token is valid using the jwtUtil.isTokenValid(token) method. This method checks if the token is not expired and has a valid signature.
            if(jwtUtil.isTokenValid(token)){

                // If the token is valid, it extracts the username from the token using the jwtUtil.getUsernameFromToken(token) method. If the username is null, it throws a UserNotFoundException.
                String usernmae = jwtUtil.getUsernameFromToken(token);
                if (usernmae == null){
                    throw new UserNotFoundException("user not found");
                }

                // It loads user details (including roles and permissions) from the database using the userDetailsService. This is done by calling userDetailsService.loadUserByUsername(username).
                UserDetails userDetails = userDetailsService.loadUserByUsername(usernmae);


                // This line creates an Authentication object (UsernamePasswordAuthenticationToken) with the user details and authorities (roles and permissions) obtained from the database.
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                // It sets the authentication details, including the remote address and session ID, using WebAuthenticationDetailsSource.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Finally, it sets the authenticated Authentication object in the Spring Security SecurityContextHolder, indicating that the user is authenticated and authorized to access protected resources.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // This line allows the request to continue processing by passing it along the filter chain.
        filterChain.doFilter(request,response);
    }
}