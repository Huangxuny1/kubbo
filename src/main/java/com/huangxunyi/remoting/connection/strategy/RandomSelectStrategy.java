package com.huangxunyi.remoting.connection.strategy;

import com.huangxunyi.remoting.connection.Connection;
import com.huangxunyi.remoting.connection.strategy.ConnectionSelectStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;

@Slf4j
public class RandomSelectStrategy implements ConnectionSelectStrategy {

    private static final int MAX_TIMES = 5;
    private final Random random = new Random();

    public RandomSelectStrategy() {
    }

    @Override
    public Connection select(List<Connection> connections) {
        try {
            if (connections == null) {
                return null;
            }
            int size = connections.size();
            if (size == 0) {
                return null;
            }

            Connection result;
            result = randomGet(connections);
            return result;
        } catch (Throwable e) {
            log.error("Choose connection failed using RandomSelectStrategy!", e);
            return null;
        }
    }

    /**
     * get one connection randomly
     *
     * @param connections source connections
     * @return result connection
     */
    private Connection randomGet(List<Connection> connections) {
        if (null == connections || connections.isEmpty()) {
            return null;
        }

        int size = connections.size();
        int tries = 0;
        Connection result = null;
        while ((result == null || !result.isFine()) && tries++ < MAX_TIMES) {
            result = connections.get(this.random.nextInt(size));
        }

        if (result != null && !result.isFine()) {
            result = null;
        }
        return result;
    }
}
