package org.ssssssss.magicboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MagicBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MagicBootApplication.class, args);
    }

}
