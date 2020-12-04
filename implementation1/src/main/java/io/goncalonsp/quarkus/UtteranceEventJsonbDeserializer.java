/*
 *
 * Talkdesk Confidential
 *
 * Copyright (C) Talkdesk Inc. 2019
 *
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office. Unauthorized copying of this file, via any medium
 * is strictly prohibited.
 *
 */
package io.goncalonsp.quarkus;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class UtteranceEventJsonbDeserializer extends JsonbDeserializer<UtteranceEvent> {
    public UtteranceEventJsonbDeserializer() {
        super(UtteranceEvent.class);
    }
}
