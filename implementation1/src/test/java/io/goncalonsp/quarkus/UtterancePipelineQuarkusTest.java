package io.goncalonsp.quarkus;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.reactive.messaging.connectors.InMemoryConnector;
import io.smallrye.reactive.messaging.connectors.InMemorySink;
import io.smallrye.reactive.messaging.connectors.InMemorySource;
import java.util.concurrent.TimeUnit;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UtterancePipelineQuarkusTest {

    @Inject
    @Any
    InMemoryConnector connector;
    private InMemorySource<Object> utterancesSource;
    private InMemorySink<RecommendationEvent> recommendationsSink;

    @BeforeEach
    void beforeEach() {
        utterancesSource = connector.source("utterances");
        recommendationsSink = connector.sink("recommendations");
        recommendationsSink.clear();
    }

    @Test
    @DisplayName("Given a utterance to process from the utterances channel, "
        + "when the utterance is processed by the full utterance pipeline, "
        + "then a recommendation is sent to the recommendations channel")
    void consumeUtterance() {
        final UtteranceEvent event = UtteranceEvent.builder()
            .text("example text")
            .build();

        utterancesSource.send(event);

        await().atMost(10, TimeUnit.SECONDS).until(() -> recommendationsSink.received().size() == 1);
        final RecommendationEvent producedEvent = recommendationsSink.received().get(0).getPayload();
        assertEquals("example text", producedEvent.getText());
    }
}