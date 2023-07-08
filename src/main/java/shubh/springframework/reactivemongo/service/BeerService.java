package shubh.springframework.reactivemongo.service;

import reactor.core.publisher.Mono;
import shubh.springframework.reactivemongo.model.BeerDTO;

public interface BeerService {

    Mono<BeerDTO> saveBeer(BeerDTO beerDTO);

    Mono<BeerDTO> getById(String beerId);

}
