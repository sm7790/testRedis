package com.gruposantander.subscribersarq;

import org.h2.tools.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import java.sql.SQLException;

@SpringBootApplication
@EnableCaching
public class SubscriberSarqApplication {
		// fgdfgfdgfdgfdgfdgfdgfdgfdgdfd
	public static void main(String[] args) {
		SpringApplication.run(SubscriberSarqApplication.class, args);
	}

	// Expose H2 database via a TCP port for integration in local dev
	@Bean(initMethod = "start", destroyMethod = "stop")
	public Server inMemoryH2DatabaseaServer() throws SQLException {
		return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9090");
	}
}

