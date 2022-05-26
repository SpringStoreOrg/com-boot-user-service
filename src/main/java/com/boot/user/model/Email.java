package com.boot.user.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class Email {

	private String emailFrom;

	private String emailTo;

	private String emailCc;

	private String emailBcc;

	private String emailSubject;

	private String emailContent;

	private String contentType;

	private List<Object> attachments;

	private Map<String, Object> model;

	public Email() {
		contentType = "text/plain";
	}
}
