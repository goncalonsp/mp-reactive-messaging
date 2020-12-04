package io.goncalonsp.quarkus;

import java.util.concurrent.ExecutionException;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class UtterancePipelineTest {

    @Mock
    Emitter<RecommendationEvent> emitter;
    @InjectMocks
    UtterancePipeline subject;

    @Test
    @DisplayName("Given an incoming utterance event, "
        + "when the consumer is invoked for the utterance, "
        + "then the utterance sent to be processed")
    void x() throws ExecutionException, InterruptedException {
        final UtteranceEvent event = UtteranceEvent.builder()
            .text("example text")
            .build();

        subject.consume(event).subscribe().asCompletionStage().get();
    }
}