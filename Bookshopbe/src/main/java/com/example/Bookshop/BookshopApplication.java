package com.example.Bookshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BookshopApplication {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
	}

	public static void main(String[] args) {
		System.out.println("Java timezone = " + TimeZone.getDefault().getID());
		SpringApplication.run(BookshopApplication.class, args);
	}

}
