package org.javacoders.messenger_03.security;

import java.io.IOException;

import org.javacoders.messenger_03.services.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	private UserDetailsService userDetailsService;
	private JwtTokenHelper jwtTokenHelper;
	private final UserService userService;
	
	public JwtAuthenticationFilter(UserDetailsService userDetailsService, JwtTokenHelper jwtTokenHelper,
			UserService userService) {
		this.userDetailsService = userDetailsService;
		this.jwtTokenHelper = jwtTokenHelper;
		this.userService = userService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String requestToken = this.userService.extractCookieFromRequest(request, "jwtToken");
		
		String username = null;
		
		if(requestToken != null && !requestToken.isEmpty()) {
			
			try {
				username = this.jwtTokenHelper.getUsernameFromToken(requestToken);
				
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get Jwt token");
				e.printStackTrace();
			} catch (ExpiredJwtException e) {
				System.out.println("Jwt Token has expired");
				e.printStackTrace();
			} catch (MalformedJwtException e) {
				System.out.println("Invalid Jwt Token");
				e.printStackTrace();
			}
		} else {
			System.out.println("JwtToken does not start with Bearer");
		}
		
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
			
			if(this.jwtTokenHelper.validateToken(requestToken, userDetails)) {
				UsernamePasswordAuthenticationToken authToken = 
						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource()
						.buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authToken);
			} else {
				System.out.println("Invalid Jwt token");
			}
		} else {
			System.out.println("Username is null or context is not null");
		}
		
		filterChain.doFilter(request, response);
	}
	
}
