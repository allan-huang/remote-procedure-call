package tw.me.ychuang.rfc;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A container that wraps a skeleton's ID, a method that will be invoked, and the needed parameters that will be inputted.
 * 
 * @author Y.C. Huang
 */
public class Command {
	private static final Logger log = LoggerFactory.getLogger(Command.class);

	/**
	 * A kind of constructor
	 * 
	 * @param skeleton A skeleton's ID
	 * @param method A skeleton's method
	 * @param staticMethod true if the skeleton's method is static
	 */
	public Command(String skeleton, String method, boolean staticMethod) {
		super();
		this.skeleton = skeleton;
		this.method = method;
		this.staticMethod = staticMethod;
	}

	/**
	 * A skeleton's ID
	 */
	private final String skeleton;

	/**
	 * Getter method for field 'skeleton'
	 * 
	 * @return skeleton
	 */
	public String getSkeleton() {
		return this.skeleton;
	}

	/**
	 * A skeleton's method that will be executed
	 */
	private final String method;

	/**
	 * Getter method for field 'method'
	 * 
	 * @return method
	 */
	public String getMethod() {
		return this.method;
	}

	/**
	 * The skeleton's method whether is static or not
	 */
	private final boolean staticMethod;

	/**
	 * Getter method for field 'staticMethod'
	 * 
	 * @return staticMethod
	 */
	public boolean isStaticMethod() {
		return this.staticMethod;
	}

	/**
	 * A value list of the specified parameters are sent for executing a remote skeleton
	 */
	private List<Object> parameters = new ArrayList<>();

	/**
	 * Getter method for field 'parameters'
	 * 
	 * @return parameters
	 */
	public List<Object> getParameters() {
		return this.parameters;
	}

	/**
	 * A class list of the specified parameters are sent for executing a remote skeleton
	 */
	private List<Class> paramClasses = new ArrayList<>();

	/**
	 * Getter method for field 'paramClasses'
	 * 
	 * @return paramClasses
	 */
	public List<Class> getParamClasses() {
		return this.paramClasses;
	}

	/**
	 * Adds the pair of a parameter and its class
	 * 
	 * @param parameter a parameter
	 * @param paramClass a parameter class
	 */
	public void addParameter(Object parameter, Class paramClass) {
		this.parameters.add(parameter);
		this.paramClasses.add(paramClass);
	}

	/**
	 * A convenient method for field 'parameters'
	 * 
	 * @return parameters
	 */
	public Object[] findParameters() {
		Object[] parameters = new Object[0];
		if (this.parameters != null && false == this.parameters.isEmpty()) {
			parameters = this.parameters.toArray();
		}

		return parameters;
	}

	/**
	 * A convenient method for field 'parameterClasses'
	 * 
	 * @return parameterClasses
	 */
	public Class[] findParamClasses() {
		Class[] paramClasses = new Class[0];
		if (this.paramClasses != null && false == this.paramClasses.isEmpty()) {
			paramClasses = new Class[this.paramClasses.size()];
			for (int i = 0; i < this.paramClasses.size(); i++) {
				paramClasses[i] = this.paramClasses.get(i);
			}
		}

		return paramClasses;
	}
}
