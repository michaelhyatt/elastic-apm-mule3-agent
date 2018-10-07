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
		return muleMessage.getInboundPropertyNames().stream().filter((x) -> filterInputProperties(x))
				.map((x) -> new ImmutablePair<String, Object>(x, muleMessage.getInboundProperty(x)));
	}

	private static boolean filterInputProperties(String x) {
		String regex = System.getProperty("elastic.apm.mule.capture_input_properties_regex");
		if (regex == null)
			return false;
		return x.matches(regex);
	}

	private static boolean filterOutputProperties(String x) {
		String regex = System.getProperty("elastic.apm.mule.capture_output_properties_regex");
		if (regex == null)
			return false;
		return x.matches(regex);
	}

	/**
	 * Returns a stream of pairs (key, value) for all output properties of the
	 * MuleMessage
	 * 
	 * @param muleMessage
	 * @return
	 */
	public static Stream<ImmutablePair<String, Object>> getOutputProperties(MuleMessage muleMessage) {
		return muleMessage.getOutboundPropertyNames().stream().filter((x) -> filterOutputProperties(x))
				.map((x) -> new ImmutablePair<String, Object>(x, muleMessage.getOutboundProperty(x)));
	}

	public static boolean isInputPropertyCaptureEnabled() {
		return Boolean.getBoolean("elastic.apm.mule.capture_input_properties");
	}

	public static boolean isOutputPropertyCaptureEnabled() {
		return Boolean.getBoolean("elastic.apm.mule.capture_output_properties");

	}
}
