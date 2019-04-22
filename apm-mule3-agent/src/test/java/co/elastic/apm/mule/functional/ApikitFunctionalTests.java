package co.elastic.apm.mule.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;

@RunWith(MockitoJUnitRunner.class)
public class ApikitFunctionalTests extends AbstractApmFunctionalTestCase {


	@Test
	public void testAPIKitFlow() throws Exception {

		MuleMessage message = getTestMuleMessage();
		message.setProperty("http.listener.path", "/api/*", PropertyScope.INBOUND);
		message.setProperty("http.method", "GET", PropertyScope.INBOUND);
		message.setProperty("host", "localhost", PropertyScope.INBOUND);
		message.setProperty("http.request.path", "/api/helloworld", PropertyScope.INBOUND);

		MuleEvent response = runFlow("api-kit-test-main", message);

		assertTrue(response.getMessage().getPayload().toString().contains("Hello world"));
		assertEquals("api-kit-test-main", tx.getName().toString());
		assertEquals("Set Payload", spans.get(0).getName().toString());

	}

	@Override
	protected String getConfigResources() {
		return "test_tracer.xml, api-kit-test.xml";
	}

}
