package org.arabellan.tdas;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.arabellan.tdas.model.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MetricsService {

    private final StatsDClient client;

    @Inject
    MetricsService(Configuration config) {
        String prefix = config.getMetrics().getPrefix();
        String address = config.getMetrics().getAddress();
        int port = config.getMetrics().getPort();
        this.client = new NonBlockingStatsDClient(prefix, address, port);
    }

    public void increment(String name) {
        client.increment(name);
    }

    public void gauge(String name, long value) {
        client.gauge(name, value);
    }

    public void timing(String name, long value) {
        client.time(name, value);
    }
}
