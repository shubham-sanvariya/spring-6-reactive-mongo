package shubh.springframework.reactivemongo.mappers;

import org.mapstruct.Mapper;
import shubh.springframework.reactivemongo.domain.Beer;
import shubh.springframework.reactivemongo.model.BeerDTO;

@Mapper
public interface BeerMapper {
    Beer beerDtoToBeer(BeerDTO beerDTO);

    BeerDTO beerToBeerDto(Beer beer);
}
