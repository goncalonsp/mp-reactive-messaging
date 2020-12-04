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

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.mguenther.kafka.junit.EmbeddedKafkaCluster;
import net.mguenther.kafka.junit.EmbeddedKafkaClusterConfig;
import net.mguenther.kafka.junit.TopicConfig;

@Slf4j
public class EmbeddedKafkaQuarkusTestResource implements QuarkusTestResourceLifecycleManager {

    public static final String ARGUMENT_CREATE_TOPICS = "create-topics";
    public static final String QUARKUS_CONFIG_KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    private EmbeddedKafkaCluster cluster;
    private List<String> topicsToCreate;

    @Override
    public void init(Map<String, String> initArgs) {
        log.info("Configuring kafka cluster test resource");
        topicsToCreate = parseCommaDelimitedStringList(
            initArgs.getOrDefault(ARGUMENT_CREATE_TOPICS, null));

        cluster = EmbeddedKafkaCluster.provisionWith(EmbeddedKafkaClusterConfig.useDefaults());
    }

    @SneakyThrows
    @Override
    public Map<String, String> start() {
        log.info("Starting kafka cluster test resource");
        cluster.start();
        topicsToCreate.forEach(topic -> cluster.createTopic(TopicConfig.forTopic(topic).build()));
        return Map.of(QUARKUS_CONFIG_KAFKA_BOOTSTRAP_SERVERS, cluster.getBrokerList());
    }

    @Override
    public void stop() {
        log.info("Stopping kafka cluster test resource");
        cluster.stop();
    }

    @Override
    public void inject(Object testInstance) {
        log.info("Injecting kafka cluster test resource into test");

        Class<?> c = testInstance.getClass();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getAnnotation(InjectTestResource.class) != null) {
                    final Object injectable = getInjectableTestResourceForType(f.getType());

                    f.setAccessible(true);
                    try {
                        f.set(testInstance, injectable);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            c = c.getSuperclass();
        }
    }

    private List<String> parseCommaDelimitedStringList(final String value) {
        if (Objects.isNull(value)) {
            return List.of();
        } else {
            return Stream.of(value.split(","))
                .map(String::trim)
                .collect(Collectors.toUnmodifiableList());
        }
    }

    private Object getInjectableTestResourceForType(final Class<?> injectableType) {
        if (EmbeddedKafkaCluster.class.isAssignableFrom(injectableType)) {
            return cluster;
        } else {
            throw new RuntimeException(String.format("@InjectTestResource cannot be used on type %s", injectableType.getName()));
        }
    }

}
