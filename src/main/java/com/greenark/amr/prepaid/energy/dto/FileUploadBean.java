package com.greenark.amr.prepaid.energy.dto;

import org.springframework.web.multipart.MultipartFile;

public class FileUploadBean {

	private MultipartFile file;

	public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

}
