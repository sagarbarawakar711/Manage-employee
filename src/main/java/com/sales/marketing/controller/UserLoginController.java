package com.sales.marketing.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sales.marketing.bean.PasswordEncoderConfig;
import com.sales.marketing.config.JwtTokenUtil;
import com.sales.marketing.model.UserLogin;
import com.sales.marketing.service.UserLoginService;
import com.sales.marketing.utils.GenerateEncryptionPassword;
import com.sales.marketing.utils.GeneratePlainPassword;
import com.sales.marketing.vo.SearchVO;
import com.sales.marketing.vo.UserResponse;
import com.sam.email.SendEmailNotification;

@CrossOrigin(allowedHeaders = "*")
@RestController
@RequestMapping("/userlogin")
public class UserLoginController {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	PasswordEncoderConfig passwordEncoder;
	
	@Autowired
	UserLoginService service;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@GetMapping
	public List<UserLogin> getAllUserLogins() {
		return service.getAllUserLogins();
	}
	
	@PostMapping
	public HttpStatus insertUserLoginInfo(@RequestBody UserLogin userLogin) {
		boolean result = service.addUserLogin(userLogin);
		return  result? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;
	}
	
	@PutMapping
	public UserLogin updateUserLoginInfo(@RequestBody SearchVO search) {		
		UserLogin userLogin = new UserLogin();
		try {
			List<UserLogin> loginUserData = service.findByUserId(search.getEmailId());
			for (Iterator<UserLogin> iterator = loginUserData.iterator(); iterator.hasNext();) {
				userLogin = (UserLogin) iterator.next();
				//validating user given current password with existing password.
				String tempkey =userLogin.getPasswordSalt();
				String existingpassword=userLogin.getPasswordHash();
				String plainpassword = new GeneratePlainPassword().GetPlainPaswword(tempkey, existingpassword);
				if (!search.getPassWord().equals(plainpassword)) {
					userLogin.setApiStatusMessage("Current password is not matching");
					return userLogin;
				} else {
					String passwordencoder = new GenerateEncryptionPassword().GenerateEncrytPassword(tempkey, search.getNewPassWord());
					userLogin.setPasswordSalt(tempkey);
					userLogin.setPasswordHash(passwordencoder);	
					userLogin.setLastUpdatedD(new Timestamp(new Date().getTime()));
					userLogin.setLastUpdatedUserI(search.getEmailId());
					boolean result= service.updateUserLogin(userLogin);
					if(result) {
						//userLogin.setApiStatusMessage("Successfully password updated");
						logger.info(userLogin.toString());
						
						//If you don't want to send any email please remove the following lines of code.
						String toEmailIds[] = new String[1];
						toEmailIds[0] = search.getEmailId();
						SendEmailNotification sendEmail = new SendEmailNotification();
						String templateName = "Password successfully updated.";
						templateName+="<br> Please use the following credentials to Login.";
						templateName+= "<br> User Name :"+search.getEmailId();
						templateName+= "<br> New Password : " + search.getNewPassWord();
						templateName+="<br> Thanks";
						templateName+="<br> LLP Team.";
								templateName+="<br> Please note this is an auto generated email."; 
						String msg = sendEmail.sendMail(toEmailIds, null, null, "Password successfully updated ", templateName, null);
						logger.info(msg);						
						return userLogin;
					}
				}	
			}
		} catch (Exception e) {
			userLogin.setApiStatusMessage(e.getMessage());
		} /*
			 * finally { userLogin.setApiStatusMessage("Update Password Failed."); }
			 */
		return userLogin;
	}
	@PostMapping(value = "/{validate}")
	public List<UserResponse> getUserInfo(@RequestBody SearchVO search) {
		try {
			List<UserLogin> result = service.findByUserId(search.getEmailId());
			if (result.isEmpty()) {
				List<UserLogin> list = new ArrayList<UserLogin>();
				UserLogin ul = new UserLogin();
				ul.setApiStatusMessage("Account doesnt exist");
				list.add(ul);
				return getUserForResponse(list);
			} else {
				for (Iterator<UserLogin> iterator = result.iterator(); iterator.hasNext();) {
					UserLogin userLogin = (UserLogin) iterator.next();
					if(userLogin.getInactiveD() != null) {
						userLogin.setApiStatusMessage("Account is terminated");
					}else {
						String tempkey =userLogin.getPasswordSalt();
						String existingpassword=userLogin.getPasswordHash();
						String plainpassword = new GeneratePlainPassword().GetPlainPaswword(tempkey, existingpassword);
						if (!search.getPassWord().equals(plainpassword)) {
							userLogin.setApiStatusMessage("Incorrect Password");
						}
					}
					
				}

				return getUserForResponse(result);
			} 
		} catch (Exception e) {
			List<UserLogin> list = new ArrayList<UserLogin>();
			UserLogin ul = new UserLogin();
			ul.setApiStatusMessage(e.getMessage());
			list.add(ul);
			return getUserForResponse(list);

		}		
	}
	
	private List<UserResponse> getUserForResponse(List<UserLogin> userLogin) {
		List<UserResponse> list = new ArrayList<UserResponse>();
		for (Iterator<UserLogin> iterator = userLogin.iterator(); iterator.hasNext();) {
			UserLogin user = (UserLogin) iterator.next();
			UserResponse resp = new UserResponse();
			resp.setApiStatusMessage(user.getApiStatusMessage());
			resp.setEmployee(user.getEmployee());
			resp.setEmployeeId(user.getEmployeeId());
			resp.setPasswordHash(user.getPasswordHash());
			resp.setPasswordSalt(user.getPasswordSalt());
			resp.setUserId(user.getUserId());
			
			if(user.getApiStatusMessage() == null) {
				resp.setJwtToken(jwtTokenUtil.generateToken(user.getUserId()));
			}
			
			list.add(resp);
		}
		return list;
	}
	
	
	@PostMapping(value = "/forgot/{emailId}")
	public String forgotPassword(@PathVariable String emailId) {
		try {
			List<UserLogin> result = service.findByUserId(emailId);
			if (result.isEmpty()) {
				return "Account doesnt exist";
			} else {
				for (Iterator<UserLogin> iterator = result.iterator(); iterator.hasNext();) {
					UserLogin userLogin = (UserLogin) iterator.next();
					if(userLogin.getInactiveD() != null) {
						return "Account is terminated";
					}else {
						String tempkey =userLogin.getPasswordSalt();
						String existingpassword=userLogin.getPasswordHash();
						String plainpassword = new GeneratePlainPassword().GetPlainPaswword(tempkey, existingpassword);
						String toEmailIds[] = new String[1];
						toEmailIds[0] = emailId;
						SendEmailNotification sendEmail = new SendEmailNotification();
						String templateName = "";
						templateName+="<br> Please use the following credentials to Login.";
						templateName+= "<br> User Name :"+emailId;
						templateName+= "<br> Password : " + plainpassword;
						templateName+="<br> Thanks";
						templateName+="<br> LLP Team.";
								templateName+="<br> Please note this is an auto generated email."; 
						String msg = sendEmail.sendMail(toEmailIds, null, null, "Password successfully sent ", templateName, null);
						logger.info(msg);
						return msg;
					}
				}
				return null;
			} 
		} catch (Exception e) {
			return e.getMessage();
		}
	}	
}