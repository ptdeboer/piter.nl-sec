/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point to SpringBoot application.
 */
@SpringBootApplication
public class WebApp {

    /**
     * Method initializes and starts SpringBoot application
     */
    public static void main(String[] args) {
        SpringApplication.run(WebApp.class, args);
    }

}
