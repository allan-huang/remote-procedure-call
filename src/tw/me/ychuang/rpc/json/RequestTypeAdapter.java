package tw.me.ychuang.rpc.json;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rpc.Command;
import tw.me.ychuang.rpc.Request;

import com.google.gson.JsonArray;
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
 * Serializes and deserializes a request to / from one JSON string.
 * 
 * @author Y.C. Huang
 */
public class RequestTypeAdapter implements JsonSerializer<Request>, JsonDeserializer<Request> {
	private static final Logger log = LoggerFactory.getLogger(RequestTypeAdapter.class);

	/**
	 * Serializes a request to a json element
	 */
	public JsonElement serialize(Request request, Type requestClass, JsonSerializationContext context) {
		JsonObject jsonRequest = new JsonObject();
		jsonRequest.addProperty("id", request.getId());

		Command command = request.getCommand();
		if (command == null) {
			jsonRequest.add("command", JsonNull.INSTANCE);
			return jsonRequest;
		}

		JsonObject jsonCommand = new JsonObject();
		jsonRequest.add("command", jsonCommand);

		jsonCommand.addProperty("skeleton", command.getSkeleton());
		jsonCommand.addProperty("method", command.getMethod());
		jsonCommand.addProperty("staticMethod", command.isStaticMethod());

		List<Object> parameters = command.getParameters();
		List<Class> paramClasses = command.getParamClasses();

		Object parameter = null;
		Class clazz = null;
		JsonElement jsonParameter = null;
		JsonArray jsonParameters = new JsonArray();
		JsonPrimitive jsonParamClass = null;
		JsonArray jsonParamClasses = new JsonArray();

		for (int i = 0; i < parameters.size(); i++) {
			parameter = parameters.get(i);
			clazz = paramClasses.get(i);
			jsonParameter = context.serialize(parameter, clazz);
			jsonParameters.add(jsonParameter);

			jsonParamClass = new JsonPrimitive(clazz.getName());
			jsonParamClasses.add(jsonParamClass);
		}
		jsonCommand.add("parameters", jsonParameters);
		jsonCommand.add("paramClasses", jsonParamClasses);

		return jsonRequest;
	}

	/**
	 * Deserialize a json element to a request
	 */
	public Request deserialize(JsonElement jsonElement, Type requestClass, JsonDeserializationContext context) throws JsonParseException {
		JsonObject jsonRequest = (JsonObject) jsonElement;
		long id = jsonRequest.getAsJsonPrimitive("id").getAsLong();
		JsonElement jsonCmdElement = jsonRequest.get("command");

		Request request = null;
		if (jsonCmdElement.isJsonNull()) {
			request = new Request(id, null);
			return request;
		}

		JsonObject jsonCommand = jsonCmdElement.getAsJsonObject();
		String skeleton = jsonCommand.getAsJsonPrimitive("skeleton").getAsString();
		String method = jsonCommand.getAsJsonPrimitive("method").getAsString();
		boolean staticMethod = jsonCommand.getAsJsonPrimitive("staticMethod").getAsBoolean();

		Command command = new Command(skeleton, method, staticMethod);
		request = new Request(id, command);

		JsonArray jsonParameters = jsonCommand.getAsJsonArray("parameters");
		JsonArray jsonParamClasses = jsonCommand.getAsJsonArray("paramClasses");

		Object parameter = null;
		Class clazz = null;
		JsonElement jsonParameter = null;
		JsonElement jsonParamClass = null;

		for (int i = 0; i < jsonParamClasses.size(); i++) {
			jsonParameter = jsonParameters.get(i);
			jsonParamClass = jsonParamClasses.get(i);

			String className = jsonParamClass.getAsString();
			try {
				clazz = ClassUtils.getClass(className);
			} catch (ClassNotFoundException e) {
				throw new JsonParseException("Cannot find a matching class by name: " + className, e);
			}

			if (jsonParameter.isJsonNull()) {
				command.addParameter(null, clazz);
				continue;
			}

			if (clazz.isPrimitive()) {
				parameter = PrimitiveWrapperUtils.toPrimitive(jsonParameter.getAsString(), clazz);

			} else if (ClassUtils.isPrimitiveOrWrapper(clazz)) {
				parameter = PrimitiveWrapperUtils.toPrimitiveWrapper(jsonParameter.getAsString(), clazz);

			} else {
				parameter = context.deserialize(jsonParameter, clazz);
			}
			command.addParameter(parameter, clazz);
		}

		return request;
	}
}
