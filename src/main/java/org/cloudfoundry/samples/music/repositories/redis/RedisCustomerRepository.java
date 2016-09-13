package org.cloudfoundry.samples.music.repositories.redis;

import org.cloudfoundry.samples.music.domain.Customer;
import org.cloudfoundry.samples.music.domain.RandomIdGenerator;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RedisCustomerRepository implements CrudRepository<Customer, String> {
    public static final String CUSTOMERS_KEY = "customers";

    private final RandomIdGenerator idGenerator;
    private final HashOperations<String, String, Customer> hashOps;

    public RedisCustomerRepository(RedisTemplate<String, Customer> redisTemplate) {
        this.hashOps = redisTemplate.opsForHash();
        this.idGenerator = new RandomIdGenerator();
    }

    @Override
    public <S extends Customer> S save(S customer) {
        if (customer.getId() == null) {
            customer.setId(idGenerator.generateId());
        }

        hashOps.put(CUSTOMERS_KEY, customer.getId(), customer);

        return customer;
    }

    @Override
    public <S extends Customer> Iterable<S> save(Iterable<S> customers) {
        List<S> result = new ArrayList<>();

        for (S entity : customers) {
            save(entity);
            result.add(entity);
        }

        return result;
    }

    @Override
    public Customer findOne(String id) {
        return hashOps.get(CUSTOMERS_KEY, id);
    }

    @Override
    public boolean exists(String id) {
        return hashOps.hasKey(CUSTOMERS_KEY, id);
    }

    @Override
    public Iterable<Customer> findAll() {
        return hashOps.values(CUSTOMERS_KEY);
    }

    @Override
    public Iterable<Customer> findAll(Iterable<String> ids) {
        return hashOps.multiGet(CUSTOMERS_KEY, convertIterableToList(ids));
    }

    @Override
    public long count() {
        return hashOps.keys(CUSTOMERS_KEY).size();
    }

    @Override
    public void delete(String id) {
        hashOps.delete(CUSTOMERS_KEY, id);
    }

    @Override
    public void delete(Customer customer) {
        hashOps.delete(CUSTOMERS_KEY, customer.getId());
    }

    @Override
    public void delete(Iterable<? extends Customer> customers) {
        for (Customer customer : customers) {
            delete(customer);
        }
    }

    @Override
    public void deleteAll() {
        Set<String> ids = hashOps.keys(CUSTOMERS_KEY);
        for (String id : ids) {
            delete(id);
        }
    }

    private <T> List<T> convertIterableToList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T object : iterable) {
            list.add(object);
        }
        return list;
    }
}
