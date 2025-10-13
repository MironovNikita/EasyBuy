package com.shop.easybuy.container;

import com.redis.testcontainers.RedisContainer;
import org.testcontainers.utility.DockerImageName;

public class CommonRedisContainer {

    public static final RedisContainer REDIS = new RedisContainer(
            DockerImageName.parse("redis:7.4.2-bookworm"))
            .withExposedPorts(6379)
            .withReuse(true)
            .withCreateContainerCmdModifier(cmd -> cmd.withName("test-redis-container"));

    static {
        REDIS.start();
    }

    public static String getRedisUrl() {
        return String.format("redis://%s:%d", getHost(), getPort());
    }

    public static String getHost() {
        return REDIS.getHost();
    }

    public static Integer getPort() {
        return REDIS.getFirstMappedPort();
    }
}
