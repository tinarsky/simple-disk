package com.github.tinarsky.simpledisk.controllers;

import com.github.tinarsky.simpledisk.domain.SystemItem;
import com.github.tinarsky.simpledisk.domain.SystemItemHistoryUnit;
import com.github.tinarsky.simpledisk.models.SystemItemType;
import com.github.tinarsky.simpledisk.repos.SystemItemHistoryUnitRepo;
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

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(initializers = {NodeHistoryTests.Initializer.class})
@AutoConfigureMockMvc
@Testcontainers
class NodeHistoryTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private SystemItemHistoryUnitRepo historyUnitRepo;

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
		setUpElementsInDb();

		var requestBuilder =
				get("/node/element3/history")
				.param("dateStart", "2022-06-05T21:12:06.000Z")
				.param("dateEnd", "2022-06-06T21:12:06.000Z");

		mockMvc.perform(requestBuilder)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.items[0].id", is("element3")))
				.andExpect(jsonPath("$.items[1]").doesNotExist())
				.andExpect(jsonPath("$.items[0].date", is("2022-06-05T21:12:06Z")));
	}

	@Test
	public void validationFailed() throws Exception {
		setUpElementsInDb();

		var requestBuilder =
				get("/node/element3/history")
						.param("dateStart", "2022_06_05T21:12:06.000Z")
						.param("dateEnd", "2022-06-05T21:12:06.000Z");

		mockMvc.perform(requestBuilder)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.code", is(400)))
				.andExpect(jsonPath("$.message").exists());
	}

	@Test
	public void NotFound() throws Exception {
		setUpElementsInDb();

		var requestBuilder =
				get("/node/element0/history")
						.param("dateStart", "2022-06-05T21:12:06.000Z")
						.param("dateEnd", "2022-06-05T21:12:06.000Z");

		mockMvc.perform(requestBuilder)
				.andDo(print())
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.code", is(404)))
				.andExpect(jsonPath("$.message").exists());
	}

	private void setUpElementsInDb() {
		SystemItem element1 = new SystemItem.Builder()
				.withId("element1")
				.byUrl("some/url")
				.hasParentById("element2")
				.hasType(SystemItemType.FILE)
				.size(10L)
				.updatedIn(Instant.parse("2022-06-05T21:12:06.000Z"))
				.build();

		SystemItem element2 = new SystemItem.Builder()
				.withId("element2")
				.byUrl(null)
				.hasParentById("element3")
				.hasType(SystemItemType.FOLDER)
				.size(null)
				.updatedIn(Instant.parse("2022-06-05T21:12:06.000Z"))
				.build();
		element2.getChildren().add(element1);

		SystemItem element3 = new SystemItem.Builder()
				.withId("element3")
				.byUrl(null)
				.hasParentById(null)
				.hasType(SystemItemType.FOLDER)
				.size(null)
				.updatedIn(Instant.parse("2022-06-05T21:12:06.000Z"))
				.build();

		element3.getChildren().add(element2);
		element2.setSize(10L);
		element3.setSize(10L);

		systemItemRepo.saveAll(List.of(element1, element2, element3));

		historyUnitRepo.saveAll(List.of(
				new SystemItemHistoryUnit(element1),
				new SystemItemHistoryUnit(element2),
				new SystemItemHistoryUnit(element3)));

		element1.setSize(20L);
		element2.setSize(20L);
		element3.setSize(20L);
		element1.setDate(Instant.parse("2022-06-06T21:12:06.000Z"));
		element2.setDate(Instant.parse("2022-06-06T21:12:06.000Z"));
		element3.setDate(Instant.parse("2022-06-06T21:12:06.000Z"));

		systemItemRepo.saveAll(List.of(element1, element2, element3));

		historyUnitRepo.saveAll(List.of(
				new SystemItemHistoryUnit(element1),
				new SystemItemHistoryUnit(element2),
				new SystemItemHistoryUnit(element3)));
	}
}

