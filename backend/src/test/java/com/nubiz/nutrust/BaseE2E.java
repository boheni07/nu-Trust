package com.nubiz.nutrust;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nubiz.nutrust.entity.Company;
import com.nubiz.nutrust.entity.NotificationLog;
import com.nubiz.nutrust.entity.ProcessingPlan;
import com.nubiz.nutrust.entity.Project;
import com.nubiz.nutrust.entity.Role;
import com.nubiz.nutrust.entity.Ticket;
import com.nubiz.nutrust.entity.User;
import com.nubiz.nutrust.entity.UserStatus;
import com.nubiz.nutrust.repository.CompanyRepository;
import com.nubiz.nutrust.repository.NotificationLogRepository;
import com.nubiz.nutrust.repository.ProcessingPlanRepository;
import com.nubiz.nutrust.repository.ProjectRepository;
import com.nubiz.nutrust.repository.RoleRepository;
import com.nubiz.nutrust.repository.TicketRepository;
import com.nubiz.nutrust.repository.UserRepository;

import java.time.LocalDate;
import java.util.Set;
import com.nubiz.nutrust.entity.Notice;
import com.nubiz.nutrust.entity.Holiday;
import com.nubiz.nutrust.entity.ExtensionRequest;
import com.nubiz.nutrust.repository.NoticeRepository;
import com.nubiz.nutrust.repository.HolidayRepository;
import com.nubiz.nutrust.repository.ExtensionRequestRepository;
import com.nubiz.nutrust.security.JwtTokenProvider;
import com.nubiz.nutrust.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@TestPropertySource(properties = {
    "spring.rabbitmq.host=localhost",
    "spring.redis.host=localhost"
})
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@SpringBootTest
public abstract class BaseE2E {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected CustomUserDetailsService userDetailsService;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected com.nubiz.nutrust.repository.CompanyRepository companyRepository;

    @Autowired
    protected com.nubiz.nutrust.repository.ProjectRepository projectRepository;

    @Autowired
    protected com.nubiz.nutrust.repository.TicketRepository ticketRepository;

    @Autowired
    protected com.nubiz.nutrust.repository.ProcessingPlanRepository processingPlanRepository;

    @Autowired
    protected com.nubiz.nutrust.repository.NotificationLogRepository notificationLogRepository;

    @Autowired
    protected com.nubiz.nutrust.repository.NoticeRepository noticeRepository;

    @Autowired
    protected com.nubiz.nutrust.repository.HolidayRepository holidayRepository;

    @Autowired
    protected com.nubiz.nutrust.repository.ExtensionRequestRepository extensionRequestRepository;

    protected String testUserEmail = "e2E@example.com";
    protected String testUserPassword = "password123";

