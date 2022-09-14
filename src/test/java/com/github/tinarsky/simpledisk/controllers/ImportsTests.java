package com.github.tinarsky.simpledisk.controllers;

import com.github.tinarsky.simpledisk.repos.SystemItemRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(initializers = {ImportsTests.Initializer.class})
@AutoConfigureMockMvc
@Testcontainers
class ImportsTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SystemItemRepo systemItemRepo;

	@Container
	public static PostgreSQLContainer<?> postgreSQLContainer =
			new PostgreSQLContainer<>("postgres:12.12-alpine")
					.withDatabaseName("mydb")
					.withUsername("myuser")
					.withPassword("mypass");

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues.of(
					"spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
					"spring.datasource.username=" + postgreSQLContainer.getUsername(),
					"spring.datasource.password=" + postgreSQLContainer.getPassword()
			).applyTo(configurableApplicationContext.getEnvironment());
		}
	}

	@Test
	public void success() throws Exception {
		String json;
		URI jsonFileUri = getClass().getResource("/imports/correctImport.json").toURI();
		try (var in = Files.newBufferedReader(Path.of(jsonFileUri))) {
			json = in.lines().collect(Collectors.joining());
		}

		var requestBuilder = post("/imports")
				.contentType("application/json")
				.content(json);

		mockMvc.perform(requestBuilder)
				.andDo(print())
				.andExpect(status().isOk());

		assertEquals(systemItemRepo.findAll().size(), 6);
		assertTrue(systemItemRepo.findById("element3").isPresent());
		assertTrue(systemItemRepo.findById("element0").isEmpty());
	}

	@Test
	public void validationFailed() throws Exception {
		String json;
		URI jsonFileUri = getClass().getResource("/imports/incorrectImport.json").toURI();
		try (var in = Files.newBufferedReader(Path.of(jsonFileUri))) {
			json = in.lines().collect(Collectors.joining());
		}

		var requestBuilder = post("/imports")
				.contentType("application/json")
				.content(json);

		mockMvc.perform(requestBuilder)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(400)))
				.andExpect(jsonPath("$.message").exists());
	}
}

