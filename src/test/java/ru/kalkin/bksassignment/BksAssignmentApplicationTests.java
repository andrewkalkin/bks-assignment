package ru.kalkin.bksassignment;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import pl.zankowski.iextrading4j.client.IEXCloudClient;
import pl.zankowski.iextrading4j.client.rest.endpoint.GenericRestEndpoint;
import pl.zankowski.iextrading4j.client.rest.manager.RestClient;
import pl.zankowski.iextrading4j.client.rest.manager.RestClientMetadata;
import pl.zankowski.iextrading4j.client.rest.manager.RestManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BksAssignmentApplicationTests {

    private static ClientAndServer mockServer;

    @Value("${iexcloud.user.publishableToken}")
    private String token;

    @Autowired
    private IEXCloudClient cloudClient;

    @Autowired
    private MockMvc mockMvc;

    @BeforeClass
    public static void startServer() {
        mockServer = startClientAndServer(10080);
    }

    @AfterClass
    public static void stopServer() {
        mockServer.stop();
    }

    private void createExpectationForIexApi() throws IOException {
        MockServerClient mockServerClient = new MockServerClient("127.0.0.1", 10080);

        mockServerClient.when(
                HttpRequest.request()
                        .withMethod("GET")
                        .withPath("/v1/ref-data/symbols")
                        .withQueryStringParameter("token", token),
                Times.exactly(1))
                .respond(HttpResponse.response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8")
                                )
                        .withBody(IOUtils.resourceToString("/tests/symbols.json", StandardCharsets.UTF_8))
                );


        for (String symbol: Arrays.asList("aapl", "hog", "mdso", "idra", "mrsn", "aaww", "abcb", "abev", "yndx", "pcti")) {
            mockServerClient.when(HttpRequest.request()
                            .withMethod("GET")
                            .withPath(String.format("/v1/stock/%s/quote", symbol))
                            .withQueryStringParameter("token", token),
                    Times.exactly(1))
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withHeaders(
                                    new Header("Content-Type", "application/json; charset=utf-8")
                            )
                            .withBody(IOUtils.resourceToString(String.format("/tests/iexcloudResponse_%sQuote.json", symbol.toLowerCase()), StandardCharsets.UTF_8))
                    );
        }

        for (String symbol: Arrays.asList("aapl", "hog", "mdso", "idra", "mrsn", "aaww", "abcb", "abev", "yndx", "pcti")) {
            mockServerClient.when(HttpRequest.request()
                            .withMethod("GET")
                            .withPath(String.format("/v1/stock/%s/company", symbol))
                            .withQueryStringParameter("token", token),
                    Times.exactly(1))
                    .respond(HttpResponse.response()
                            .withStatusCode(200)
                            .withHeaders(
                                    new Header("Content-Type", "application/json; charset=utf-8")
                            )
                            .withBody(IOUtils.resourceToString(String.format("/tests/iexcloudResponse_%sCompany.json", symbol.toLowerCase()), StandardCharsets.UTF_8))
                    );
        }
    }

    @Before
    public void BeforeTest() throws IOException {

        GenericRestEndpoint genericRestEndpoint = (GenericRestEndpoint) ReflectionTestUtils.getField(cloudClient, "genericRestEndpoint");
        RestManager restManager = (RestManager) ReflectionTestUtils.getField(genericRestEndpoint, "restManager");
        RestClient restClient = (RestClient) ReflectionTestUtils.getField(restManager, "restClient");
        RestClientMetadata restClientMetadata = restClient.getRestClientMetadata();
        ReflectionTestUtils.setField(restClientMetadata, "url", "http://localhost:10080/v1");

        createExpectationForIexApi();
    }

    @Test
    public void test1() throws Exception{
        Thread.sleep(2000);

        mockMvc.perform(
                post("/assets")
                        .content(
                                IOUtils.resourceToString("/tests/test1/testRequest.json", StandardCharsets.UTF_8)
                        )
                .header("Content-Type", "application/json; charset=utf-8")

        ).andDo(
                print()
        ).andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(content().json(IOUtils.resourceToString("/tests/test1/testResponse.json", StandardCharsets.UTF_8)));
    }

    @Test
    public void test2() throws Exception{

        Thread.sleep(2000);

        mockMvc.perform(
                post("/assets")
                        .content(
                                IOUtils.resourceToString("/tests/test2/testRequest.json", StandardCharsets.UTF_8)
                        )
                        .header("Content-Type", "application/json; charset=utf-8")

        ).andDo(
                print()
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().json(IOUtils.resourceToString("/tests/test2/testResponse.json", StandardCharsets.UTF_8)));
    }

    @Test
    public void test3() throws Exception{

        Thread.sleep(2000);

        mockMvc.perform(
                post("/assets")
                        .content(
                                IOUtils.resourceToString("/tests/test3/testRequest.json", StandardCharsets.UTF_8)
                        )
                        .header("Content-Type", "application/json; charset=utf-8")

        ).andDo(
                print()
        ).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(content().json(IOUtils.resourceToString("/tests/test3/testResponse.json", StandardCharsets.UTF_8)));
    }

}
