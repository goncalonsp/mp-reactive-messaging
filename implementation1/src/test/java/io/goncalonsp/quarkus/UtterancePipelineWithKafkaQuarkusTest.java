package io.goncalonsp.quarkus;

import static net.mguenther.kafka.junit.SendKeyValues.to;
import static org.awaitility.Awaitility.await;

import io.goncalonsp.quarkus.utils.EmbeddedKafkaQuarkusTestResource;
import io.goncalonsp.quarkus.utils.InjectTestResource;
import io.goncalonsp.quarkus.utils.KafkaAssertion;
import io.goncalonsp.quarkus.utils.UsingKafkaConnectorProfile;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.ResourceArg;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import net.mguenther.kafka.junit.KeyValue;
import net.mguenther.kafka.junit.ObserveKeyValues;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
@QuarkusTest
@TestProfile(UsingKafkaConnectorProfile.class)
@QuarkusTestResource(value = EmbeddedKafkaQuarkusTestResource.class,
    initArgs = {
        @ResourceArg(
            name = EmbeddedKafkaQuarkusTestResource.ARGUMENT_CREATE_TOPICS,
            value = UtterancePipelineWithKafkaQuarkusTest.UTTERANCES_TOPIC_NAME + "," + UtterancePipelineWithKafkaQuarkusTest.RECOMMENDATIONS_TOPIC_NAME)
    })
class UtterancePipelineWithKafkaQuarkusTest {

    static final String UTTERANCES_TOPIC_NAME = "utterances";
    static final String RECOMMENDATIONS_TOPIC_NAME = "recommendations";

    @InjectTestResource
    EmbeddedKafkaCluster cluster;

    @Inject
    @ConfigProperty(name = "mp.messaging.incoming." + UTTERANCES_TOPIC_NAME + ".group.id")
    String groupIdProperty;

    @Inject
    @ConfigProperty(name = "mp.messaging.incoming." + UTTERANCES_TOPIC_NAME + ".client.id")
    String clientIdProperty;

    @Test
    @DisplayName("Given a utterance to process from the utterances channel, "
        + "when the utterance is processed by the full utterance pipeline, "
        + "then a recommendation is sent to the recommendations channel")
    void consumeUtterance() throws InterruptedException {
        final KafkaAssertion assertKafka = KafkaAssertion.builder().bootstrapServers(cluster.getBrokerList()).build();
        final UtteranceEvent event = UtteranceEvent.builder()
            .text("example text")
            .build();

        await().atMost(10, TimeUnit.SECONDS).until(() ->
            assertKafka.isConsumerConnectedToTopic(groupIdProperty, clientIdProperty, UTTERANCES_TOPIC_NAME));

        cluster.send(to(UTTERANCES_TOPIC_NAME, List.of(
            new KeyValue<>("8712378612387631313", "{\"text\":\"example-transcription\"}")
        )).useDefaults());

        cluster.observeValues(ObserveKeyValues.on(RECOMMENDATIONS_TOPIC_NAME, 1)
            .observeFor(10, TimeUnit.SECONDS)
            .useDefaults());
    }
}