package com.ocean.tigaapi;

import org.noear.solon.Solon;
import org.noear.solon.core.event.AppLoadEndEvent;

import com.ocean.tigaapi.engine.sql.EngineWarmup;

public class App {
    public static void main(String[] args) {
        Solon.start(App.class, args, app -> {
            app.onEvent(AppLoadEndEvent.class, e -> {
            	EngineWarmup.init();
            });
            app.enableWebSocket(true);
        });
    }
}