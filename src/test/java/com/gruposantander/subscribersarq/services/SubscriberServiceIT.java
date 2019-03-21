package com.gruposantander.subscribersarq.services;

import com.gruposantander.subscribersarq.dtos.CustodianInputDto;
import com.gruposantander.subscribersarq.dtos.OriginDto;
import com.gruposantander.subscribersarq.models.Lineage;
import com.gruposantander.subscribersarq.repositories.CustodianRepository;
import com.gruposantander.subscribersarq.repositories.LineageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka
@TestPropertySource(locations = "classpath:test.properties")
public class SubscriberServiceIT {

	@Autowired
	private CustodianRepository custodianRepository;

	@Autowired
	private LineageRepository lineageRepository;

	@Autowired
	private SubscriberService subscriberService;

	@Test
	public void testSaveCustodianLineage() {
		OriginDto originDto1 = OriginDto.builder().hash("0000001").uri("http://ejemplo1.es").build();
		OriginDto originDto2 = OriginDto.builder().hash("0000002").uri("http://ejemplo2.es").build();
		CustodianInputDto custodianInputDto = CustodianInputDto.builder().hash("0000003").uri("http://ejemplo3.es").proc("P3")
				.version("v3.11.0").information("Esto es un comentario").origins(Arrays.asList(originDto1, originDto2)).build();
		CustodianLineages custodianLineages = this.subscriberService.saveCustodianLineages(custodianInputDto);
		assertNotNull(custodianLineages);
		assertEquals(custodianInputDto.getHash(), custodianLineages.getCustodian().getHash());
		assertTrue(custodianLineages.getLineagesList().size() >= 2);
		this.custodianRepository.deleteById(custodianLineages.getCustodian().getId());
		for (Lineage lineage : custodianLineages.getLineagesList()) {
			this.lineageRepository.deleteById(lineage.getId());
		}
	}
}
