package io.goncalonsp.quarkus;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.goncalonsp.quarkus.UtteranceEvent;
import java.io.IOException;
import javax.json.bind.JsonbBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UtteranceEventSerializationTest {

    @Test
    @DisplayName("Given valid utterance string, "
        + "when utterance is deserialized, "
        + "then a corresponding utterance event is returned")
    public void deserialize() throws IOException {
        final String string = "{"
            + "\"text\":\"example text\""
            + "}";

        final UtteranceEvent utteranceEvent = JsonbBuilder.create().fromJson(string, UtteranceEvent.class);

        Assertions.assertEquals("example text", utteranceEvent.getText());
    }

    @Test
    @DisplayName("Given valid utterance event, "
        + "when utterance is serialized, "
        + "then a corresponding utterance string is returned")
    public void serialize() throws IOException {
        final UtteranceEvent event = UtteranceEvent.builder()
            .text("example text")
            .build();

        final String string = JsonbBuilder.create().toJson(event);

        assertEquals("{\"text\":\"example text\"}", string);
    }

}