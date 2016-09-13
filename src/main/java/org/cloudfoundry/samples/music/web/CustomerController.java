package org.cloudfoundry.samples.music.web;

import org.cloudfoundry.samples.music.domain.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/customers")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private CrudRepository<Customer, String> repository;

    @Autowired
    public CustomerController(CrudRepository<Customer, String> repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Customer> customers() {
        return repository.findAll();
    }

    @RequestMapping(method = RequestMethod.PUT)
    public Customer add(@RequestBody @Valid Customer customer) {
        logger.info("Adding customer " + customer.getId());
        return repository.save(customer);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Customer update(@RequestBody @Valid Customer customer) {
        logger.info("Updating customer " + customer.getId());
        return repository.save(customer);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Customer getById(@PathVariable String id) {
        logger.info("Getting customer " + id);
        return repository.findOne(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteById(@PathVariable String id) {
        logger.info("Deleting customer " + id);
        repository.delete(id);
    }
}
