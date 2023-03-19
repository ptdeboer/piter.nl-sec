/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.dao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Test app to set up full spring context enabled application.
 * If tests get to big, better move them out in a separate -dao-test-it module.
 */
@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"nl.piter.web.t7"})
@PropertySource("classpath:application-ittest.properties")
@EnableJpaRepositories(basePackages = {"nl.piter.web.t7.dao"})
@EntityScan(basePackages = {"nl.piter.web.t7.dao.entities"})
public class DaoTestApp {

    public static void main(String[] args) {
        SpringApplication.run(DaoTestApp.class, args);
    }

}
