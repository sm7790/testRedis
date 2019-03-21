package com.gruposantander.subscribersarq.services;

import com.gruposantander.subscribersarq.repositories.CustodianRepository;
import io.confluent.kafka.schemaregistry.RestApp;
import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;
import io.confluent.kafka.schemaregistry.rest.SchemaRegistryConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka
@TestPropertySource(locations = "classpath:test.properties")
@Slf4j
public class KafkaListenerServiceIT {

	private static final String TOPIC = "custodian";

	private static final String KAFKA_SCHEMAS_TOPIC = "_schemas";

	private static final String AVRO_COMPATIBILITY_TYPE = AvroCompatibilityLevel.NONE.name;

	private static final String KAFKASTORE_OPERATION_TIMEOUT_MS = "10000";

	private static final String KAFKASTORE_DEBUG = "true";

	private static final String KAFKASTORE_INIT_TIMEOUT = "90000";

	@ClassRule
	public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, TOPIC);

	private static EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafkaRule.getEmbeddedKafka();

	private static Consumer<String, GenericRecord> consumer;

	private static RestApp schemaRegistry;

	@Autowired
	private KafkaListenerServiceImpl kafkaListenerService;

	@Autowired
	private CustodianRepository custodianRepository;

	@BeforeClass
	public static void setUp() {
		final Properties schemaRegistryProps = new Properties();
		try {
			schemaRegistryProps.put(SchemaRegistryConfig.KAFKASTORE_TIMEOUT_CONFIG, KAFKASTORE_OPERATION_TIMEOUT_MS);
			schemaRegistryProps.put(SchemaRegistryConfig.DEBUG_CONFIG, KAFKASTORE_DEBUG);
			schemaRegistryProps.put(SchemaRegistryConfig.KAFKASTORE_INIT_TIMEOUT_CONFIG, KAFKASTORE_INIT_TIMEOUT);
			schemaRegistry = new RestApp(0, embeddedKafkaBroker.getZookeeperConnectionString(), KAFKA_SCHEMAS_TOPIC,
					AVRO_COMPATIBILITY_TYPE, schemaRegistryProps);
			schemaRegistry.start();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		Map<String, Object> consumerProps = KafkaTestUtils
				.consumerProps("GroupKafkaEmbeddedTest" + UUID.randomUUID().toString(), "false", embeddedKafkaBroker);
		consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
		consumerProps.put("schema.registry.url", schemaRegistry.restConnect);
		DefaultKafkaConsumerFactory<String, GenericRecord> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
		consumer = cf.createConsumer();
		embeddedKafkaBroker.consumeFromEmbeddedTopics(consumer, TOPIC);
	}

	@AfterClass
	public static void tearDown() {
		try {
			consumer.close();
			schemaRegistry.stop();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	@Test
	public void testSubscribe() throws IOException {

		HashMap<String, Object> hashMap = new HashMap<>();
		hashMap.put("hash", "00000004");
		hashMap.put("uri", "http://ejemplo5.es");
		hashMap.put("proc", "P1");
		hashMap.put("version", "v1.0.l0");
		hashMap.put("information", "Esto es un comentario");

		Schema schema = new Schema.Parser().parse(getClass().getResourceAsStream("/avro/Custodian.avsc"));
		GenericRecordBuilder builder = new GenericRecordBuilder(schema);
		GenericRecord genericRecord = builder.build();
		schema.getFields().forEach(r -> genericRecord.put(r.name(), hashMap.get(r.name())));

		Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
		senderProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		senderProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
		senderProps.put("schema.registry.url", schemaRegistry.restConnect);
		DefaultKafkaProducerFactory<String, GenericRecord> pf = new DefaultKafkaProducerFactory<>(senderProps);
		KafkaTemplate<String, GenericRecord> template = new KafkaTemplate<>(pf, true);
		template.setDefaultTopic(TOPIC);
		template.sendDefault(genericRecord);

		ConsumerRecords<String, GenericRecord> records = KafkaTestUtils.getRecords(consumer);
		ConsumerRecord<String, GenericRecord> record = records.records(TOPIC).iterator().next();

		CustodianLineages custodianLineages = this.kafkaListenerService.subscribe(record.value());
		assertNotNull(custodianLineages);
		assertEquals(genericRecord.get("hash").toString(), custodianLineages.getCustodian().getHash());
		this.custodianRepository.deleteById(custodianLineages.getCustodian().getId());

	}

}
