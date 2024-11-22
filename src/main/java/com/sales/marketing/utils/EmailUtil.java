/*package com.sales.marketing.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.mail.HtmlEmail;


public class EmailUtil {

	public static final String EMAIL_HOST = "smtp.csx.com";
	public static final String SALES_MARKETING_EMAIL_ID = "doNotReply_SalesAndMarketing@yahoo.com";

	public static void sendEMail(String from, String to, 
		String subject, String templateName, Map<String, Object> templateData) throws Exception {
		HtmlEmail email = new HtmlEmail();
		email.setHostName(EMAIL_HOST);
		
		email.setFrom(from);
		email.addTo(to);
		email.setSubject(subject);
		//email.setHtmlMsg(VelocityUtil.generate(templateName, templateData).toString());
		email.setHtmlMsg(templateName.toString());
		email.send();
	}
	public static void sendEMail(String from, String to, String cc, String bcc,
		String subject, String templateName, Map<String, Object> templateData) throws Exception {
		HtmlEmail email = new HtmlEmail();
		email.setHostName(EMAIL_HOST);
		
		email.setFrom(from);
		
		email.addTo(to);
		if (cc != null) {
			email.addCc(cc);
		}
		if (bcc != null) {
			email.addBcc(bcc);
		}

		email.setSubject(subject);
		//email.setHtmlMsg(VelocityUtil.generate(templateName, templateData).toString());
		email.send();
	}
	
	public static void sendEMail(String from, List<String> to, 
			String subject, String templateName, Map<String, Object> templateData) throws Exception {
		HtmlEmail email = new HtmlEmail();
		email.setHostName(EMAIL_HOST);
		
		email.setFrom(from);
		email.addTo(to.toArray(new String[]{}));
		email.setSubject(subject);
		//email.setHtmlMsg(VelocityUtil.generate(templateName, templateData).toString());
		email.send();
	}
	
	public static void sendEMail(String from, String[] to, 
			String subject, String templateName, Map<String, Object> templateData) throws Exception {
		HtmlEmail email = new HtmlEmail();
		email.setHostName(EMAIL_HOST);
		
		email.setFrom(from);
		email.addTo(to);
		email.setSubject(subject);
		//email.setHtmlMsg(VelocityUtil.generate(templateName, templateData).toString());
		email.send();
	}
	
	public static void sendEMail(String from, List<String> to, List<String> cc, List<String> bcc,
		String subject, String templateName, Map<String, Object> templateData) throws Exception {
		HtmlEmail email = new HtmlEmail();
		email.setHostName(EMAIL_HOST);
		
		email.setFrom(from);
		
		if (to != null) {
			email.addTo(to.toArray(new String[]{}));
		}
		if (cc != null) {
			email.addCc(cc.toArray(new String[]{}));
		}
		if (bcc != null) {
			email.addBcc(bcc.toArray(new String[]{}));
		}

		email.setSubject(subject);
		//email.setHtmlMsg(VelocityUtil.generate(templateName, templateData).toString());
		email.send();
	}
	
	public static void sendEMail(String from, String[] to, String[] cc, String[] bcc,
			String subject, String templateName, Map<String, Object> templateData) throws Exception {
			HtmlEmail email = new HtmlEmail();
			email.setHostName(EMAIL_HOST);
			
			email.setFrom(from);
			if (to != null) {
				email.addTo(to);
			}
			if (cc != null) {
				email.addCc(cc);
			}
			if (bcc != null) {
				email.addBcc(bcc);
			}

			email.setSubject(subject);
			//email.setHtmlMsg(VelocityUtil.generate(templateName, templateData).toString());
			email.send();
		}
}*/
