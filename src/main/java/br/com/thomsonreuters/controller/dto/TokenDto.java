package br.com.thomsonreuters.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDto {

	private String token;
	private String tipo;
}
