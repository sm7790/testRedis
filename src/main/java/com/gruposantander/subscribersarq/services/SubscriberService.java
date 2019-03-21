package com.gruposantander.subscribersarq.services;

import com.gruposantander.subscribersarq.dtos.CustodianInputDto;

public interface SubscriberService {

	CustodianLineages saveCustodianLineages(CustodianInputDto custodianInputDto);
}
