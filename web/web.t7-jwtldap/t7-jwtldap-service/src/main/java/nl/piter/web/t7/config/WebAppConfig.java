/*/
 */ (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 */ https://www.piter.nl/github
 */ See LICENSE.txt for more details.
 */
//
package nl.piter.web.t7.config;

import nl.piter.web.t7.service.WebAppInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Web Application configurations.
 */
@Configuration
@ComponentScan(basePackages = {"nl.piter.web.t7"})
@PropertySource("classpath:application.properties")
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"nl.piter.web.t7.dao.repositories"})
@EntityScan(basePackages = {"nl.piter.web.t7.dao.entities"})
public class WebAppConfig {

    @Value("${webapp.application.name}")
    private String applicationName;

    /**
     * Compiled into template properties by Maven:
     */
    @Value("${webapp.build.version}")
    private String buildVersion;

    /**
     * Compiled into template properties by Maven:
     */
    @Value("${webapp.build.timestamp}")
    private String buildTimestamp;

    @Bean
    public WebAppInfo t7Info() {
        return new WebAppInfo(applicationName, buildVersion, buildTimestamp);
    }

}
