package com.gruposantander.subscribersarq.services;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
		CustodianServiceIT.class,
		LineageServiceIT.class,
		SubscriberServiceIT.class,
		KafkaListenerServiceIT.class,})
public class AllServicesIntegrationTests {
}
