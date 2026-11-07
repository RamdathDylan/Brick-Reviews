package io.github.ramdathdylan.brickreviews;

import org.springframework.boot.SpringApplication;

public class TestBrickReviewsApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(BrickReviewsApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
