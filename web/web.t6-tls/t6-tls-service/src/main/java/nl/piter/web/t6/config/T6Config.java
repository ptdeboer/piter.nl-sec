/* (C-left) 2015-2024 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t6.config;

import nl.piter.web.t6.controller.rest.T6Info;
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
