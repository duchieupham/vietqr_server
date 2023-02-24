package com.vietqr.org.dto;

import java.io.Serializable;
import java.util.List;

public class VietQRCreateListDTO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private List<VietQRCreateDTO> dtos;

	public VietQRCreateListDTO() {
		super();
	}

	public VietQRCreateListDTO(List<VietQRCreateDTO> dtos) {
		super();
		this.dtos = dtos;
	}

	public List<VietQRCreateDTO> getDtos() {
		return dtos;
	}

	public void setDtos(List<VietQRCreateDTO> dtos) {
		this.dtos = dtos;
	}


}
