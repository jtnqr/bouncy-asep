package com.binaryneedle.bouncyasep.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.binaryneedle.bouncyasep.BouncyAsep;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
//                return new GwtApplicationConfiguration(true);
                // Fixed size application:
                return new GwtApplicationConfiguration(1024, 768);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new BouncyAsep();
        }
}