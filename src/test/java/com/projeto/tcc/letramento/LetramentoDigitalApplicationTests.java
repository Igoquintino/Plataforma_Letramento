package com.projeto.tcc.letramento;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
		"spring.security.oauth2.client.registration.google.client-id=mock-google-id",
		"spring.security.oauth2.client.registration.google.client-secret=mock-google-secret"
})
@Disabled("placeholder")
@ActiveProfiles("test")
class LetramentoDigitalApplicationTests {

	@Test
	void contextLoads() {
	}

}
