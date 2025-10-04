/* ----------------------------------------------------------------------------
 * (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * https://www.piter.nl/github
 * See LICENSE.txt for more details.
 * ----------------------------------------------------------------------------
 */
//
package nl.piter.web.t7.ft;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Start Spring app in controlled Test Environment for Functional Tests.
 */
public class WebAppStarter {

    private ConfigurableApplicationContext appContext;
    private final Class<?> startClass;

    public WebAppStarter(Class<?> startClass) {
        this.startClass = startClass;
    }

    public void start(String[] args) {
        startWithProfiles(null, args);
    }

    public void startWithProfiles(String[] profiles, String[] args) {
        SpringApplication app = new SpringApplication(startClass);
        if (profiles != null) {
            app.setAdditionalProfiles(profiles);
        }
        appContext = app.run(args);
    }

    public void stop() {
        appContext.stop();
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return this.appContext;
    }

}
