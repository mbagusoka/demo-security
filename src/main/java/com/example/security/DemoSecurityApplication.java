package com.example.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@SpringBootApplication
public class DemoSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSecurityApplication.class, args);
    }

}

@RestController
class HomeResource {

    @GetMapping("/")
    public String home() {
        return "<h1>Welcome</h1>";
    }

    @GetMapping("/user")
    public String user() {
        return "<h1>Welcome User</h1>";
    }

    @GetMapping("/admin")
    public String admin() {
        return "<h1>Welcome Admin</h1>";
    }
}

@EnableWebSecurity
@RequiredArgsConstructor
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
            .dataSource(dataSource)
            .usersByUsernameQuery(
                "select username, password, enabled from users where username = ?"
            )
            .authoritiesByUsernameQuery(
                "select username, authority from authorities where username = ?"
            );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/admin").hasRole("ADMIN")
            .antMatchers("/user").hasAnyRole("ADMIN", "USER")
            .antMatchers("/")
            .permitAll()
            .and().formLogin();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
