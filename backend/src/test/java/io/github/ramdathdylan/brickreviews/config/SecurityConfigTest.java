package io.github.ramdathdylan.brickreviews.config;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import io.github.ramdathdylan.brickreviews.TestcontainersConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)  
class SecurityConfigTest {

    @Value("${local.server.port}")
    int port;

    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void healthEndpointIsPublicAndUp() throws Exception {
        HttpResponse<String> response = get("/api/health");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).contains("\"status\":\"UP\"");
    }

    @Test
    void everyOtherEndpointRequiresAuthentication() throws Exception {
        HttpResponse<String> response = get("/api/reviews");

        assertThat(response.statusCode()).isIn(401, 403);
    }

    @Test
    void healthDoesNotLeakDetails() throws Exception {
        HttpResponse<String> response = get("/api/health");

        assertThat(response.body()).doesNotContain("db", "diskSpace", "components");
    }
}