package com.gruposantander.subscribersarq.repositories;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
		CustodianRepositoryIT.class,
		LineageRepositoryIT.class,
})
public class AllRepositoriesIntegrationTests {
}
