package br.com.wirizada.smartcondo_api.core.config;

import br.com.wirizada.smartcondo_api.core.tenant.TenantFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TenantFilter tenantFilter;

    public SecurityConfig(TenantFilter tenantFilter){
        this.tenantFilter = tenantFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/condominios").permitAll();
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
