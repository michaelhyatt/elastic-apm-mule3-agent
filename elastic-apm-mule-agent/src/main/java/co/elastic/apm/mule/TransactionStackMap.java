package co.elastic.apm.mule;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

import co.elastic.apm.api.Span;

public class TransactionStackMap {
	
	private Map<String, Stack<Span>> map = new ConcurrentHashMap<>();
	
	public void put(String key, Span span) {
		
		Stack<Span> stack = map.get(key);
		Stack<Span> newStack = new Stack<Span>();

		// Is there a Stack already?
		if (stack != null)
			newStack.addAll(stack);

		newStack.push(span);
		map.put(key, newStack);
	}

	public Span get(String key) {
		
		Stack<Span> stack = map.get(key);
		Stack<Span> newStack = new Stack<Span>();
		
		Span result = stack.pop();
		
		newStack.addAll(stack);
		map.put(key, newStack);
		
		return result;
	}
	
	public int depth(String key) {
		Stack<Span> value = map.get(key);

		if (value == null)
			return 0;
		
		return value.size();
		
	}
	
	public Span peek(String key) {
		return map.get(key).peek();
	}
	
}
