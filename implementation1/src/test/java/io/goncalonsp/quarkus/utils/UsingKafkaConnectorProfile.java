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
package io.goncalonsp.quarkus.utils;

import io.quarkus.test.junit.QuarkusTestProfile;
import java.util.Map;

/**
 * This profile aims to setup the application to use Kafka as the messaging broker.
 */
public class UsingKafkaConnectorProfile implements QuarkusTestProfile {

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "mp.messaging.incoming.utterances.connector", "smallrye-kafka",
            "mp.messaging.outgoing.recommendations.connector", "smallrye-kafka"
        );
    }

}
