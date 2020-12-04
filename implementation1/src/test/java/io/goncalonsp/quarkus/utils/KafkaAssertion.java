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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.MemberDescription;
import org.apache.kafka.common.TopicPartition;

@Slf4j
@Builder
public class KafkaAssertion {

    private final String bootstrapServers;

    private Properties buildDefaultProperties() {
        final Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(AdminClientConfig.CLIENT_ID_CONFIG, "kafka-junit-admin-client");
        return props;
    }

    public boolean isConsumerConnectedToTopic(final String groupId, final String clientId, final String topic) {
        try (AdminClient client = AdminClient.create(buildDefaultProperties())) {
            final Map<String, ConsumerGroupDescription> consumerGroupListings = client.describeConsumerGroups(List.of(groupId)).all().get();
            final Optional<MemberDescription> memberDescription = consumerGroupListings.get(groupId).members()
                .stream()
                .filter(member -> member.clientId().equals(clientId))
                .findAny();

            if (log.isDebugEnabled()) {
                memberDescription.ifPresentOrElse(md -> {
                    Set<TopicPartition> assignedPartitions = md.assignment().topicPartitions();
                    if (assignedPartitions.stream().anyMatch(assignment -> assignment.topic().equals(topic))) {
                        log.debug("isConsumerConnectedToTopic? Yes, on partitions '{}'",
                            assignedPartitions.stream().map(TopicPartition::toString).collect(Collectors.joining(",")));
                    } else {
                        log.debug("isConsumerConnectedToTopic? No, consumer isn't assigned to topic");
                    }
                }, () -> log.debug("isConsumerConnectedToTopic? No, consumer isn't connected to cluster"));
            }

            return memberDescription.map(
                description -> description.assignment().topicPartitions()
                    .stream()
                    .anyMatch(assignment -> assignment.topic().equals(topic))
            ).orElseGet(() -> { return false; });

        } catch (Exception e) {
            log.error("Error validating consumer is connected", e);
            return false;
        }
    }
}
