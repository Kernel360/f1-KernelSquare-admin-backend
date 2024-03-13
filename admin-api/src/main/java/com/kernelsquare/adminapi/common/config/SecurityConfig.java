package com.kernelsquare.adminapi.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.kernelsquare.adminapi.common.filter.JWTSettingFilter;
import com.kernelsquare.adminapi.common.jwt.JWTAccessDeniedHandler;
import com.kernelsquare.adminapi.common.jwt.JWTAuthenticationEntryPoint;
import com.kernelsquare.adminapi.domain.auth.service.TokenProvider;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final TokenProvider tokenProvider;
	private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JWTAccessDeniedHandler jwtAccessDeniedHandler;

	private final String[] permitAllPatterns = new String[] {
		"/api/v1/auth/login",
		"/actuator",
		"/actuator/**",
	};

	private final String[] hasRoleAdminPatterns = new String[] {
		"/api/v1/techs/{techStackId}",
		"/api/v1/levels/**",
		"/api/v1/notices/**",
		"/api/v1/members/**",
		"/api/v1/images/**",
		"/api/v1/auth/reissue",
		"/api/v1/auth/logout",
		"/api/v1/questions/**",
		"/api/v1/search/**",
		"/api/v1/coffeechat/**",
		"/api/v1/hashtags/**",
		"/api/v1/techs/**"
	};

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//todo : filter 설정 추가하기
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());
		http.authorizeHttpRequests(authz -> authz
			// 모든 접근 허용
			.requestMatchers(permitAllPatterns).permitAll()

			// ROLE_ADMIN 권한 필요
			.requestMatchers(hasRoleAdminPatterns).hasRole("ADMIN")
		);

		http.addFilterBefore(new JWTSettingFilter(tokenProvider), BasicAuthenticationFilter.class);

		http.exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer
			.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			.accessDeniedHandler(jwtAccessDeniedHandler));

		http.sessionManagement(sessionManagementConfigurer ->
			sessionManagementConfigurer
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.logout(Customizer.withDefaults());

		return http.build();
	}
}
