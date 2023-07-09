package shubh.springframework.reactivemongo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import shubh.springframework.reactivemongo.domain.Beer;
import shubh.springframework.reactivemongo.mappers.BeerMapper;
import shubh.springframework.reactivemongo.mappers.BeerMapperImpl;
import shubh.springframework.reactivemongo.model.BeerDTO;
import shubh.springframework.reactivemongo.repository.BeerRepository;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.await;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BeerServiceImplTest {

    @Autowired
    BeerService beerService;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    BeerDTO beerDTO;
    @BeforeEach
    void setUp() {
        beerDTO = beerMapper.beerToBeerDto(getTestBeer());
    }

    @Test
    void findByBeerStyle() {
        BeerDTO beerDto = getsavedBeerDto();

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        beerService.findByBeerStyle(beerDto.getBeerStyle())
                .subscribe(dto -> {
                    System.out.println(dto.toString());
                    atomicBoolean.set(true);
                });

        await().untilTrue(atomicBoolean);
    }

    @Test
    void findFirstByBeerName() {
        BeerDTO beerDTO  = getsavedBeerDto();

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        Mono<BeerDTO> foundDto = beerService.findFirstByBeerName(beerDTO.getBeerName());

        foundDto.subscribe(dto -> {
            System.out.println(dto.toString());
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @DisplayName("Test save Beer using subscriber")
    void saveBeer() throws InterruptedException{

        AtomicBoolean atomicBoolean  = new AtomicBoolean(false);
        AtomicReference<BeerDTO> atomicDto = new AtomicReference<>();

        Mono<BeerDTO> savedMono = beerService.saveBeer(Mono.just(beerDTO));

        savedMono.subscribe(savedDto -> {
            System.out.println(savedDto.getId());
            atomicBoolean.set(true);
            atomicDto.set(savedDto);
        });

        await().untilTrue(atomicBoolean);
    }

    @Test
    @DisplayName("Test save Beer using block")
    void testSaveBeerUseBlock() {
        BeerDTO beerDto = beerService.saveBeer(Mono.just(getTestBeerDto())).block();
        assertThat(beerDto).isNotNull();
        assertThat(beerDto.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test update Beer using Block")
    void updateBeerUseBlock() {
        final String newName = "New Beer Name";  // use final so cannot mutate
        BeerDTO savedBeerDto = getTestBeerDto();
        savedBeerDto.setBeerName(newName);

        BeerDTO updateBeer  = beerService.saveBeer(Mono.just(savedBeerDto)).block();

        // Verify exist in db
        BeerDTO fetchedDto = beerService.getById(updateBeer.getId()).block();
        assertThat(fetchedDto.getBeerName()).isEqualTo(newName);

    }

    @Test
    @DisplayName("Test Update Using Reactive Streams")
    void testUpdateStreaming() {
        final String newName = "New Beer Name";  // use final so cannot mutate

        AtomicReference<BeerDTO> atomicDto = new AtomicReference<>();

        beerService.saveBeer(Mono.just(getTestBeerDto()))
                .map(savedBeerDto -> {
                    savedBeerDto.setBeerName(newName);
                    return savedBeerDto;
                })
                .flatMap(beerService::saveBeer) // save updated beer
                .flatMap(savedUpdatedDto -> beerService.getById(savedUpdatedDto.getId())) // get from db
                .subscribe(dtoFromDb -> {
                    atomicDto.set(dtoFromDb);
                });

        await().until(() -> atomicDto.get() != null);
        assertThat(atomicDto.get().getBeerName()).isEqualTo(newName);
    }

    @Test
    void testDeleteBeer() {
        BeerDTO beerToDelete = getsavedBeerDto();

        beerService.deleteBeerById(beerToDelete.getId()).block();

        Mono<BeerDTO> expectedEmptyBeerMono = beerService.getById(beerToDelete.getId());

        BeerDTO emptyBeer = expectedEmptyBeerMono.block();

        assertThat(emptyBeer).isNull();

    }

    public static Beer getTestBeer() {
        return Beer.builder()
                .beerName("space dust")
                .beerStyle("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .upc("123213")
                .build();

    }

    public  BeerDTO getsavedBeerDto() {
        return beerService.saveBeer(Mono.just(getTestBeerDto())).block();
    }

    public static BeerDTO getTestBeerDto(){
        return new BeerMapperImpl().beerToBeerDto(getTestBeer());
    }
}