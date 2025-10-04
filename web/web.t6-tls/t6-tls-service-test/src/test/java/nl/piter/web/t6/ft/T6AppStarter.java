/* (C-left) 2015-2025 Piter.NL - Free of use, but keep this header.
 * See LICENSE.txt for more details.
 */
//
package nl.piter.web.t6.ft;

import nl.piter.web.t6.WebAppT6;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class T6AppStarter {

    private ConfigurableApplicationContext appContext;

    public void start(String[] args) {
        startWithProfiles(null, args);
    }

    public void startWithProfiles(String[] profiles, String[] args) {
        SpringApplication app = new SpringApplication(WebAppT6.class);
        if (profiles != null) {
            app.setAdditionalProfiles(profiles);
        }
        appContext = app.run(args);
    }

    public void stop() {
        if (appContext == null) {
            throw new RuntimeException("No Application Started");
        }
        appContext.stop();
    }

    public ConfigurableApplicationContext getApplicationContext() {
        return this.appContext;
    }

}
