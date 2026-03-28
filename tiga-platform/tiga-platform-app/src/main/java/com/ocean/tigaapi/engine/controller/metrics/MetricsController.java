package com.ocean.tigaapi.engine.controller.metrics;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;

import com.ocean.tigaapi.engine.groovy.GroovyMonitor;
import com.ocean.tigaapi.engine.magic.MagicMonitor;

@Controller
public class MetricsController {

    @Get
    @Mapping("/groovy/metrics")
    public String groovyMetrics() {
    	// Prometheus 会定时来拉取这个接口返回的文本
        return GroovyMonitor.getPrometheusMetrics();
    }
    @Get
    @Mapping("/magic/metrics")
    public String magicMetrics() {
    	// Prometheus 会定时来拉取这个接口返回的文本
    	return MagicMonitor.getPrometheusMetrics();
    }
    
    
}