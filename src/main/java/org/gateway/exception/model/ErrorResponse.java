package org.gateway.exception.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {

	private String message;
    private int status;
    private LocalDateTime timestamp;
}
