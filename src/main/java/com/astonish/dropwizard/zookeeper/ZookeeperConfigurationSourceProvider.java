/**
 * This file is a proprietary trade secret of ZyWave, Copyright 2015.
 *
 * @author Matt Carrier (matthew.carrier@zywave.com)
 * @created Sep 30, 2015
 */
package com.astonish.dropwizard.zookeeper;

import static com.google.common.base.Preconditions.checkNotNull;
import io.dropwizard.Configuration;
import io.dropwizard.configuration.ConfigurationException;
import io.dropwizard.configuration.ConfigurationFactory;
import io.dropwizard.configuration.ConfigurationSourceProvider;
import io.dropwizard.configuration.DefaultConfigurationFactoryFactory;
import io.dropwizard.configuration.FileConfigurationSourceProvider;

import java.io.IOException;
import java.io.InputStream;

import javax.validation.Validator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 */
public class ZookeeperConfigurationSourceProvider<T extends Configuration> implements ConfigurationSourceProvider {
    private final Validator validator;
    private final ObjectMapper objectMapper;
    private final Class<T> configurationClass;

    private ConfigurationStore<T> store;
    private CuratorFramework client;

    public ZookeeperConfigurationSourceProvider(Validator validator, ObjectMapper objectMapper,
            Class<T> configurationClass) {
        this.validator = checkNotNull(validator);
        this.objectMapper = checkNotNull(objectMapper);
        this.configurationClass = checkNotNull(configurationClass);
    }

    /**
     * @return the store
     */
    ConfigurationStore<T> getStore() {
        return store;
    }

    /**
     * @return the client
     */
    CuratorFramework getClient() {
        return client;
    }

    /*
     * (non-Javadoc)
     * 
     * @see io.dropwizard.configuration.FileConfigurationSourceProvider#open(java.lang.String)
     */
    @Override
    public InputStream open(String zookeeperConfigurationPath) throws IOException {
        final ZookeeperConfiguration zConfig = parseZookeeperConfiguration(zookeeperConfigurationPath);
        client = createClient(zConfig);
        store = new ConfigurationStore<>(client, zConfig, objectMapper, configurationClass);
        return store.configurationAsStream();
    }

    /**
     * Creates a curator client.
     * @param configuration
     *            the zookeeper configuration
     * @return the curator client
     */
    private CuratorFramework createClient(ZookeeperConfiguration configuration) {
        final RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        final CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(configuration.getConnectString()).retryPolicy(retryPolicy)
                .namespace(configuration.getNamespace()).build();
        client.start();
        return client;
    }

    /**
     * Parses the zookeeper configuration
     * @param zookeeperConfigurationPath
     *            the path to the zookeeper configuration
     * @return the zookeeper configuration
     * @throws IOException
     *             if there was an issue accessing the zookeeper configuration
     */
    private ZookeeperConfiguration parseZookeeperConfiguration(String zookeeperConfigurationPath) throws IOException {
        if (null == zookeeperConfigurationPath) {
            throw new RuntimeException("You must specify a zookeeper configuration file");
        }

        final ConfigurationFactory<ZookeeperConfiguration> configurationFactory = new DefaultConfigurationFactoryFactory<ZookeeperConfiguration>()
                .create(ZookeeperConfiguration.class, validator, objectMapper, "dw");
        try {
            return configurationFactory.build(new FileConfigurationSourceProvider(), zookeeperConfigurationPath);
        } catch (ConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
