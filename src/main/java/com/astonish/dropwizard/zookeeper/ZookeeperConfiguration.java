/**
 * This file is a proprietary trade secret of ZyWave, Copyright 2015.
 *
 * @author Matt Carrier (matthew.carrier@zywave.com)
 * @created Sep 29, 2015
 */
package com.astonish.dropwizard.zookeeper;

import javax.validation.constraints.NotNull;

/**
 * 
 */
public class ZookeeperConfiguration {
    private String connectString = "127.0.0.1:2181";

    @NotNull
    private String namespace;

    @NotNull
    private String configurationFile;

    @NotNull
    private String configPath;

    /**
     * @return the configPath
     */
    public String getConfigPath() {
        return configPath;
    }

    /**
     * @param configPath
     *            the configPath to set
     */
    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    /**
     * @return the connectString
     */
    public String getConnectString() {
        return connectString;
    }

    /**
     * @param connectString
     *            the connectString to set
     */
    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    /**
     * @return the configurationFile
     */
    public String getConfigurationFile() {
        return configurationFile;
    }

    /**
     * @param configurationFile
     *            the configurationFile to set
     */
    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param namespace
     *            the namespace to set
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
