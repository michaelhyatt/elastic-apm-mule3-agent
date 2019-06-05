package co.elastic.apm.mule.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;

import co.elastic.apm.agent.impl.error.ErrorCapture;
import co.elastic.apm.agent.impl.transaction.Span;
import co.elastic.apm.agent.impl.transaction.Transaction;

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
		
		Mockito.verify(reporter, Mockito.times(2)).report(Mockito.any(Span.class));
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Transaction.class));
		Mockito.verify(reporter, Mockito.times(0)).report(Mockito.any(ErrorCapture.class));
		
		assertTrue(response.getMessage().getPayload().toString().contains("Hello world"));
		assertEquals("api-kit-test-main", tx.getName().toString());
		assertEquals("Set Payload", spans.get(0).getName().toString());

	}

	@Override
	protected String getConfigResources() {
		return "test_tracer.xml, api-kit-test.xml";
	}

}
