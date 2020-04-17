/* (C) 2020 Piter.NL - free of use. */
package nl.piter.web.t6.config;

import nl.piter.web.t6.service.T6Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"nl.piter.web.t6"})
public class T6Config {

    @Bean
    public T6Info t6Info() {
        return new T6Info("T6 TLS-ma", "v1");
    }

}
