package io.goncalonsp.quarkus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.goncalonsp.quarkus.RecommendationEvent;
import java.io.IOException;
import javax.json.bind.JsonbBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RecommendationEventSerializationTest {

    @Test
    @DisplayName("Given valid recommendation string, "
        + "when recommendation is deserialized, "
        + "then a corresponding recommendation event is returned")
    public void deserialize() throws IOException {
        final String string = "{"
            + "\"text\":\"example text\""
            + "}";

        final RecommendationEvent event = JsonbBuilder.create().fromJson(string, RecommendationEvent.class);

        Assertions.assertEquals("example text", event.getText());
    }

    @Test
    @DisplayName("Given valid recommendation event, "
        + "when recommendation is serialized, "
        + "then a corresponding recommendation string is returned")
    public void serialize() throws IOException {
        final RecommendationEvent event = RecommendationEvent.builder()
            .text("example text")
            .build();

        final String string = JsonbBuilder.create().toJson(event);

        assertEquals("{\"text\":\"example text\"}", string);
    }

}