package com.bank.javabankapp;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition (
		info = @Info(
				title = "The Java Bank App",
				description = "Building a Demo Banking Application, backend REST API",
				version = "v1.0",
				contact = @Contact(
						name = "Marko Djokic",
						email = "markojh13@gmail.com",
						url = "https://github.com/marko-cpu/"
				),
				license = @License(
						name = "Java Projects",
						url = "https://github.com/marko-cpu/"
				)
		),
		externalDocs = @ExternalDocumentation(
				description = "Java Bank App Documentation",
				url = "https://github.com/marko-cpu/"
		)
)
public class JavaBankAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaBankAppApplication.class, args);
	}

}
