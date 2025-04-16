package com.analistas.amesistema.amesistema.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

	@Autowired
	DataSource dataSource; // encapsula la conexion con la base de datos en la variable dataSource

	@Bean
	BCryptPasswordEncoder getEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests((requests) -> requests
						.requestMatchers("/", "/home", "/productos/listado", "/register-form", "/clientes/nuevo",
								"/clientes/guardar", "/img/**", "/css/**", "/js/**", "/fonts/**")
						.permitAll()
						.anyRequest().authenticated()

				)
				.formLogin((form) -> form
						.loginPage("/login")
						.defaultSuccessUrl("/home", true)
						.permitAll())
				.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.logoutSuccessUrl("/?logout=true")
						.permitAll());

		return http.build();
	}


	@Autowired
	public void configGlobal(AuthenticationManagerBuilder builder) throws Exception {
		// Autenticación con JDBC para usuarios
		builder
				.jdbcAuthentication()
				.dataSource(dataSource)
				.usersByUsernameQuery(
						"select nombre as username, clave, activo " +
								"from usuarios " +
								"where nombre = ?")
				.authoritiesByUsernameQuery(
						"select u.nombre as username, p.nombre as authority " +
								"from permisos p " +
								"inner join usuarios u on u.id_permiso = p.id " +
								"where u.nombre = ?");

		// Autenticación con JDBC para CLIENTES
		builder
				.jdbcAuthentication()
				.dataSource(dataSource)
				.usersByUsernameQuery(
						"select dni as username, clave, activo " +
								"from clientes " +
								"where dni = ?")
				.authoritiesByUsernameQuery(
						"select c.dni as username, p.nombre as authority " +
								"from permisos p " +
								"inner join clientes c on c.id_permiso = p.id " +
								"where c.dni = ?");
	}
}