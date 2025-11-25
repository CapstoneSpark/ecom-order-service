package com.example.order.Exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomErrorResponse {
    private String code;
    private String message;
	public CustomErrorResponse(String code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
}
