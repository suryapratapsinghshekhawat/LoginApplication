package com.jwtAuthentication.config;

import com.jwtAuthentication.helper.JwtUtil;
import com.jwtAuthentication.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

          // get Jwt
         //bearer
          //validation

        String requestTokenHeader = request.getHeader("Authorization");
        String username=null;
        String jwtToken=null;

        // null & format check
        if(requestTokenHeader!=null && requestTokenHeader.startsWith("Bearer ")){

               jwtToken = requestTokenHeader.substring(7);
               try{
                  username= this.jwtUtil.getUsernameFromToken(jwtToken);

               }catch(Exception e){
                   e.printStackTrace();
               }
               UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);
//            System.out.println(username);
               //validation
            if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            else{
                System.out.println("Token is not validate!!!");
            }


        }
        filterChain.doFilter(request,response);


    }
}
