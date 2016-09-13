package org.cloudfoundry.samples.music.repositories.mongodb;

import org.cloudfoundry.samples.music.domain.Customer;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mongodb")
public interface MongoCustomerRepository extends MongoRepository<Customer, String> {
}
