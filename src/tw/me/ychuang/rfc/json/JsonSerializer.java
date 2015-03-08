package tw.me.ychuang.rfc.json;

import com.google.gson.JsonElement;

/**
 * An abstract JSON serialization utility.
 * 
 * @author Y.C. Huang
 */
public abstract class JsonSerializer {
	/**
	 * Apply a lazy-loaded singleton - Initialization on Demand Holder.<br>
	 * see detail on right side: http://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
	 */
	private static class LazyHolder {
		private static final JsonSerializer INSTANCE = new GsonSerializer();
	}

	public static JsonSerializer getInstance() {
		return LazyHolder.INSTANCE;
	}

	protected JsonSerializer() {
		super();
	}

	/**
	 * Serializes an object into its equivalent json string
	 * 
	 * @param src an object
	 * @param srcClass the specific class of src object.
	 * @return a json string represents the specified object
	 */
	public abstract String toJson(Object src, Class srcClass);

	/**
	 * Serializes an object into its equivalent json string
	 * 
	 * @param src an object
	 * @return a json element represents the specified object
	 */
	public abstract JsonElement toJsonElement(Object src);

	/**
	 * Deserializes a json string into an object of the specified class
	 * 
	 * @param jsonString a json string
	 * @param srcClass a specific class of the specified object
	 * @return the specified object
	 */
	public abstract <T> T fromJson(String jsonString, Class<T> srcClass);

	/**
	 * Deserializes a json element into an object of the specified class
	 * 
	 * @param jsonElement
	 * @param srcClass an object of the specified object.
	 * @return the specified object
	 */
	public abstract <T> T fromJsonElement(JsonElement jsonElement, Class<T> srcClass);

}
