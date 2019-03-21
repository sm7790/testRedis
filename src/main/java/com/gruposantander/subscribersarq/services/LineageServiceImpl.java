package com.gruposantander.subscribersarq.services;

import com.gruposantander.subscribersarq.models.Lineage;
import com.gruposantander.subscribersarq.repositories.LineageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
public class LineageServiceImpl implements LineageService {

	@Autowired
	LineageRepository lineageRepository;

	@CachePut(value = "lineagesCache", key = "#lineage.id")
	@Override
	public Lineage save(Lineage lineage) {
		return this.lineageRepository.save(lineage);
	}

}
