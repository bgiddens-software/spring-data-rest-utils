package com.bgiddens.projection;

import com.bgiddens.impl.entities.Department;
import com.bgiddens.impl.entities.User;
import com.bgiddens.impl.entities.UserDepartment;
import com.bgiddens.impl.repos.DepartmentRepo;
import com.bgiddens.impl.repos.UserDepartmentRepo;
import com.bgiddens.impl.repos.UserRepo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This test case contains no assertions because doing so would couple it strongly to the underlying database adapter.
 * It is therefore disabled by default. It can nonetheless be used manually with settings to show generated SQL to
 * demonstrate that enabling this package's projection entity graph features results in improved query structure based
 * on the requested projection.
 */
// @Disabled
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testUser", roles = "ADMIN")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProjectionEntityGraphTest {

	@Autowired private DepartmentRepo departmentRepo;

	@Autowired private UserRepo userRepo;

	@Autowired private MockMvc mockMvc;

	@Autowired private UserDepartmentRepo userDepartmentRepo;

	private static final Department departmentOperations = Department.builder().name("Operations").build();
	private static final Department departmentAccounting = Department.builder().name("Accounting").build();
	private static final User userAlice = User.builder().principal("Alice").build();
	private static final User userBob = User.builder().principal("Bob").build();
	private static final User userCharlie = User.builder().principal("Charlie").build();
	private static final UserDepartment userDepartmentAliceOperations = UserDepartment.builder().user(userAlice)
			.department(departmentOperations).build();
	private static final UserDepartment userDepartmentBobOperations = UserDepartment.builder().user(userBob)
			.department(departmentOperations).build();
	private static final UserDepartment userDepartmentBobAccounting = UserDepartment.builder().user(userBob)
			.department(departmentAccounting).build();
	private static final UserDepartment userDepartmentCharlieAccounting = UserDepartment.builder().user(userCharlie)
			.department(departmentAccounting).build();

	@BeforeAll
	void setup() {
		departmentRepo.save(departmentOperations);
		departmentRepo.save(departmentAccounting);
		userRepo.save(userAlice);
		userRepo.save(userBob);
		userRepo.save(userCharlie);
		userDepartmentRepo.save(userDepartmentAliceOperations);
		userDepartmentRepo.save(userDepartmentBobOperations);
		userDepartmentRepo.save(userDepartmentBobAccounting);
		userDepartmentRepo.save(userDepartmentCharlieAccounting);
	}

	@AfterAll
	void cleanup() {
		userDepartmentRepo.deleteAll();
		departmentRepo.deleteAll();
		userRepo.deleteAll();
	}

	@Test
	void testFetchGraph() throws Exception {
		mockMvc.perform(get("/users?projection=userWithDepartments")).andExpect(status().is2xxSuccessful());
	}
}
