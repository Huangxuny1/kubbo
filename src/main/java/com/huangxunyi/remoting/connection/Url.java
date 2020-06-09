package com.huangxunyi.remoting.connection;

import com.huangxunyi.remoting.codec.Constants;
import lombok.extern.slf4j.Slf4j;

import java.lang.ref.SoftReference;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class Url {

    private String originUrl;

    private String ip;

    private int port;

    private String uniqueKey;

    private byte protocol;

    private byte version = Constants.VERSION_1;


    private boolean connWarmup;


    private Properties properties;


    protected Url(String originUrl) {
        this.originUrl = originUrl;
    }


    public Url(String ip, int port) {
        this(ip + ":" + port);
        this.ip = ip;
        this.port = port;
        this.uniqueKey = this.originUrl;
    }


    public Url(String originUrl, String ip, int port) {
        this(originUrl);
        this.ip = ip;
        this.port = port;
        this.uniqueKey = ip + ":" + port;
    }

    public Url(String originUrl, String ip, int port, Properties properties) {
        this(originUrl, ip, port);
        this.properties = properties;
    }


    public Url(String originUrl, String ip, int port, String uniqueKey, Properties properties) {
        this(originUrl, ip, port);
        this.uniqueKey = uniqueKey;
        this.properties = properties;
    }

    public int getConnNum() {
        return 1;
    }

    public String getProperty(String key) {
        if (properties == null) {
            return null;
        }
        return properties.getProperty(key);
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public byte getProtocol() {
        return protocol;
    }

    public void setProtocol(byte protocol) {
        this.protocol = protocol;
    }

    public boolean isConnWarmup() {
        return connWarmup;
    }

    public void setConnWarmup(boolean connWarmup) {
        this.connWarmup = connWarmup;
    }

    public Properties getProperties() {
        return properties;
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Url url = (Url) obj;
        if (this.getOriginUrl().equals(url.getOriginUrl())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.getOriginUrl() == null) ? 0 : this.getOriginUrl().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("Origin url [").append(this.originUrl).append("], Unique key [")
                .append(this.uniqueKey).append("].");
        return sb.toString();
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }
}