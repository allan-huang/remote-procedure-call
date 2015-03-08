package tw.me.ychuang.rfc.json;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serializes and deserializes a date to / from one JSON string.
 * 
 * @author Y.C. Huang
 */
public class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
	/*
	 * (non-Javadoc)
	 * @see com.google.gson.JsonSerializer#serialize(java.lang.Object, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)
	 */
	public JsonElement serialize(Date date, Type dateClass, JsonSerializationContext context) {
		JsonPrimitive jsonPrimitive = new JsonPrimitive(date.getTime());

		return jsonPrimitive;
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.gson.JsonDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)
	 */
	public Date deserialize(JsonElement jsonElement, Type dateClass, JsonDeserializationContext context) throws JsonParseException {
		long time = jsonElement.getAsLong();
		Date date = new Date(time);

		return date;
	}
}
