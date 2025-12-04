package com.example.order.Exception;

public class CustomErrorResponse {
    private String code;
    private String message;
    
    // Empty constructor
    public CustomErrorResponse() {
    }
    
    // Constructor with parameters
    public CustomErrorResponse(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }
    
    // Getter for code
    public String getCode() {
        return code;
    }
    
    // Setter for code
    public void setCode(String code) {
        this.code = code;
    }
    
    // Getter for message
    public String getMessage() {
        return message;
    }
    
    // Setter for message
    public void setMessage(String message) {
        this.message = message;
    }
}