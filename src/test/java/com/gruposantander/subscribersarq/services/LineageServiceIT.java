package com.gruposantander.subscribersarq.services;

import com.gruposantander.subscribersarq.models.Lineage;
import com.gruposantander.subscribersarq.repositories.LineageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka
@TestPropertySource(locations = "classpath:test.properties")
public class LineageServiceIT {

	@Autowired
	private LineageService lineageService;

	@Autowired
	private LineageRepository lineageRepository;

	@Test
	public void testSave() {
		Lineage lineageMock = Lineage.builder().hash("0000003").hashOrigin("0000001").uri("http://ejemplo3.es")
				.uriOrigin("http://ejemplo1.es").build();
		Lineage lineage = this.lineageService.save(lineageMock);
		assertNotNull(lineage);
		assertEquals(lineageMock.getHash(), lineage.getHash());
		assertEquals(lineageMock.getHashOrigin(), lineage.getHashOrigin());
		assertEquals(lineageMock.getUri(), lineage.getUri());
		assertEquals(lineageMock.getUriOrigin(), lineage.getUriOrigin());
		this.lineageRepository.deleteById(lineage.getId());
	}

}
