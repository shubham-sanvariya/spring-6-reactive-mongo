package shubh.springframework.reactivemongo.service;

import reactor.core.publisher.Mono;
import shubh.springframework.reactivemongo.model.BeerDTO;

public class BeerServiceImpl implements BeerService {
    @Override
    public Mono<BeerDTO> saveBeer(BeerDTO beerDTO) {
        return null;
    }

    @Override
    public Mono<BeerDTO> getById(String beerId) {
        return null;
    }
}
