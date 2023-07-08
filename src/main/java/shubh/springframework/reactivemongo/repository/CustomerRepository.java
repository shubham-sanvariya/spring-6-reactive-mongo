package shubh.springframework.reactivemongo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import shubh.springframework.reactivemongo.domain.Customer;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String > {
}
