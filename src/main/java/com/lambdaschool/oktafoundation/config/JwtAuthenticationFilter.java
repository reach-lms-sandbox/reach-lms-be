package com.lambdaschool.oktafoundation.config;


import com.lambdaschool.oktafoundation.models.User;
import com.lambdaschool.oktafoundation.models.UserRoles;
import com.lambdaschool.oktafoundation.repository.UserRepository;
import com.lambdaschool.oktafoundation.services.RoleService;
import com.lambdaschool.oktafoundation.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


@Component
public class JwtAuthenticationFilter
		extends OncePerRequestFilter {

	// A very specific implementation of `userRepository.findByUserName` was needed for this filter,
	// so the repository is used directly.
	@Autowired
	UserRepository userRepository;

	// Using the service is preferred so for save, the service is used
	@Autowired
	UserService userService;

	/**
	 * A method in this controller adds a new user to the application with the role User so needs access to Role Services to do this.
	 */
	@Autowired
	private RoleService roleService;

	@Override
	protected void doFilterInternal(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse,
			FilterChain filterChain
	)
	throws ServletException, IOException {
		// find the username of the authenticated user
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();

		// if we have an authenticated context, we'll enter into this statement
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			User workingUser = userRepository.findByUsername(authentication.getName());

			// if we don't yet have this user, we should make one
			if (workingUser == null) {
				workingUser = new User(authentication.getName());
				// adds a default ADMIN role to this new user... should we default to ADMIN?
				Set<UserRoles> newRoles = new HashSet<>();
				newRoles.add(new UserRoles(workingUser, roleService.findByName("ADMIN")));
				workingUser.setRoles(newRoles);
				workingUser = userService.save(workingUser);
			}

			// Forcing authentication to recognize the BE authorities not Okta's.
			Authentication newAuth = new UsernamePasswordAuthenticationToken(authentication.getName(),
					authentication.getCredentials(),
					workingUser.getAuthority()
			);

			SecurityContextHolder.getContext()
					.setAuthentication(newAuth);

			// continue the filter chain.
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

}
