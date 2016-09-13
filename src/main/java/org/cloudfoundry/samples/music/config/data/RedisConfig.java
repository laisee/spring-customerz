package org.cloudfoundry.samples.music.config.data;

import org.cloudfoundry.samples.music.domain.Customer;
import org.cloudfoundry.samples.music.repositories.redis.RedisCustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Profile("redis")
public class RedisConfig {

    @Bean
    public RedisCustomerRepository redisRepository(RedisTemplate<String, Customer> redisTemplate) {
        return new RedisCustomerRepository(redisTemplate);
    }

    @Bean
    public RedisTemplate<String, Customer> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Customer> template = new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        RedisSerializer<Customer> customerSerializer = new Jackson2JsonRedisSerializer<>(Customer.class);

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(customerSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(customerSerializer);

        return template;
    }

}
