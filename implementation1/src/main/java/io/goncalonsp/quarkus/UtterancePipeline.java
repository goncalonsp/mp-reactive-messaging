package io.goncalonsp.quarkus;

import io.smallrye.mutiny.Uni;
import java.time.Duration;
import java.util.Random;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@Slf4j
@ApplicationScoped
public class UtterancePipeline {

    public final Emitter<RecommendationEvent> emitter;

    @Inject
    public UtterancePipeline(final @Channel("recommendations") Emitter<RecommendationEvent> emitter) {
        this.emitter = emitter;
    }

    private final Random random = new Random();

    @Incoming("utterances")
    public Uni<RecommendationEvent> consume(final UtteranceEvent event) {
        return Uni.createFrom().item(event)
            .onItem().transformToUni(event1 -> Uni.createFrom().item(event1).onItem().delayIt().by(Duration.ofMillis(100 + random.nextInt(5000))))
            .onItem().transformToUni(event1 -> Uni.createFrom().item(RecommendationEvent.builder().text(event1.getText()).build()))
            .onItem().transformToUni(event1 -> Uni.createFrom().item(event1).onItem().invoke(emitter::send));
    }
}
