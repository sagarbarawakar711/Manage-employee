package com.sales.marketing.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.sales.marketing.controller.model.EmployeeDealerGradeDetails;
import com.sales.marketing.model.DDealerInfo;
import com.sales.marketing.model.EmailConfigVO;
import com.sales.marketing.model.EmpBusinessCoverageArea;
import com.sales.marketing.model.Employee;
import com.sales.marketing.model.EmployeeAssignedGoal;
import com.sales.marketing.model.UserLogin;
import com.sales.marketing.model.UserRoles;
import com.sales.marketing.model.VDealerGrade;
import com.sales.marketing.model.VEmployeeSearch;
import com.sales.marketing.repository.EmailConfigRepository;
import com.sales.marketing.repository.EmployeeRepository;
import com.sales.marketing.repository.EmployeeSearchRepository;
import com.sales.marketing.utils.GenerateEncryptionPassword;
import com.sales.marketing.utils.GenerateKey;
import com.sales.marketing.vo.SearchVO;
import com.sam.email.SendEmailNotification;

@Service
public class EmployeeService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Value("${newuserreg.email.config.id}")
	private String newUserRegEmailConfig;
	
	@Autowired
	EmailConfigRepository<EmailConfigVO> emailConfigVO;

	@Autowired
	EmployeeRepository<Employee> employeeRepository;

	@Autowired
	EmployeeSearchRepository<Employee> employeeSearchRepository;

	@Autowired
	EmpBusinessCoverageAreaService empbcaService;

	@Autowired
	EmpAssignedFGoalService empAssignGoalService;

	@Autowired
	UserLoginService userLoginService;

	@Autowired
	private RestTemplate restTemplate;

	public String getEmployeeData() {
		String url = "http://manage-employee/api/employees";
		return restTemplate.getForObject(url, String.class);
	}

	@Transactional
	public List<Employee> getAllEmployees() {
		List<Employee> list = employeeRepository.findAll(Sort.by(Sort.Direction.ASC, "employeeId"));
		for (Iterator<Employee> iterator = list.iterator(); iterator.hasNext();) {
			Employee employee = (Employee) iterator.next();
			generateEmpBusinessCoverageArea(employee);
			generateEmpAssignGoals(employee);
		}
		return list;
	}

	@Transactional
	public List<Employee> findEmployeeId(Long employeeId) throws ServiceException {
		List<Employee> list = employeeRepository.findEmployeeByEmployeeId(employeeId);
		for (Iterator<Employee> iterator = list.iterator(); iterator.hasNext();) {
			Employee employee = (Employee) iterator.next();
			generateEmpBusinessCoverageArea(employee);
			generateEmpAssignGoals(employee);
		}
		return list;
	}

	private void generateEmpBusinessCoverageArea(Employee employee) throws ServiceException {
		List<EmpBusinessCoverageArea> empbcalist = empbcaService.findByEmployeeId(employee.getEmployeeId());
		if (!empbcalist.isEmpty()) {
			for (Iterator<EmpBusinessCoverageArea> iterator2 = empbcalist.iterator(); iterator2.hasNext();) {
				EmpBusinessCoverageArea empBusinessCoverageArea = (EmpBusinessCoverageArea) iterator2.next();
				employee.getEmpBusinessCoverageAreas().add(empBusinessCoverageArea);
			}
		}
	}

	private void generateEmpAssignGoals(Employee employee) throws ServiceException {
		List<EmployeeAssignedGoal> empaglist = empAssignGoalService
				.retrieveEmployeeAssignedGoals(employee.getEmployeeId());
		if (!empaglist.isEmpty()) {
			for (Iterator<EmployeeAssignedGoal> iterator2 = empaglist.iterator(); iterator2.hasNext();) {
				EmployeeAssignedGoal employeeAssignedGoal = (EmployeeAssignedGoal) iterator2.next();
				employee.getEmployeeAssignedGoals().add(employeeAssignedGoal);
			}
		}
	}

	public List<VEmployeeSearch> searchEmployees(SearchVO search) throws ServiceException {
		return employeeSearchRepository.searchEmployees(
				(search.getFirstName().length() == 0 ? null : search.getFirstName().toUpperCase()),
				(search.getLastName().length() == 0 ? null : search.getLastName().toUpperCase()),
				(search.getRoleType().length() == 0 ? null : search.getRoleType()),
				(search.getStatusCode().length() == 0 ? null : search.getStatusCode()));
	}

	public List<VEmployeeSearch> getEmployeesForDealerId(Integer dealerId) throws ServiceException {
		List<Integer> employeeIdList = employeeSearchRepository.getEmployeesForDealerId(dealerId);
		List<VEmployeeSearch> employeeDetails = employeeSearchRepository.searchEmployeesList(employeeIdList);

		return employeeDetails;
	}

	@Transactional
	public Employee addEmployee(Employee employee) throws Exception {
		boolean result = false;
		if (employee.getUserRoles() != null) {
			for (UserRoles userRole : employee.getUserRoles()) {
				userRole.setEmployee(employee); // Link each role to the employee
			}
		}
		// Saving Data into Employee Table
		Employee emp = employeeRepository.save(employee);
		logger.info("Generated Emp Id: " + employee.getEmployeeId());

		// Following lines of code is applicable to sales rep users only.
		if (emp != null && emp.getUserRoleC().equalsIgnoreCase("SR")) {
			// Saving data into Employee Business Coverage Area
			List<EmpBusinessCoverageArea> list = new ArrayList<EmpBusinessCoverageArea>();
			Set<EmpBusinessCoverageArea> empbca = emp.getEmpBusinessCoverageAreas();
			for (Iterator<EmpBusinessCoverageArea> iterator = empbca.iterator(); iterator.hasNext();) {
				EmpBusinessCoverageArea empBusinessCoverageArea = (EmpBusinessCoverageArea) iterator.next();
				empBusinessCoverageArea.getId().setEmployeeId(employee.getEmployeeId());
				list.add(empBusinessCoverageArea);
			}
			if (!list.isEmpty()) {
				result = empbcaService.addEmpBusinessCoverageArea(list);
			}
			if (!emp.getEmployeeAssignedGoals().isEmpty()) {
				// Saving data into Employee Agent Goals Table
				List<EmployeeAssignedGoal> empaglist = new ArrayList<EmployeeAssignedGoal>();
				Set<EmployeeAssignedGoal> empag = emp.getEmployeeAssignedGoals();
				for (Iterator<EmployeeAssignedGoal> iterator = empag.iterator(); iterator.hasNext();) {
					EmployeeAssignedGoal empAsignGoal = (EmployeeAssignedGoal) iterator.next();
					empAsignGoal.setEmployeeId(employee.getEmployeeId());
					empaglist.add(empAsignGoal);
				}
				result = empAssignGoalService.addEmployeeAssignedGoals(empaglist);
			}
		}
		if (emp != null) {
			String key = new GenerateKey().GenerateRandomKey();
			// String passwordencoder = new
			// GenerateEncryptionPassword().GenerateEncrytPassword(key,
			// autoPassword(emp,key));
			String passwordencoder = new GenerateEncryptionPassword().GenerateEncrytPassword(key, "welcome1");

			UserLogin userlogin = new UserLogin();

			userlogin.setEmployeeId(emp.getEmployeeId());
			userlogin.setUserId(emp.getDigitalContactInfo().getEmail());
			userlogin.setPasswordSalt(key);
			userlogin.setPasswordHash(passwordencoder);
			userlogin.setCreationUserI(emp.getCreationUserI());
			userlogin.setCreationD(new Timestamp(new Date().getTime()));
			if (null != emp.getInactiveD()) {
				userlogin.setInactiveD(emp.getInactiveD());
			}
			// userlogin.setInactiveD(new Timestamp(new Date().getTime()));
			result = userLoginService.addUserLogin(userlogin);
			if (result) {
				
				// Configure from email credentails
				EmailConfigVO emailConfigDetail = getEmailConfigDetails(newUserRegEmailConfig);
				Map<String, String> emailConfigMap = new HashMap<>();
				if (emailConfigDetail != null) {
					emailConfigMap.put("emailUser", emailConfigDetail.getEmailUser());
					emailConfigMap.put("password", emailConfigDetail.getPassword());
					emailConfigMap.put("protocol", emailConfigDetail.getProtocol());
					emailConfigMap.put("smtpHost", emailConfigDetail.getSmtpHost());
					emailConfigMap.put("smtpPort", emailConfigDetail.getSmtpPort());
					emailConfigMap.put("smtpAuth", emailConfigDetail.getSmtpAuth());
					emailConfigMap.put("starttlsEnable", emailConfigDetail.getStarttlsEnable());
					emailConfigMap.put("active", emailConfigDetail.getActive());
					emailConfigMap.put("emailServer", emailConfigDetail.getEmailServer());
					emailConfigMap.put("description", emailConfigDetail.getDescription());
				}
				
				String toEmailIds[] = new String[1];
				toEmailIds[0] = emp.getDigitalContactInfo().getEmail();
				SendEmailNotification sendEmail = new SendEmailNotification();
				String templateName = "Welcome <b>" + emp.getFirstName() + " " + emp.getLastName() + "</b> !";
				templateName += "<br> Congratulation we are able to setup the account.";
				templateName += "<br> Please use the following credentials to Login onces your account is activated.";
				templateName += " Please contact administrator and inform you receiced account registration credentials and request to activate your account.";
				templateName += "<br> User Name :" + emp.getDigitalContactInfo().getEmail();
				templateName += "<br> Password : " + "welcome1";
				templateName += "<br> Thanks";
				templateName += "<br> LLP Team.";
				templateName += "<br> Please note this is an auto generated email.";
				String msg = sendEmail.sendMail(toEmailIds, null, null, "New Employee Added into the System",
						templateName, null, emailConfigMap, null);
				logger.info(emp.getFirstName() + ", " + emp.getLastName() + ", " + emp.getEmployeeId() + ", "
						+ emp.getDigitalContactInfo().getEmail() + " := " + msg);
				if (StringUtils.isNotEmpty(msg)) {
					employee.setApiStatusMessage(msg);
				}
			}
		}
		if (result) {
			employee.setApiStatusMessage("CREATED");
		}
		return employee;
	}

	/*
	 * private String autoPassword(Employee emp, String key) { String autopass =
	 * emp.getFirstName().substring(0, 1) +
	 * emp.getLastName().substring(emp.getLastName().length()-1,
	 * emp.getLastName().length()).toUpperCase()+ "#" + key.substring(0, 5); return
	 * autopass; }
	 */
	private boolean dataDeleted(Set<EmpBusinessCoverageArea> newList, long existingDealerId) {
		int i = 0;
		for (EmpBusinessCoverageArea ebca : newList) {
			if (ebca.getId().getDealerId() == existingDealerId) {
				i = -1;
				break;
			}
		}
		return (i == 0 ? true : false);
	}

	private boolean empAssignedGoalsdataDeleted(Set<EmployeeAssignedGoal> newList, long existingGoalId) {
		int i = 0;
		for (EmployeeAssignedGoal eag : newList) {
			if (eag.getGoalId() == existingGoalId) {
				i = -1;
				break;
			}
		}
		return (i == 0 ? true : false);
	}

	@Transactional
	public Employee updateEmployee(Employee employee) {
		Employee emp = null;
		List<Employee> searchEmployee = findEmployeeId(employee.getEmployeeId());
		boolean result = false;
		if (searchEmployee.size() > 0) {
			if(employee.getUserRoles().size() > 0) {
				if (employee.getUserRoles() != null && searchEmployee != null && !searchEmployee.isEmpty()) {
					List<UserRoles> newRoles = employee.getUserRoles();
					List<UserRoles> existingRoles = searchEmployee.get(0).getUserRoles();

					// 1. Add new roles that are not already in the existing roles
					for (UserRoles newRole : newRoles) {
						boolean roleExists = existingRoles.stream()
								.anyMatch(existingRole -> existingRole.getUserRoleC().equals(newRole.getUserRoleC()));

						if (!roleExists) {
							// If the role doesn't exist in the current list, add it
							newRole.setEmployee(employee); // Link the new role to the employee
							existingRoles.add(newRole); // Add it to the employee's roles list
						}
					}

					// 2. Remove roles that are no longer part of the new roles list
					List<UserRoles> rolesToRemove = new ArrayList<>();
					for (UserRoles existingRole : existingRoles) {
						boolean roleExists = newRoles.stream()
								.anyMatch(newRole -> newRole.getUserRoleC().equals(existingRole.getUserRoleC()));

						if (!roleExists) {
							// If the role doesn't exist in the new list, mark it for removal
							rolesToRemove.add(existingRole);
						}
					}

					// 3. Remove the identified roles from the existing roles
					existingRoles.removeAll(rolesToRemove);

					// Set the updated roles back to the employee
					employee.setUserRoles(existingRoles);
				}

				try {
					// Save the employee entity
					emp = employeeRepository.save(employee);
				} catch (DataIntegrityViolationException e) {
					e.getMessage();
				} catch (Exception e) {
					e.getMessage();
				}

				// Following logic will in active user information.
				if (emp != null) {
					List<UserLogin> userloginlist = userLoginService.findByUserId(emp.getDigitalContactInfo().getEmail());
					if (!userloginlist.isEmpty()) {
						UserLogin userloginobj = userloginlist.get(0);
						userloginobj.setInactiveD(emp.getInactiveD());
						userLoginService.updateUserLogin(userloginobj);
					}
				}

				// Following logic will updated Sales Rep Business coverage area agent goals
				// data
				if (emp != null && emp.getUserRoleC().equalsIgnoreCase("SR")) {
					/** Start Employee Business Coverage Area **/
					if (!emp.getEmpBusinessCoverageAreas().isEmpty()) {
						List<EmpBusinessCoverageArea> deletedObjects = new ArrayList<EmpBusinessCoverageArea>();
						List<EmpBusinessCoverageArea> existingObjects = empbcaService.findByEmployeeId(emp.getEmployeeId());
						for (Iterator<EmpBusinessCoverageArea> iterator = existingObjects.iterator(); iterator.hasNext();) {
							EmpBusinessCoverageArea empBusinessCoverageArea = (EmpBusinessCoverageArea) iterator.next();
							boolean isdatadeleted = dataDeleted(emp.getEmpBusinessCoverageAreas(),
									empBusinessCoverageArea.getId().getDealerId());
							if (isdatadeleted) {
								EmpBusinessCoverageArea obj = new EmpBusinessCoverageArea();
								obj = empBusinessCoverageArea;
								deletedObjects.add(obj);
							}
						}
						List<EmpBusinessCoverageArea> list = new ArrayList<EmpBusinessCoverageArea>();
						Set<EmpBusinessCoverageArea> empbca = emp.getEmpBusinessCoverageAreas();
						for (Iterator<EmpBusinessCoverageArea> iterator = empbca.iterator(); iterator.hasNext();) {
							EmpBusinessCoverageArea empBusinessCoverageArea = (EmpBusinessCoverageArea) iterator.next();
							list.add(empBusinessCoverageArea);
						}
						result = empbcaService.updateEmpBusinessCoverageArea(list);
						empbcaService.deleteEmpBusinessCoverageArea(deletedObjects);
					} else {
						empbcaService.deleteByEmployeeId(emp.getEmployeeId());
						empAssignGoalService.deleteEmployeeAssignedGoals(emp.getEmployeeId());
						result = true;
					}
					/** End Employee Business Coverage Area **/

					/** Start Employee Agent Goals **/
					List<EmployeeAssignedGoal> existinglist = empAssignGoalService
							.retrieveEmployeeAssignedGoals(employee.getEmployeeId());
					List<EmployeeAssignedGoal> deletedRows = new ArrayList<>();
					for (Iterator<EmployeeAssignedGoal> iterator = existinglist.iterator(); iterator.hasNext();) {
						EmployeeAssignedGoal employeeAssignedGoal = (EmployeeAssignedGoal) iterator.next();
						boolean isdatadeleted = empAssignedGoalsdataDeleted(emp.getEmployeeAssignedGoals(),
								employeeAssignedGoal.getGoalId());
						if (isdatadeleted) {
							EmployeeAssignedGoal agobj = new EmployeeAssignedGoal();
							agobj = employeeAssignedGoal;
							deletedRows.add(agobj);
						}
					}
					if (result && !emp.getEmployeeAssignedGoals().isEmpty()) {
						List<EmployeeAssignedGoal> empaglist = new ArrayList<EmployeeAssignedGoal>();
						Set<EmployeeAssignedGoal> empag = emp.getEmployeeAssignedGoals();
						for (Iterator<EmployeeAssignedGoal> iterator = empag.iterator(); iterator.hasNext();) {
							EmployeeAssignedGoal empAsignGoal = (EmployeeAssignedGoal) iterator.next();
							empAsignGoal.setEmployeeId(employee.getEmployeeId());
							empaglist.add(empAsignGoal);
						}
						result = empAssignGoalService.updateEmployeeAssignedGoals(empaglist);
						empAssignGoalService.deleteEmpAssignedGoals(deletedRows);
					}
					/** End Employee Agent Goals **/

				} else if (emp != null && !emp.getUserRoleC().equalsIgnoreCase("SR")) {
					result = true;
				}
				if (result) {
					emp.setApiStatusMessage("UPDATED");
				}
				return emp;
			} else {
				employee.setApiStatusMessage("User roles empty");
				return employee;
			}
			
		} else {
			return emp;
		}
	}

	@Transactional
	public void deleteEmployee(Long id) {
		userLoginService.deleteUserLogin(id);
		employeeRepository.deleteById(id);
		empbcaService.deleteByEmployeeId(id);
		empAssignGoalService.deleteEmployeeAssignedGoals(id);
	}

	@SuppressWarnings("rawtypes")
	@Transactional
	public List<DDealerInfo> retrieveSalesRepDealers(Long employeeId) {
		List<DDealerInfo> list = new ArrayList<DDealerInfo>();
		List<Object[]> records = (List<Object[]>) employeeRepository.findSalesRepDealers(employeeId);
		for (Iterator iterator = records.iterator(); iterator.hasNext();) {
			Object[] objects = (Object[]) iterator.next();
			DDealerInfo dealer = new DDealerInfo();
			dealer.setDealerId((Integer) objects[0]);
			dealer.setDealerName((String) objects[1]);
			list.add(dealer);
		}
		return list;
	}

	@SuppressWarnings("rawtypes")
	@Transactional
	public List<Employee> getSalesRepEmployees() {
		List<Employee> list = new ArrayList<Employee>();
		List<Object[]> records = (List<Object[]>) employeeRepository.findSalesRepEmpoyee();
		for (Iterator iterator = records.iterator(); iterator.hasNext();) {
			Object[] objects = (Object[]) iterator.next();
			Employee emp = new Employee();
			emp.setEmployeeId((Integer) objects[0]);
			emp.setFirstName((String) objects[1]);
			if (objects[2] != null) {
				emp.setMiddleName((String) objects[2]);
			}
			emp.setLastName((String) objects[3]);
			list.add(emp);
		}
		return list;
	}

	public List<EmployeeDealerGradeDetails> getEmployeeDealerGradeDetails(int empID, List<String> colorCodes) {

		List<VDealerGrade> results = employeeSearchRepository.searchEmpDealerGradeDetails(empID, colorCodes);

		return results.stream().map(r -> new EmployeeDealerGradeDetails(r.getEmployeeId(), r.getDealerId(),
				r.getDealerName(), r.getDealerGrade())).collect(Collectors.toList());
	}
	
	public EmailConfigVO getEmailConfigDetails(String configId) {
		try {
			String idStr = configId;
			EmailConfigVO configVO = emailConfigVO.findById(Integer.parseInt(idStr)).get();
			logger.info("Email details fetch");
			return configVO;
		} catch (Exception e) {
			logger.error("Error in reading Email configurations : " + e);
			return null;
		}
	}
}
