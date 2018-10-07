package co.elastic.apm.mule.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.DataType;
import org.mule.api.transport.PropertyScope;
import org.mule.tck.junit4.FunctionalTestCase;

import co.elastic.apm.bci.ElasticApmAgent;
import co.elastic.apm.impl.ElasticApmTracerBuilder;
import co.elastic.apm.impl.transaction.Span;
import co.elastic.apm.impl.transaction.Transaction;
import co.elastic.apm.report.Reporter;
import net.bytebuddy.agent.ByteBuddyAgent;

@RunWith(MockitoJUnitRunner.class)
public class FunctionalTests extends FunctionalTestCase {

	@Test
	public void simpleFlowSendsOneTransaction() throws Exception {
	
		runFlow("test1Flow");
	
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Span.class));
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Transaction.class));
	
		assertEquals("test1Flow", tx.getName().toString());
		assertEquals("Logger", span.getName().toString());
	}

	@Test
	public void simpleFlowSendsOneTransactionWithProperty() throws Exception {
		
		MuleMessage msg = this.getTestMuleMessage();
		
		msg.setProperty("testProp", "testValue", PropertyScope.INBOUND, DataType.STRING_DATA_TYPE);
		msg.setProperty("not_testProp", "testValue", PropertyScope.INBOUND, DataType.STRING_DATA_TYPE);
		msg.setPayload("TEST", DataType.STRING_DATA_TYPE);
		System.setProperty("elastic.apm.mule.capture_input_properties", "true");
		System.setProperty("elastic.apm.mule.capture_input_properties_regex", "testPro(.*)");
		
		runFlow("test1Flow", msg);
	
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Span.class));
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Transaction.class));
	
		assertEquals("test1Flow", tx.getName().toString());
		
		// Logged property
		assertEquals("testValue", tx.getContext().getTags().get("in:testProp"));
		
		// Filtered out property 
		assertNull(tx.getContext().getTags().get("in:not_testProp"));
		
		assertEquals("Logger", span.getName().toString());
	}

	private Span span;
	private Transaction tx;
	
	@Mock
	private Reporter reporter;

	@Before
	public void setup() {
		
		Mockito.doAnswer(new Answer<Span>() {
			@Override
			public Span answer(InvocationOnMock invocation) throws Throwable {
				span = invocation.getArgumentAt(0, Span.class);
				return null;
			}
		}).when(reporter).report(Mockito.any(Span.class));

		Mockito.doAnswer(new Answer<Transaction>() {
			@Override
			public Transaction answer(InvocationOnMock invocation) throws Throwable {
				tx = invocation.getArgumentAt(0, Transaction.class);
				return null;
			}
		}).when(reporter).report(Mockito.any(Transaction.class));

		ElasticApmAgent.initInstrumentation(new ElasticApmTracerBuilder().reporter(reporter).build(),
				ByteBuddyAgent.install());
	}

	@After
	public void tearDown() {
		ElasticApmAgent.reset();
	}
	
	@Override
	protected String getConfigResources() {
		return "test_tracer.xml, test1.xml";
	}

}