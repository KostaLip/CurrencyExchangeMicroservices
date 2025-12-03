package util.exceptions;

import org.springframework.http.HttpStatus;

public class ExceptionModel {

	private String errorMessage;
	private String reccomendation;
	private HttpStatus status;
		
	public ExceptionModel() {

	}

	public ExceptionModel(String errorMessage, String reccomendation, HttpStatus status) {
		this.errorMessage = errorMessage;
		this.reccomendation = reccomendation;
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getReccomendation() {
		return reccomendation;
	}
	
	public void setReccomendation(String reccomendation) {
		this.reccomendation = reccomendation;
	}
	
	public HttpStatus getStatus() {
		return status;
	}
	
	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	
}
