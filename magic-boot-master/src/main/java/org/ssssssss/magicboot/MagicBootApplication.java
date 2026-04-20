package org.ssssssss.magicboot;

import com.dtflys.forest.Forest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class MagicBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MagicBootApplication.class, args);
    }

}
