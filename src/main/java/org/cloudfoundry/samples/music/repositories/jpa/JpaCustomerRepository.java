package org.cloudfoundry.samples.music.repositories.jpa;

import org.cloudfoundry.samples.music.domain.Customer;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Profile({"in-memory", "mysql", "postgres", "oracle"})
public interface JpaCustomerRepository extends JpaRepository<Customer, String> {
}
