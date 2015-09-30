/**
 * This file is a proprietary trade secret of ZyWave, Copyright 2015.
 *
 * @author Matt Carrier (matthew.carrier@zywave.com)
 * @created Sep 28, 2015
 */
package com.astonish.dropwizard.zookeeper;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A zookeeper cache of a configuration file
 */
public class ConfigurationStore<T> {
    private final ObjectMapper mapper;
    private final Class<T> configurationClass;
    private final PathChildrenCache cache;
    private final String configPath;

    public ConfigurationStore(CuratorFramework client, ZookeeperConfiguration zConfig, ObjectMapper mapper,
            Class<T> configurationClass) {
        this.mapper = checkNotNull(mapper);
        this.configurationClass = checkNotNull(configurationClass);
        this.configPath = checkNotNull(zConfig.getConfigPath());
        try {
            client.create().forPath(configPath, Files.readAllBytes(Paths.get(zConfig.getConfigurationFile())));
            this.cache = new PathChildrenCache(client, configPath, true);
            this.cache.start(StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception e) {
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @return the configuration file
     */
    public T configuration() {
        try {
            return mapper.readValue(configurationAsStream(), configurationClass);
        } catch (Exception e) {
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @return the configuration file as an InputStream
     */
    InputStream configurationAsStream() {
        try {
            return new ByteArrayInputStream(cache.getCurrentData().get(0).getData());
        } catch (Exception e) {
            throw new WebApplicationException(e, Status.INTERNAL_SERVER_ERROR);
        }
    }
}
