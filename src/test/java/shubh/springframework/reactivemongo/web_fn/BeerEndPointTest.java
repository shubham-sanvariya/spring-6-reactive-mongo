package shubh.springframework.reactivemongo.web_fn;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import shubh.springframework.reactivemongo.domain.Beer;
import shubh.springframework.reactivemongo.model.BeerDTO;
import shubh.springframework.reactivemongo.service.BeerServiceImplTest;

import java.util.List;

import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
class BeerEndPointTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void testDeleteBeerNotFound() {
        webTestClient.delete()
                .uri(BeerRouterConfig.BEER_PATH_ID,999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteBeer() {
        BeerDTO beerDTO = getSavedTestBeer();

        webTestClient.delete().uri(BeerRouterConfig.BEER_PATH_ID, beerDTO.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testPatchIdNotFound() {
        webTestClient.patch()
                .uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testPatchIdFound() {
        BeerDTO beerDTO = getSavedTestBeer();

        webTestClient.patch()
                .uri(BeerRouterConfig.BEER_PATH_ID, beerDTO.getId())
                .body(Mono.just(beerDTO), BeerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Order(4)
    void testUpdateBeerBadRequest() {
        BeerDTO testBeer = getSavedTestBeer();
        testBeer.setBeerStyle("");

        webTestClient.put()
                .uri(BeerRouterConfig.BEER_PATH_ID, testBeer)
                .body(Mono.just(testBeer), BeerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateBeerNotFound() {
        webTestClient.put()
                .uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateBeerBadData() {
        Beer testBeer = BeerServiceImplTest.getTestBeer();
        testBeer.setBeerName("");

        webTestClient.post().uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(testBeer), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Order(3)
    void testUpdateBeer() {
        BeerDTO beerDTO = getSavedTestBeer();
        beerDTO.setBeerName("new");

        webTestClient.put().uri(BeerRouterConfig.BEER_PATH_ID, beerDTO.getId())
                .body(Mono.just(beerDTO), BeerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testCreateBeer() {
        BeerDTO beerDTO = getSavedTestBeer();

        webTestClient.post().uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(beerDTO), BeerDTO.class)
                .header("Content-type","application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location");
    }

    @Test
    void testGetByIdNotFound() {
        webTestClient.get().uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(1)
    void testGetById() {
        BeerDTO beerDTO = getSavedTestBeer();

        webTestClient.get().uri(BeerRouterConfig.BEER_PATH_ID, beerDTO.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("content-type","application/json")
                .expectBody(BeerDTO.class);
    }

    @Order(2)
    @Test
    void testListBeersByStyle() {
        final String BEER_STYLE =  "TEST";
        BeerDTO testDto = getSavedTestBeer();
        testDto.setBeerStyle(BEER_STYLE);

        // create test data
        webTestClient.post().uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(testDto),BeerDTO.class)
                .header("Content-Type","application/json")
                .exchange();

        webTestClient.get().uri(UriComponentsBuilder
                .fromPath(BeerRouterConfig.BEER_PATH)
                .queryParam("beerStyle", BEER_STYLE).build().toUri())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type","application/json")
                .expectBody().jsonPath("$.size()").value(equalTo(1));

    }

    @Test
    @Order(2)
    void listBeers() {
        webTestClient.get().uri(BeerRouterConfig.BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.size()").value(greaterThan(1));
    }

    public BeerDTO getSavedTestBeer() {
        FluxExchangeResult<BeerDTO> beerDTOFluxExchangeResult = webTestClient.post()
                .uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .header("Content-Type","application/json")
                .exchange()
                .returnResult(BeerDTO.class);

        List<String> location = beerDTOFluxExchangeResult.getRequestHeaders().get("location");

        return webTestClient.get().uri(BeerRouterConfig.BEER_PATH)
                .exchange()
                .returnResult(BeerDTO.class)
                .getResponseBody().blockFirst();
    }
}