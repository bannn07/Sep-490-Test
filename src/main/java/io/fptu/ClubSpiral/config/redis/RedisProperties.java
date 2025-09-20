package io.fptu.ClubSpiral.config.redis;

import lombok.Data;

@Data
public class RedisProperties {

    private int port;
    private String host;
    private int timeout;
    private int database;

}