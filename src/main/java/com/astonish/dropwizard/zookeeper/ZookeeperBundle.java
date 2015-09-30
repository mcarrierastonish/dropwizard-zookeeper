/**
 * This file is a proprietary trade secret of ZyWave, Copyright 2015.
 *
 * @author Matt Carrier (matthew.carrier@zywave.com)
 * @created Sep 29, 2015
 */
package com.astonish.dropwizard.zookeeper;

import org.apache.curator.framework.CuratorFramework;

import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.util.Generics;

/**
 * Retrieves a dropwizard configuration file from a zookeeper instance and provides a curator client.
 */
public abstract class ZookeeperBundle<T extends Configuration> implements ConfiguredBundle<T> {
    private Class<T> configurationClass;
    private ConfigurationStore<T> store;
    private CuratorFramework client;

    public ZookeeperBundle() {
        this.configurationClass = Generics.getTypeParameter(getClass(), Configuration.class);
    }

    /**
     * @return the store
     */
    public ConfigurationStore<T> getStore() {
        return store;
    }

    /**
     * @return the client
     */
    public CuratorFramework getClient() {
        return client;
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.dropwizard.ConfiguredBundle#initialize(io.dropwizard.setup.Bootstrap)
     */
    @Override
    public void initialize(Bootstrap<?> bootstrap) {
        final ZookeeperConfigurationSourceProvider<T> provider = new ZookeeperConfigurationSourceProvider<>(bootstrap
                .getValidatorFactory().getValidator(), bootstrap.getObjectMapper(), configurationClass);
        store = provider.getStore();
        client = provider.getClient();
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.dropwizard.ConfiguredBundle#run(java.lang.Object, io.dropwizard.setup.Environment)
     */
    @Override
    public void run(T configuration, Environment environment) throws Exception {

    }
}
