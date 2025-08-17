package io.fptu.sep490.config.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean("redis-properties")
    @ConditionalOnMissingBean(name = "redis-properties")
    @ConfigurationProperties(prefix = "spring.redis-sep490")
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @Bean("redis-sep490-cache")
    @ConditionalOnMissingBean(name = "redis-sep490-cache")
    public RedisConnectionFactory redisConnectionFactory(@Qualifier("redis-properties") RedisProperties redisProperties) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());
        configuration.setDatabase(redisProperties.getDatabase());

        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("redis-sep490-cache") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return redisTemplate;
    }
}