    // Seeded IDs (set by seedData())
    protected long seededCompanyId = 0;
    protected long seededProjectId = 0;
    protected long seededTicketId = 0;
    protected long seededUserId = 0;
    protected long seededPlanId = 0;
    protected long seededNotificationId = 0;
    protected long seededNoticeId = 0;
    protected long seededHolidayId = 0;
    protected long seededExtensionRequestId = 0;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        cleanDb();
        createAdminUser();
        seedData();
    }

    protected String findRoleName(String name) {
        return roleRepository.findByName(name).map(Role::getName).orElse(null);
    }

    protected long createAdminUser() {
        if (!userRepository.existsByEmail(testUserEmail)) {
            Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").description("Administrator").build()));
            Role companyAdminRole = roleRepository.findByName("COMPANY_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("COMPANY_ADMIN").description("Company Admin").build()));
            Role supportRole = roleRepository.findByName("SUPPORT")
                .orElseGet(() -> roleRepository.save(Role.builder().name("SUPPORT").description("Support").build()));
            Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("CUSTOMER").description("Customer").build()));

            User user = User.builder()
                .email(testUserEmail)
                .password(passwordEncoder.encode(testUserPassword))
                .name("E2E Test User")
                .status(UserStatus.ACTIVE)
                .roles(java.util.Set.of(adminRole, companyAdminRole, supportRole, customerRole))
                .build();
            userRepository.save(user);
        }
        return userRepository.findByEmail(testUserEmail)
            .orElseThrow().getId();
    }

    protected void seedData() {
        // 1. Create company (ID=1 via JPA sequence)
        Company company = Company.builder()
            .name("E2E Test Company")
            .businessNumber("123-45-67890")
            .address("Seoul, Korea")
            .status("ACTIVE")
            .build();
        Company savedCompany = companyRepository.save(company);
        seededCompanyId = savedCompany.getId();

        // 2. Create a customer company (ID=2)
        Company customerCompany = Company.builder()
            .name("E2E Customer Company")
            .businessNumber("098-76-54321")
            .address("Seoul, Korea")
            .status("ACTIVE")
            .build();
        Company savedCustomerCompany = companyRepository.save(customerCompany);
        long customerCompanyId = savedCustomerCompany.getId();

        // 3. Create owner user (if not already created by createAdminUser)
        User owner;
        if (userRepository.findByEmail("owner@e2e.com").isEmpty()) {
            Role customerRole = roleRepository.findByName("CUSTOMER")
                .orElseThrow(() -> new RuntimeException("CUSTOMER role not found"));
            owner = User.builder()
                .email("owner@e2e.com")
                .password(passwordEncoder.encode("password123"))
                .name("Project Owner")
                .companyId(seededCompanyId)
                .status(UserStatus.ACTIVE)
                .roles(Set.of(customerRole))
                .build();
            owner = userRepository.save(owner);
        } else {
            owner = userRepository.findByEmail("owner@e2e.com")
                .orElseThrow();
        }
        seededUserId = owner.getId();

        // 4. Create project
        Project project = Project.builder()
            .companyId(seededCompanyId)
            .customerCompanyId(customerCompanyId)
            .name("E2E Test Project")
            .ownerId(seededUserId)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusMonths(3))
            .status("ACTIVE")
            .description("Test project for E2E tests")
            .build();
        Project savedProject = projectRepository.save(project);
        seededProjectId = savedProject.getId();

        // 5. Create a ticket
        Ticket ticket = Ticket.builder()
            .project(savedProject)
            .title("E2E Test Ticket")
            .description("Test ticket description")
            .type("bug")
            .priority("HIGH")
            .status("REGISTERED")
            .currentStatus("REGISTERED")
            .progress(0)
            .build();
        ticket = ticketRepository.save(ticket);
        seededTicketId = ticket.getId();

        ProcessingPlan plan = ProcessingPlan.builder()
            .ticket(ticket)
            .writer(owner)
            .title("E2E Test Plan")
            .content("Test plan content")
            .status("SUBMITTED")
            .build();
        plan = processingPlanRepository.save(plan);
        seededPlanId = plan.getId();

        NotificationLog notification = NotificationLog.builder()
            .eventId("test-event-" + System.currentTimeMillis())
            .targetUserId(seededUserId)
            .ticketId(seededTicketId)
            .payload("{}")
            .sentVia("IN_APP")
            .status("QUEUED")
            .build();
        notification = notificationLogRepository.save(notification);
        seededNotificationId = notification.getId();

        Notice notice = Notice.builder()
            .companyId(seededCompanyId)
            .title("E2E Test Notice")
            .content("Test notice content")
            .isPinned(false)
            .status("ACTIVE")
            .authorId(seededUserId)
            .build();
        notice = noticeRepository.save(notice);
        seededNoticeId = notice.getId();

        Holiday holiday = Holiday.builder()
            .companyId(seededCompanyId)
            .holidayDate(LocalDate.of(2024, 12, 25))
            .holidayName("크리스마스")
            .holidayType("NATIONAL")
            .build();
        holiday = holidayRepository.save(holiday);
        seededHolidayId = holiday.getId();

        ExtensionRequest extensionRequest = ExtensionRequest.builder()
            .ticketId(seededTicketId)
            .userId(seededUserId)
            .reason("추가 작업 필요")
            .requestedExtendDays(3)
            .status("PENDING")
            .build();
        extensionRequest = extensionRequestRepository.save(extensionRequest);
        seededExtensionRequestId = extensionRequest.getId();
    }

    protected String loginWithDefault() throws Exception {
        return loginUser(testUserEmail, testUserPassword);
    }

    protected String loginAsAdmin() throws Exception {
        return loginUser(testUserEmail, testUserPassword);
    }

    protected String loginUser(String email, String password) throws Exception {
        cleanDb();
        createAdminUser();
        return performLoginAndReturnToken(email, password);
    }

    protected String performLoginAndReturnToken(String email, String password) throws Exception {
        String body = objectMapper.writeValueAsString(Map.of("email", email, "password", password));
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(responseBody);
        return json.has("accessToken") ? json.get("accessToken").asText() : null;
    }

    protected void cleanDb() {
        String[] tables = {"user_roles", "users", "roles", "tickets", "ticket_comments",
            "ticket_histories", "ticket_attachments", "ticket_labels", "companies",
            "ticket_label_mapping", "ticket_status_histories", "audit_logs"};
        for (String table : tables) {
            try {
                jdbcTemplate.execute("TRUNCATE TABLE " + table + " RESTART IDENTITY CASCADE");
            } catch (Exception ignored) {
            }
        }
    }

    @Configuration
    static class TestConfig {
        @Bean("rabbitConnectionFactory")
        ConnectionFactory rabbitConnectionFactory() {
            return Mockito.mock(ConnectionFactory.class);
        }

        @Bean
        RabbitTemplate rabbitTemplate() {
            return Mockito.mock(RabbitTemplate.class);
        }
        @Bean
        SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory cf) {
            var factory = new SimpleRabbitListenerContainerFactory();
            factory.setConnectionFactory(cf);
            factory.setAutoStartup(false);
            return factory;
        }
        @Bean
        RedisConnectionFactory redisConnectionFactory() {
            return Mockito.mock(RedisConnectionFactory.class);
        }
        @Bean("redisTemplate")
        RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
            var template = new RedisTemplate<String, Object>();
            template.setConnectionFactory(connectionFactory);
            return template;
        }

        @Bean
        StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
            java.util.Map<String, String> store = new java.util.concurrent.ConcurrentHashMap<>();
            var ops = org.mockito.Mockito.mock(org.springframework.data.redis.core.ValueOperations.class);
            org.mockito.Mockito.doAnswer(inv -> {
                    store.put(inv.getArgument(0), inv.getArgument(1));
                    return null;
                })
                .when(ops).set(
                    org.mockito.ArgumentMatchers.anyString(),
                    org.mockito.ArgumentMatchers.anyString(),
                    org.mockito.ArgumentMatchers.anyLong(),
                    org.mockito.ArgumentMatchers.any(java.util.concurrent.TimeUnit.class));
            org.mockito.Mockito.when(ops.get(org.mockito.ArgumentMatchers.anyString()))
                .thenAnswer(inv -> store.get(inv.getArgument(0)));
            org.springframework.data.redis.core.StringRedisTemplate tpl = new org.springframework.data.redis.core.StringRedisTemplate(connectionFactory);
            org.springframework.data.redis.core.StringRedisTemplate spy = org.mockito.Mockito.spy(tpl);
            org.mockito.Mockito.doReturn(ops).when(spy).opsForValue();
            org.mockito.Mockito.doAnswer(inv -> {
                    String key = inv.getArgument(0);
                    store.remove(key);
                    return null;
                })
                .when(spy).delete(org.mockito.ArgumentMatchers.anyString());
            return spy;
        }
    }
}
