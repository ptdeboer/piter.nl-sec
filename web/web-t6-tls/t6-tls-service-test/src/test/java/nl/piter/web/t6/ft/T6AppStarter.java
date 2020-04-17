/* (C) 2020 Piter.NL - free of use. */
package nl.piter.web.t6.ft;

import nl.piter.web.t6.T6App;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class T6AppStarter {

    private ConfigurableApplicationContext appContext;

    public void start(String[] args) {
        startWithProfiles(null, args);
    }

    public void startWithProfiles(String[] profiles, String[] args) {
        SpringApplication app = new SpringApplication(T6App.class);
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
