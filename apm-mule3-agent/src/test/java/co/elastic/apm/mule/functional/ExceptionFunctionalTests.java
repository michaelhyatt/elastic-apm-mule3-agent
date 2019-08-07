package co.elastic.apm.mule.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MessagingException;

import co.elastic.apm.agent.impl.error.ErrorCapture;
import co.elastic.apm.agent.impl.transaction.Span;
import co.elastic.apm.agent.impl.transaction.Transaction;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionFunctionalTests extends AbstractApmFunctionalTestCase {

	@Test
	public void testSimpleException() throws Exception {

		try {
			runFlow("exception1Flow");

			fail("Should throw exception");
		} catch (Exception e) {
			assertTrue(e instanceof MessagingException);
		}

		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Span.class));
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Transaction.class));
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(ErrorCapture.class));

		assertEquals("exception1Flow", tx.getNameAsString());

		assertEquals("HTTP", spans.get(0).getNameAsString());
		String expected = "Error sending HTTP request";
		assertEquals(expected, errors.get(0).getException().getMessage().subSequence(0, expected.length()));

	}

	@Override
	protected String getConfigResources() {
		return "test_tracer.xml, test_exception1.xml";
	}

}
