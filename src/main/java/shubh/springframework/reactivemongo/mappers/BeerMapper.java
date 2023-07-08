package shubh.springframework.reactivemongo.mappers;

import org.mapstruct.Mapper;
import shubh.springframework.reactivemongo.domain.Beer;
import shubh.springframework.reactivemongo.model.BeerDTO;

@Mapper
public interface BeerMapper {
    Beer BeerDtoToBeer(BeerDTO beerDTO);

    BeerDTO BeerToBeerDto(Beer beer);
}
