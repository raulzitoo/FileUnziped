package com.hamburgsud.FileUnziped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.hamburgsud.FileUnziped.Service.FileUnzipedService;


@SpringBootApplication
public class FileUnzipedApplication implements CommandLineRunner {

	@Autowired
	FileUnzipedService service;
	
	public static void main(String[] args) {
		SpringApplication.run(FileUnzipedApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		service.Unziped();
		
	}
}
