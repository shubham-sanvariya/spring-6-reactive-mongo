package shubh.springframework.reactivemongo.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import shubh.springframework.reactivemongo.domain.Beer;

public interface BeerRepository extends ReactiveMongoRepository<Beer, String > {
}
