package tw.me.ychuang.rfc.json;

import java.util.Date;

import tw.me.ychuang.rfc.Constants;
import tw.me.ychuang.rfc.Request;
import tw.me.ychuang.rfc.Response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * A JSON serialization utility is based on GSON library.
 * 
 * @author Y.C. Huang
 */
public class GsonSerializer extends JsonSerializer {
	private static final Gson DEFAULT_GSON;

	static {
		// create one Gson Builder and reuse it.
		GsonBuilder GSON_BUILDER = new GsonBuilder();
		GSON_BUILDER.enableComplexMapKeySerialization();

		// register the three kinds of Type Adapters to handle requests, responses, and date objects
		GSON_BUILDER.registerTypeAdapter(Request.class, new RequestTypeAdapter());
		GSON_BUILDER.registerTypeAdapter(Response.class, new ResponseTypeAdapter());
		GSON_BUILDER.registerTypeAdapter(Date.class, new DateTypeAdapter());
		GSON_BUILDER.serializeNulls();
		GSON_BUILDER.setDateFormat(Constants.ISO_8601_PATTERN);

		DEFAULT_GSON = GSON_BUILDER.create();
	}

	GsonSerializer() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see tw.me.ychuang.rfc.json.JsonSerializer#toJson(java.lang.Object, java.lang.Class)
	 */
	public String toJson(Object src, Class srcClass) {
		String json = DEFAULT_GSON.toJson(src, srcClass);

		return json;
	}

	/*
	 * (non-Javadoc)
	 * @see tw.me.ychuang.rfc.json.JsonSerializer#toJsonElement(java.lang.Object)
	 */
	public JsonElement toJsonElement(Object src) {
		JsonElement jsonElement = DEFAULT_GSON.toJsonTree(src);

		return jsonElement;
	}

	/*
	 * (non-Javadoc)
	 * @see tw.me.ychuang.rfc.json.JsonSerializer#fromJson(java.lang.String, java.lang.Class)
	 */
	public <T> T fromJson(String jsonString, Class<T> srcClass) {
		T src = DEFAULT_GSON.fromJson(jsonString, srcClass);

		return src;
	}

	/*
	 * (non-Javadoc)
	 * @see tw.me.ychuang.rfc.json.JsonSerializer#fromJsonElement(com.google.gson.JsonElement, java.lang.Class)
	 */
	public <T> T fromJsonElement(JsonElement jsonElement, Class<T> srcClass) {
		T src = DEFAULT_GSON.fromJson(jsonElement, srcClass);

		return src;
	}
}
