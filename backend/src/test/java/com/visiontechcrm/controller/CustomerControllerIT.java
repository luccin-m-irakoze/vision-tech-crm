package com.visiontechcrm.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visiontechcrm.dto.CustomerDto;
import com.visiontechcrm.dto.LoginRequest;
import com.visiontechcrm.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests for {@link CustomerController}.
 *
 * <p>Boots the full Spring context against an H2 in-memory database (see
 * {@code application-test.properties}), authenticates as the seeded admin to
 * obtain a JWT, and then exercises every CRUD operation through {@link MockMvc}
 * — exactly the way the Vue.js frontend talks to the backend.
 *
 * <p>Covers Test Plan §3 (Integration), TC-03 (create), TC-04 (update),
 * TC-05 (delete), TC-06 (search) and TC-19 (404 on missing resource).
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CustomerControllerIT {

    @Autowired private WebApplicationContext context;
    @Autowired private FilterChainProxy springSecurityFilterChain;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CustomerRepository customerRepository;

    private MockMvc mockMvc;
    private static String bearer;
    private static Long createdId;

    private MockMvc mvc() {
        if (mockMvc == null) {
            mockMvc =
                    MockMvcBuilders.webAppContextSetup(context)
                            .addFilter(springSecurityFilterChain)
                            .build();
        }
        return mockMvc;
    }

    /** Logs in once as the seeded admin and caches the JWT for subsequent calls. */
    private String token() throws Exception {
        if (bearer != null) {
            return bearer;
        }
        LoginRequest req = new LoginRequest();
        req.setEmail("admin@visiontech.rw");
        req.setPassword("Admin@123");

        MvcResult result =
                mvc().perform(
                                post("/api/auth/login")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(req)))
                        .andExpect(status().isOk())
                        .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        bearer = "Bearer " + body.get("token").asText();
        return bearer;
    }

    @Test
    @Order(1)
    @DisplayName("GET /api/customers without JWT returns 401 (security wired correctly)")
    void list_withoutToken_returns401() throws Exception {
        mvc().perform(get("/api/customers")).andExpect(status().isUnauthorized());
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/customers with JWT returns the four seeded customers")
    void list_withToken_returnsSeed() throws Exception {
        mvc().perform(get("/api/customers").header("Authorization", token()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(4))));
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/customers creates a new customer and persists it (TC-03)")
    void create_persistsCustomer() throws Exception {
        CustomerDto dto =
                CustomerDto.builder()
                        .companyName("Acme Ltd")
                        .industry("IT")
                        .contactPerson("Wile E. Coyote")
                        .email("acme@example.rw")
                        .phone("+250788999000")
                        .address("Kigali")
                        .build();

        MvcResult result =
                mvc().perform(
                                post("/api/customers")
                                        .header("Authorization", token())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(dto)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.companyName", is("Acme Ltd")))
                        .andExpect(jsonPath("$.industry", is("IT")))
                        .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        createdId = body.get("id").asLong();

        // Confirm in the DB as well — proves the row was actually persisted, not just echoed.
        assertThat(customerRepository.findById(createdId)).isPresent();
    }

    @Test
    @Order(4)
    @DisplayName("PUT /api/customers/{id} updates an existing customer (TC-04)")
    void update_modifiesIndustry() throws Exception {
        CustomerDto patch =
                CustomerDto.builder()
                        .companyName("Acme Ltd")
                        .industry("Banking")
                        .email("acme@example.rw")
                        .build();

        mvc().perform(
                        put("/api/customers/" + createdId)
                                .header("Authorization", token())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.industry", is("Banking")));

        assertThat(customerRepository.findById(createdId))
                .get()
                .extracting("industry")
                .isEqualTo("Banking");
    }

    @Test
    @Order(5)
    @DisplayName("GET /api/customers?q=bank returns the matching customers (TC-06)")
    void search_byPartialName_filters() throws Exception {
        mvc().perform(
                        get("/api/customers")
                                .header("Authorization", token())
                                .param("q", "bank"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].companyName").exists());
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/customers/{unknown} returns 404 with ErrorResponse body (TC-19)")
    void get_missingId_returns404() throws Exception {
        mvc().perform(
                        get("/api/customers/99999")
                                .header("Authorization", token()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(7)
    @DisplayName("DELETE /api/customers/{id} removes the customer (TC-05)")
    void delete_removesCustomer() throws Exception {
        mvc().perform(
                        delete("/api/customers/" + createdId)
                                .header("Authorization", token()))
                .andExpect(status().isNoContent());

        assertThat(customerRepository.findById(createdId)).isEmpty();
    }
}
