package com.gruposantander.subscribersarq.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import lombok.extern.slf4j.Slf4j;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.gruposantander.subscribersarq.models.Custodian;
import com.gruposantander.subscribersarq.repositories.CustodianRepository;
import redis.embedded.RedisServer;

import java.io.IOException;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka
@TestPropertySource(locations = "classpath:test.properties")
public class CustodianServiceIT {

	@Autowired
	private CustodianService custodianService;

	@Autowired
	private CustodianRepository custodianRepository;

	private final static int PORT_REDIS = 6379;

	private static RedisServer redisServer;

	@BeforeClass
	public static void before() {
		try {
			redisServer = new RedisServer(PORT_REDIS);
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		redisServer.start();
	}

	@AfterClass
	public static void after() {
		redisServer.stop();
	}

	@Test
	public void testSave() {
		Custodian custodianMock = Custodian.builder().hash("0000002").uri("http://ejemplo2.es").proc("P2").version("v2.3.l8")
				.information("Esto es un comentario").build();
		Custodian custodian = this.custodianService.save(custodianMock);
		assertNotNull(custodian);
		assertEquals(custodianMock.getHash(), custodian.getHash());
		assertEquals(custodianMock.getUri(), custodian.getUri());
		assertEquals(custodianMock.getProc(), custodian.getProc());
		assertEquals(custodianMock.getVersion(), custodian.getVersion());
		assertEquals(custodianMock.getInformation(), custodian.getInformation());
		this.custodianRepository.deleteById(custodian.getId());
	}
}
