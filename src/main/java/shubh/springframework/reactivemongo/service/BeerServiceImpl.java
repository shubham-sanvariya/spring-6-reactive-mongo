package shubh.springframework.reactivemongo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import shubh.springframework.reactivemongo.mappers.BeerMapper;
import shubh.springframework.reactivemongo.model.BeerDTO;
import shubh.springframework.reactivemongo.repository.BeerRepository;

@Service
@RequiredArgsConstructor
public class BeerServiceImpl implements BeerService {

    private final BeerMapper beerMapper;
    private final BeerRepository beerRepository;

    @Override
    public Mono<BeerDTO> findFirstByBeerName(String beerName) {
        return beerRepository.findFirstByBeerName(beerName)
                .map(beerMapper::beerToBeerDto);
    }

    @Override
    public Flux<BeerDTO> listBeers() {
        return beerRepository.findAll()
                .map(beerMapper::beerToBeerDto);
    }

    @Override
    public Mono<BeerDTO> saveBeer(Mono<BeerDTO> beerDto) {
        return beerDto.map(beerMapper::beerDtoToBeer)
                .flatMap(beerRepository::save)
                .map(beerMapper::beerToBeerDto);
    }

    @Override
    public Mono<BeerDTO> saveBeer(BeerDTO beerDTO) {
        return beerRepository.save(beerMapper.beerDtoToBeer(beerDTO))
                .map(beerMapper::beerToBeerDto);
    }

    @Override
    public Mono<BeerDTO> getById(String beerId) {
        return beerRepository.findById(beerId)
                .map(beerMapper::beerToBeerDto);
    }

    @Override
    public Mono<BeerDTO> updateBeer(String beerId, BeerDTO beerDTO) {
        return beerRepository.findById(beerId)
                .map(updatingBeer -> {
                    updatingBeer.setBeerName(beerDTO.getBeerName());
                    updatingBeer.setBeerStyle(beerDTO.getBeerStyle());
                    updatingBeer.setPrice(beerDTO.getPrice());
                    updatingBeer.setUpc(beerDTO.getUpc());
                    updatingBeer.setQuantityOnHand(beerDTO.getQuantityOnHand());

                    return updatingBeer;
                })
                .flatMap(beerRepository::save)
                .map(beerMapper::beerToBeerDto);
    }

    // upb = updatingPartBeer
    @Override
    public Mono<BeerDTO> patchBeer(String beerId, BeerDTO beerDTO) {
        return beerRepository.findById(beerId)
                .map(upb -> {
                    if (StringUtils.hasText(beerDTO.getBeerName())){
                        upb.setBeerName(beerDTO.getBeerName());
                    }

                    if(StringUtils.hasText(beerDTO.getBeerStyle())){
                        upb.setBeerStyle(beerDTO.getBeerStyle());
                    }

                    if(beerDTO.getPrice() != null){
                        upb.setPrice(beerDTO.getPrice());
                    }

                    if(StringUtils.hasText(beerDTO.getUpc())){
                        upb.setUpc(beerDTO.getUpc());
                    }

                    if(beerDTO.getQuantityOnHand() != null){
                        upb.setQuantityOnHand(beerDTO.getQuantityOnHand());
                    }
                    return upb;
                })
                .flatMap(beerRepository::save)
                .map(beerMapper::beerToBeerDto);
    }

    @Override
    public Mono<Void> deleteBeerById(String beerId) {
        return beerRepository.deleteById(beerId);
    }
}
