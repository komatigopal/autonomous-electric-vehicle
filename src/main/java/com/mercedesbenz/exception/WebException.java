/*
 * package com.mercedesbenz.exception;
 * 
 * import java.util.ArrayList; import java.util.List;
 * 
 * import com.mercedesbenz.model.Error;
 * 
 * public class WebException extends Exception { private String code;
 * 
 * public WebException(String code) { super(code); this.code = code; }
 * 
 * @Override public String getMessage() { if(code.equalsIgnoreCase("900")) {
 * 
 * } List<Error> errors = new ArrayList<Error>();
 * errors.add(Error.builder().id(9999).description("Technical Exception").build(
 * )); response.put("errors", errors); e.printStackTrace(); return
 * super.getMessage(); } }
 */