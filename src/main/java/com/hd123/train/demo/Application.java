package com.hd123.train.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hd123.train.demo.infrastructure.DateTimeFormat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.text.SimpleDateFormat;

/**
 * @author Silent
 */
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  /** 处理跨域 */
  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.addAllowedOrigin("http://localhost:8080");
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.addAllowedMethod("*");
    corsConfiguration.setAllowCredentials(true);
    source.registerCorsConfiguration("/**", corsConfiguration);
    return new CorsFilter(source);
  }

  @Bean
  public StringHttpMessageConverter StringHttpMessageConverter() {
    return new StringHttpMessageConverter();
  }

  @Bean
  public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
    //设置日期格式
    ObjectMapper objectMapper = new ObjectMapper();
    SimpleDateFormat smt = new DateTimeFormat("yyyy-MM-dd HH:mm:ss");
    objectMapper.setDateFormat(smt);
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//反序列化时，解析不存在参数不报错
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);

    return mappingJackson2HttpMessageConverter;
  }
}
