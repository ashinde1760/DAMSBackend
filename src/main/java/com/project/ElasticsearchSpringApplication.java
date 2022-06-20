package com.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages="com.project.myjparepository")
@EnableElasticsearchRepositories(basePackages = "com.project.repository")
public class ElasticsearchSpringApplication {

	public static void main(String[] args) throws Exception {
		
		SpringApplication.run(ElasticsearchSpringApplication.class, args);
		
		}
}