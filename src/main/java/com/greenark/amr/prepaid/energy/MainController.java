package com.greenark.amr.prepaid.energy;

import java.io.IOException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.greenark.amr.prepaid.energy.dto.FileUploadBean;
import com.greenark.amr.prepaid.energy.resources.SampleResource;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/main")
@Slf4j
public class MainController {

	@Autowired
	private MessageService messageService;

	@RequestMapping(value = "/write", method = RequestMethod.POST)
	public void write(@Valid @RequestBody SampleResource resource) throws IOException {

		messageService.sendMessage(resource.getName());
	}

	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public ResponseEntity<?> uploadFile(FileUploadBean bean) {
		try {
			messageService.uploadImage(bean);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping("/")
	public String getStatus() {
		return "Welcome...!";
	}

}
