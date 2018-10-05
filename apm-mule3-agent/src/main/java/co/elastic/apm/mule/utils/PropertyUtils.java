package co.elastic.apm.mule.utils;

import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mule.api.MuleMessage;

/**
 * @author michaelhyatt
 * 
 *         Check and retrieve a the properties for tagging
 *
 */
public class PropertyUtils {

	/**
	 * Returns a stream of pairs (key, value) for all input properties of the
	 * MuleMessage
	 * 
	 * @param muleMessage
	 * @return
	 */
	public static Stream<ImmutablePair<String, Object>> getInputProperties(MuleMessage muleMessage) {
		return muleMessage.getInboundPropertyNames().stream()
				.map((x) -> new ImmutablePair<String, Object>(x, muleMessage.getInboundProperty(x)));
	}

	/**
	 * Returns a stream of pairs (key, value) for all output properties of the
	 * MuleMessage
	 * 
	 * @param muleMessage
	 * @return
	 */
	public static Stream<ImmutablePair<String, Object>> getOutputProperties(MuleMessage muleMessage) {
		return muleMessage.getOutboundPropertyNames().stream()
				.map((x) -> new ImmutablePair<String, Object>(x, muleMessage.getOutboundProperty(x)));
	}
}
