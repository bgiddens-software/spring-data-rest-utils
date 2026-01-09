package com.bgiddens.repos;

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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
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
@WithMockUser(username = "testUser")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeRepoTest {

	@Autowired private CountryRepo countryRepo;

	@Autowired private RegionRepo regionRepo;

	@Autowired private DepartmentRepo departmentRepo;

	@Autowired private EmployeeRepo employeeRepo;

	@Autowired private MockMvc mockMvc;

	@Autowired private UserRepo userRepo;

	@Autowired private UserDepartmentRepo userDepartmentRepo;

	@Autowired private UserRegionRepo userRegionRepo;

	private static final Region regionNorth = Region.builder().name("North").build();
	private static final Region regionSouth = Region.builder().name("South").build();
	private static final Region regionEast = Region.builder().name("East").build();
	private static final Region regionWest = Region.builder().name("West").build();
	private static final Country countryArstotzka = Country.builder().name("Arstotzka").region(regionEast).build();
	private static final Country countryObrikrai = Country.builder().name("Obrikrai").region(regionNorth).build();
	private static final Country countryImpor = Country.builder().name("Impor").region(regionSouth).build();
	private static final Country countryAntegria = Country.builder().name("Antegria").region(regionWest).build();
	private static final Department departmentOperations = Department.builder().name("Operations").build();
	private static final Department departmentAccounting = Department.builder().name("Accounting").build();
	private static final Employee employeeAlice = Employee.builder().name("Alice").department(departmentOperations)
			.countries(Set.of(countryObrikrai, countryAntegria)).build();
	private static final Employee employeeBob = Employee.builder().name("Bob").department(departmentAccounting)
			.countries(Set.of(countryImpor, countryAntegria, countryArstotzka)).build();
	private static final Employee employeeCharlie = Employee.builder().name("Charlie").department(departmentAccounting)
			.countries(Set.of(countryArstotzka)).build();
	@Autowired private UserCountryRepo userCountryRepo;

	@BeforeAll
	void setup() {
		regionRepo.save(regionNorth);
		regionRepo.save(regionSouth);
		regionRepo.save(regionEast);
		regionRepo.save(regionWest);
		countryRepo.save(countryArstotzka);
		countryRepo.save(countryObrikrai);
		countryRepo.save(countryImpor);
		countryRepo.save(countryAntegria);
		departmentRepo.save(departmentOperations);
		departmentRepo.save(departmentAccounting);
		employeeRepo.save(employeeAlice);
		employeeRepo.save(employeeBob);
		employeeRepo.save(employeeCharlie);
	}

	static Stream<Arguments> testCollectionFilteringArguments() {
		return Stream.of(
				Arguments.of(List.of(departmentAccounting), List.of(regionNorth, regionSouth, regionEast, regionWest),
						List.of(countryArstotzka, countryObrikrai, countryImpor, countryAntegria), List.of("Bob", "Charlie")),
				Arguments.of(List.of(departmentOperations), List.of(regionNorth, regionSouth, regionEast, regionWest),
						List.of(countryArstotzka, countryObrikrai, countryImpor, countryAntegria), List.of("Alice")),
				Arguments.of(List.of(departmentAccounting), List.of(regionNorth, regionSouth, regionEast, regionWest),
						List.of(countryAntegria), List.of("Bob")),
				Arguments.of(List.of(departmentAccounting), List.of(regionEast),
						List.of(countryArstotzka, countryObrikrai, countryImpor, countryAntegria), List.of("Bob", "Charlie")),
				Arguments.of(List.of(departmentAccounting), List.of(regionSouth),
						List.of(countryArstotzka, countryObrikrai, countryImpor, countryAntegria), List.of("Bob")),
				Arguments.of(List.of(departmentAccounting), List.of(regionEast), List.of(countryArstotzka),
						List.of("Bob", "Charlie")),
				Arguments.of(List.of(departmentAccounting), List.of(regionEast), List.of(countryArstotzka),
						List.of("Bob", "Charlie")),
				Arguments.of(List.of(departmentAccounting), List.of(regionNorth), List.of(countryArstotzka), List.of()),
				Arguments.of(List.of(), List.of(), List.of(), List.of()),
				Arguments.of(List.of(departmentOperations, departmentAccounting),
						List.of(regionNorth, regionSouth, regionEast, regionWest),
						List.of(countryArstotzka, countryObrikrai, countryImpor, countryAntegria),
						List.of("Alice", "Bob", "Charlie")));
	}

	@ParameterizedTest
	@MethodSource("testCollectionFilteringArguments")
	void testCollectionFiltering(List<Department> userDepartments, List<Region> userRegions, List<Country> userCountries,
			List<String> expectedNames) throws Exception {

		var user = userRepo.saveAndFlush(User.builder().principal("testUser").build());
		userDepartments.forEach(department -> userDepartmentRepo
				.saveAndFlush(UserDepartment.builder().user(user).department(department).build()));
		userRegions.forEach(region -> userRegionRepo.saveAndFlush(UserRegion.builder().user(user).region(region).build()));
		userCountries
				.forEach(country -> userCountryRepo.saveAndFlush(UserCountry.builder().user(user).country(country).build()));

		var res = mockMvc.perform(get("/employees?sort=name")).andDo(print()).andExpect(status().is2xxSuccessful());
		res.andExpect(jsonPath("$.page.totalElements").value(expectedNames.size()));
		for (int i = 0; i < expectedNames.size(); i++) {
			res.andExpect(jsonPath(String.format("$._embedded.employees[%s].name", i)).value(expectedNames.get(i)));
		}

		userDepartmentRepo.deleteAll();
		userRegionRepo.deleteAll();
		userCountryRepo.deleteAll();
		userRepo.deleteAll();
	}
}
