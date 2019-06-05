package co.elastic.apm.mule.functional;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mule.tck.junit4.FunctionalTestCase;

import co.elastic.apm.agent.bci.ElasticApmAgent;
import co.elastic.apm.agent.impl.ElasticApmTracerBuilder;
import co.elastic.apm.agent.impl.error.ErrorCapture;
import co.elastic.apm.agent.impl.transaction.Span;
import co.elastic.apm.agent.impl.transaction.Transaction;
import co.elastic.apm.agent.report.Reporter;
import co.elastic.apm.mule.ApmClient;
import net.bytebuddy.agent.ByteBuddyAgent;

public abstract class AbstractApmFunctionalTestCase extends FunctionalTestCase {

	protected List<Span> spans;
	protected Transaction tx;
	protected ArrayList<ErrorCapture> errors;

	@Mock
	protected Reporter reporter;

	public AbstractApmFunctionalTestCase() {
		super();
	}

	@Before
	public void setup() {

		System.setProperty("elastic.apm.instrument", "false");

		tx = null;
		spans = new ArrayList<Span>();
		errors = new ArrayList<ErrorCapture>();

		Mockito.doAnswer(new Answer<Span>() {
			@Override
			public Span answer(InvocationOnMock invocation) throws Throwable {
				spans.add(invocation.getArgumentAt(0, Span.class));
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

		Mockito.doAnswer(new Answer<ErrorCapture>() {
			@Override
			public ErrorCapture answer(InvocationOnMock invocation) throws Throwable {
				errors.add(invocation.getArgumentAt(0, ErrorCapture.class));
				return null;
			}
		}).when(reporter).report(Mockito.any(ErrorCapture.class));

		ElasticApmAgent.initInstrumentation(new ElasticApmTracerBuilder().reporter(reporter).build(),
				ByteBuddyAgent.install());

		// Skip real initialisation so it is not triggered in the flows for tests
		ApmClient.setInitialised();
	}

	@After
	public void tearDown() {
		ElasticApmAgent.reset();
	}

}