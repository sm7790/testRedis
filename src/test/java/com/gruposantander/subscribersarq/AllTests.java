package com.gruposantander.subscribersarq;

import com.gruposantander.subscribersarq.repositories.AllRepositoriesIntegrationTests;
import com.gruposantander.subscribersarq.services.AllServicesIntegrationTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AllRepositoriesIntegrationTests.class, AllServicesIntegrationTests.class,})
public class AllTests {
}
