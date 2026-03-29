package com.bgiddens.projection;

import com.bgiddens.impl.entities.Country;
import com.bgiddens.impl.entities.Department;
import com.bgiddens.impl.entities.Employee;
import com.bgiddens.impl.entities.Region;
import com.bgiddens.impl.entities.User;
import com.bgiddens.impl.entities.UserCountry;
import com.bgiddens.impl.entities.UserDepartment;
import com.bgiddens.impl.entities.UserRegion;
import com.bgiddens.impl.repos.CountryRepo;
import com.bgiddens.impl.repos.DepartmentRepo;
import com.bgiddens.impl.repos.EmployeeRepo;
import com.bgiddens.impl.repos.RegionRepo;
import com.bgiddens.impl.repos.UserCountryRepo;
import com.bgiddens.impl.repos.UserDepartmentRepo;
import com.bgiddens.impl.repos.UserRegionRepo;
import com.bgiddens.impl.repos.UserRepo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
		mockMvc.perform(get("/users?projection=userWithDepartments")).andDo(print()).andExpect(status().is2xxSuccessful());
	}
}
