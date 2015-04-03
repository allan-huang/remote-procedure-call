package tw.me.ychuang.rpc.json;

import java.lang.reflect.Type;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.Response;
import tw.me.ychuang.rpc.Result;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serializes and deserializes a response to / from one JSON string.
 * 
 * @author Y.C. Huang
 */
public class ResponseTypeAdapter<Respose> implements JsonSerializer<Response>, JsonDeserializer<Response> {
	private static final Logger log = LoggerFactory.getLogger(ResponseTypeAdapter.class);

	/**
	 * Serializes a response to a json element
	 */
	@Override
	public JsonElement serialize(Response response, Type responseClass, JsonSerializationContext context) {
		JsonObject jsonResponse = new JsonObject();
		jsonResponse.addProperty("id", response.getId());

		Result result = response.getResult();
		if (result == null) {
			jsonResponse.add("result", JsonNull.INSTANCE);
			return jsonResponse;
		}

		JsonObject jsonResult = new JsonObject();
		jsonResponse.add("result", jsonResult);

		Class resultClass = result.getReturnClass();
		JsonPrimitive jsonResultClass = new JsonPrimitive(resultClass.getName());
		JsonPrimitive jsonExceptional = new JsonPrimitive(result.isExceptional());

		JsonElement jsonReturn = null;
		if (result.getReturn() != null) {
			if (false == result.isExceptional()) {
				jsonReturn = context.serialize(result.getReturn(), resultClass);
			} else {
				Throwable cause = (Throwable) result.getReturn();
				String stackTraceMessage = ExceptionUtils.getStackTrace(cause);
				jsonReturn = new JsonPrimitive(stackTraceMessage);
			}
		} else {
			jsonReturn = JsonNull.INSTANCE;
		}

		jsonResult.add("return", jsonReturn);
		jsonResult.add("returnClass", jsonResultClass);
		jsonResult.add("exceptional", jsonExceptional);

		return jsonResponse;
	}

	/**
	 * Deserialize a json element to a response
	 */
	@Override
	public Response deserialize(JsonElement jsonElement, Type responseClass, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonResponse = (JsonObject) jsonElement;
		long id = jsonResponse.getAsJsonPrimitive("id").getAsLong();
		JsonElement jsonRsElement = jsonResponse.get("result");

		Response response = null;
		if (jsonRsElement.isJsonNull()) {
			response = new Response(id, null, null);
			return response;
		}

		JsonObject jsonResult = jsonRsElement.getAsJsonObject();
		JsonElement jsonReturn = jsonResult.get("return");
		JsonPrimitive jsonReturnClass = jsonResult.getAsJsonPrimitive("returnClass");
		JsonPrimitive jsonExceptional = jsonResult.getAsJsonPrimitive("exceptional");

		Class resultClass = null;
		try {
			resultClass = ClassUtils.getClass(jsonReturnClass.getAsString());
		} catch (ClassNotFoundException e) {
			throw new JsonParseException("Cannot find a matching class by name: " + jsonReturnClass.getAsString(), e);
		}

		Result result = null;
		if (jsonReturn.isJsonNull()) {
			result = new Result(null, resultClass);

		} else {
			Object resultObj = null;

			if (resultClass.isPrimitive()) {
				resultObj = PrimitiveWrapperUtils.toPrimitive(jsonReturn.getAsString(), resultClass);

			} else if (ClassUtils.isPrimitiveOrWrapper(resultClass)) {
				resultObj = PrimitiveWrapperUtils.toPrimitiveWrapper(jsonReturn.getAsString(), resultClass);

			} else if (Throwable.class.isAssignableFrom(resultClass)) {
				resultObj = jsonReturn.getAsString();

			} else {
				resultObj = context.deserialize(jsonReturn, resultClass);
			}

			result = new Result(resultObj, resultClass);
		}

		response = new Response(id, result, resultClass.getName());

		return response;
	}
}
