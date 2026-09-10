package dev.mustafa.mini_marqetplace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MiniMarqetplaceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniMarqetplaceApplication.class, args);
	}

}
