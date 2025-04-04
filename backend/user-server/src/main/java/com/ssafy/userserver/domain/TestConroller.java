package com.ssafy.userserver.domain;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestConroller {

	@GetMapping("/test1")
	public String test1() {
		return "test";
	}

	@GetMapping("/test2")
	public String test2() {
		return "test";
	}
}
