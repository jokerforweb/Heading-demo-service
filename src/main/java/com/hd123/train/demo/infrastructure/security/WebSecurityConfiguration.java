package com.hd123.train.demo.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Silent
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  private Environment env;

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    String username = env.getProperty("demo-service.auth.username", "guest");
    String password = env.getProperty("demo-service.auth.password", "guest");
    auth.inMemoryAuthentication()
            .passwordEncoder(new BCryptPasswordEncoder())
            .withUser(username)
            .password(new BCryptPasswordEncoder().encode(password))
            .roles("USER");
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.csrf().disable().authorizeRequests()
            .antMatchers("/*/123/**").hasRole("USER")
            .and().sessionManagement()
            .and().logout()
            .and().httpBasic();
  }
}
