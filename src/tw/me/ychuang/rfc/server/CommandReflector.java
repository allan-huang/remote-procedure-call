package tw.me.ychuang.rfc.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tw.me.ychuang.rfc.Command;
import tw.me.ychuang.rfc.Result;
import tw.me.ychuang.rfc.exception.RfcException;
import tw.me.ychuang.rfc.exception.ServerSideException;

/**
 * Finds the matching callee and invokes it by naming rule and Java Reflection API.
 * 
 * @author Y.C. Huang
 */
public class CommandReflector extends CommandExecutor {
	private static final Logger log = LoggerFactory.getLogger(CommandReflector.class);

	/**
	 * A default constructor
	 */
	protected CommandReflector() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see tw.me.ychuang.rfc.server.CommandExecutor#execute(tw.me.ychuang.rfc.Command)
	 */
	@Override
	public Result execute(Command command) {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		if (classLoader == null) {
			classLoader = this.getClass().getClassLoader();
		}

		StringBuilder skeletonClsPath = new StringBuilder(50);
		String path = StringUtils.replaceChars(command.getSkeleton(), '.', '/');
		skeletonClsPath.append(path).append(".class");
		log.debug("Start to find a skeleton class name: {}, class path: {}", command.getSkeleton(), skeletonClsPath);

		Result result = null;
		try {
			// first, try to find the specified class of skeleton
			URL resource = classLoader.getResource(skeletonClsPath.toString());
			if (resource == null) {
				throw new ServerSideException("Fail to find the specified class of the specified skeleton.");
			}
			Class skeletonClass = classLoader.loadClass(command.getSkeleton());

			// second, try to find the invoked method by parameters and classes of parameters
			Method skeletonMethod = MethodUtils.getMatchingAccessibleMethod(skeletonClass, command.getMethod(), command.findParamClasses());
			if (skeletonMethod == null) {
				throw new ServerSideException("Fail to find the specified method of the specified skeleton.");
			}

			Object returnObj = null;
			if (command.isStaticMethod()) {
				// third, execute the static method without skeleton instance
				returnObj = skeletonMethod.invoke(null, command.findParameters());

			} else {
				Object skeletonInstance = null;
				// try to find a default constructor without parameter
				Constructor skeletonConstructor = ConstructorUtils.getMatchingAccessibleConstructor(skeletonClass, new Class[0]);
				if (skeletonConstructor == null) {
					// obtain unique instance by excuting getInstance method wrapped by singleton pattern
					Method getInstanceMethod = MethodUtils.getMatchingAccessibleMethod(skeletonClass, "getInstance", new Class[0]);
					if (getInstanceMethod != null) {
						skeletonInstance = getInstanceMethod.invoke(null, new Object[0]);
					}
				} else {
					// obtain a skeleton instance by default constructor without parameter
					skeletonInstance = ConstructorUtils.invokeConstructor(skeletonClass, new Object[0]);
				}

				if (skeletonInstance != null) {
					// finally, execute the instance method on constructed skeleton instance
					returnObj = skeletonMethod.invoke(skeletonInstance, command.findParameters());
				} else {
					throw new ServerSideException("Fail to construct a skeleton instance by constructor or singleton method.");
				}
			}

			if (skeletonMethod.getReturnType().equals(Void.TYPE)) {
				// return Void class since the method has no return
				result = Result.VOID_RETURN;
			} else {
				result = new Result(returnObj, returnObj.getClass());
			}
			log.info("Finish to execute the method of the skeleton class. skeleton: {}, method: {}", command.getSkeleton(), command.getMethod());

		} catch (InvocationTargetException e) {
			RfcException err = new ServerSideException("Fail to invoke the matching method of the specified skeleton class. skeleton: "
					+ command.getSkeleton() + ", method: " + command.getMethod(), e.getCause());
			err.addContextValue("Skeleton", command.getSkeleton()).addContextValue("Method", command.getMethod())
					.addContextValue("Parameters", command.getParameters()).addContextValue("Parameter Classes", command.getParamClasses());

			log.error(err.getMessage(), err);
			result = new Result(err, err.getClass());

		} catch (RfcException e) {
			e.addContextValue("Skeleton", command.getSkeleton()).addContextValue("Method", command.getMethod())
					.addContextValue("Parameters", command.getParameters()).addContextValue("Parameter Classes", command.getParamClasses());

			log.error(e.getMessage(), e);
			result = new Result(e, e.getClass());

		} catch (Throwable e) {
			RfcException err = new ServerSideException("Fail to invoke the matching method of the specified skeleton class. skeleton: "
					+ command.getSkeleton() + ", method: " + command.getMethod(), e);
			err.addContextValue("Skeleton", command.getSkeleton()).addContextValue("Method", command.getMethod())
					.addContextValue("Parameters", command.getParameters()).addContextValue("Parameter Classes", command.getParamClasses());

			log.error(err.getMessage(), err);
			result = new Result(err, err.getClass());

		} finally {
			if (result == null) {
				result = Result.VOID_RETURN;
			}
		}

		return result;
	}
}
