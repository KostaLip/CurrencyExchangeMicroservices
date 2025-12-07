package util.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.dao.DataIntegrityViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<?> handleHttpClientException(HttpClientErrorException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				new ExceptionModel(
						fineTuneMessage(e.getMessage()), "Requested currencies not found", HttpStatus.NOT_FOUND));
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> handleRoleException(DataIntegrityViolationException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				new ExceptionModel("Invalid role specified", "Role must be one of: USER, ADMIN, OWNER",
						HttpStatus.BAD_REQUEST));
	}
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<?> handleMissingRequestParam(MissingServletRequestParameterException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				new ExceptionModel(
						e.getMessage(), "Make sure to enter all request parameters", HttpStatus.BAD_REQUEST));
	}
	
	@ExceptionHandler(AdminUpdateException.class)
	public ResponseEntity<?> handleAdminUpdateException(AdminUpdateException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				new ExceptionModel(
						e.getMessage(), "Update user with USER role", HttpStatus.BAD_REQUEST));
	}
	
	@ExceptionHandler(NoDataFoundException.class)
	public ResponseEntity<?> handleInvalidExchangeRate(NoDataFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				new ExceptionModel(
						e.getMessage(), String.format("Please make sure to enter currency from the list: %s",
								e.getCurrencies()), HttpStatus.NOT_FOUND));
	}
	
	@ExceptionHandler(CurrencyDoesntExistException.class)
	public ResponseEntity<?> handleInvalidCurrency(CurrencyDoesntExistException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
				new ExceptionModel(
						e.getMessage(), String.format("Please make sure to enter currency from the list: %s",
								e.getCurrencies()), HttpStatus.NOT_FOUND));
	}
	
	@ExceptionHandler(InvalidQuantityException.class)
	public ResponseEntity<?> handleInvalidQuantity(InvalidQuantityException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
				new ExceptionModel(
						e.getMessage(), "You can exchange up to 200 units of a single currancy", 
								HttpStatus.BAD_REQUEST));
	}
	
	public String fineTuneMessage(String message) {
		String[] partsOfTheMessage = message.split("\"");
		return partsOfTheMessage[6];
	}
	
}
