package com.huangxunyi.utils;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class RemotingUtil {
    public static String parseSocketAddressToString(SocketAddress socketAddress) {
        if (socketAddress != null) {
            return doParse(socketAddress.toString().trim());
        }
        return "";
    }

    private static String doParse(String addr) {
        if (addr.isBlank()) {
            return "";
        }
        if (addr.charAt(0) == '/') {
            return addr.substring(1);
        } else {
            int len = addr.length();
            for (int i = 1; i < len; ++i) {
                if (addr.charAt(i) == '/') {
                    return addr.substring(i + 1);
                }
            }
            return addr;
        }
    }

    public static String parseRemoteAddress(final Channel channel) {
        if (null == channel) {
            return "";
        }
        final SocketAddress remote = channel.remoteAddress();
        return doParse(remote != null ? remote.toString().trim() : "");
    }

    public static String parseLocalAddress(final Channel channel) {
        if (null == channel) {
            return "";
        }
        final SocketAddress local = channel.localAddress();
        return doParse(local != null ? local.toString().trim() : "");
    }

    public static String parseRemoteIP(final Channel channel) {
        if (null == channel) {
            return "";
        }
        final InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        if (remote != null) {
            return remote.getAddress().getHostAddress();
        }
        return "";
    }

    public static int parseRemotePort(final Channel channel) {
        if (null == channel) {
            return -1;
        }
        final InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        if (remote != null) {
            return remote.getPort();
        }
        return -1;
    }
}
