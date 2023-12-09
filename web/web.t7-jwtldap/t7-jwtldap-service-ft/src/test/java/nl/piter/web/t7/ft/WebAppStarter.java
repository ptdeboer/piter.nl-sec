/* (C) 2017-2023 Piter.NL
 * Use of this code allowed under restrictions. See LICENSE.txt for details.
 */
package nl.piter.web.t7.ft;

import nl.piter.web.t7.WebAppT7;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Start Spring app in controlled Test Environment for Functional Tests.
 */
public class WebAppStarter {

    private ConfigurableApplicationContext appContext;

    public void start(String[] args) {
        startWithProfiles(null, args);
    }

    public void startWithProfiles(String[] profiles, String[] args) {
        SpringApplication app = new SpringApplication(WebAppT7.class);
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
