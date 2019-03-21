package com.gruposantander.subscribersarq.services;

import com.gruposantander.subscribersarq.models.Custodian;
import com.gruposantander.subscribersarq.repositories.CustodianRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
public class CustodianServiceImpl implements CustodianService {

    @Autowired
    CustodianRepository custodianRepository;

    @CachePut(value = "custodiansCache", key = "#custodian.id")
    @Override
    public Custodian save(Custodian custodian) {
        return this.custodianRepository.save(custodian);
    }

}
