package ariasystems.core.client.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ariasystems.core.client.ApiClient;

/**
 * this tester generically tests each generated api to test that it reaches its
 * matching api on the server endpoint. As this test doesn't handle Api-specific
 * data, it wont test for successful calls. This just ensures that the call
 * returns without an auth or api not found error_code.
 * 
 * @author anorman
 *
 */
public class GenericApiIT extends BaseApiIT {
	private static final String PackageClientApi = "ariasystems.core.client.api";
	private static final String PackageClientModel = "ariasystems.core.client.model";

	private static final String SuffixApiClass = "Api";
	private static final String SuffixRequestClass = "Request";
	private static final String GetterNameErrorCode = "getErrorCode";
	private static final String GetterNameErrorMessage = "getErrorMsg";
	private static final String SetterNameClientNo = "setClientNo";
	private static final String SetterNameAuthKey = "setAuthKey";
	private static final String SetterNameApiClient = "setApiClient";

	private static final Logger logger = LoggerFactory.getLogger(GenericApiIT.class);

	private Set<Long> failureCodes = setupFailureCodes();

	private static Set<String> testComplete = new HashSet<String>();
	private static Set<String> testSuccesses = new HashSet<String>();
	private static Map<String, String> testFailures = new HashMap<String, String>();

	private Set<Class<?>> apiClasses = initializeApiClasses();
	private Map<String, Class<?>> modelClasses = initializeModelClasses();

	private Set<Class<?>> initializeApiClasses() {
		logger.trace("setting up api classes");
		Reflections apiPackage = createPackageReflections(PackageClientApi);
		return apiPackage.getSubTypesOf(Object.class);
	}

	private Map<String, Class<?>> initializeModelClasses() {
		logger.trace("setting up model classes");
		Reflections modelPackage = createPackageReflections(PackageClientModel);

		// build the model class map
		Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
		for (Class<?> modelClass : modelPackage.getSubTypesOf(Object.class)) {
			classMap.put(modelClass.getSimpleName(), modelClass);
		}
		return classMap;
	}

	private static Reflections createPackageReflections(String packageName) {
		return new Reflections(new ConfigurationBuilder().setScanners(new SubTypesScanner(false)).setUrls(ClasspathHelper.forPackage(packageName))
				.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(packageName))));
	}

	private void markTestAsFailure(String method, String resultMessage) {
		testFailures.put(method, resultMessage);
		testComplete.add(method);
	}

	private void markTestAsSuccess(String method) {
		testSuccesses.add(method);
		testComplete.add(method);
	}

	private void setApiClient(Object apiObject)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		apiObject.getClass().getDeclaredMethod(SetterNameApiClient, ApiClient.class).invoke(apiObject, createTestApiClient());
	}

	private void injectAuthValuesIntoRequest(Object request)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		request.getClass().getDeclaredMethod(SetterNameClientNo, Long.class).invoke(request, ClientNoA7);
		request.getClass().getDeclaredMethod(SetterNameAuthKey, String.class).invoke(request, AuthTokenA7);
	}

	@SuppressWarnings("unchecked")
	private <T> T extractResponseValue(Object response, String getter, Class<T> expectedType) {
		try {
			return (T) (response.getClass().getDeclaredMethod(getter).invoke(response));
		} catch (Exception e) {
			throw new RuntimeException("apiResponse: " + response.getClass().getSimpleName() + " method call failure: " + getter, e);
		}
	}

	private Method getApiMethod(Class<?> apiClass, String apiMethodName) {
		for (Method method : apiClass.getMethods()) {
			if (StringUtils.equals(method.getName(), apiMethodName)) {
				return method;
			}
		}
		throw new RuntimeException("apiClass: " + apiClass.getSimpleName() + " method not found: " + apiMethodName);
	}

	private Object initializeApi(Class<?> apiClass) {
		try {
			Object apiInstance = apiClass.newInstance();
			setApiClient(apiInstance);
			return apiInstance;
		} catch (Exception e) {
			throw new RuntimeException("failed to initialize apiClass: " + apiClass.getName(), e);
		}
	}

	private Object initializeRequest(String requestClassName) {
		logger.trace("finding request class -> {}", requestClassName);
		Class<?> requestClass = modelClasses.get(requestClassName);

		try {
			Object requestObject = requestClass.newInstance();
			injectAuthValuesIntoRequest(requestObject);

			return requestObject;
		} catch (Exception e) {
			throw new RuntimeException("failed to initialize requestClassName: " + requestClassName, e);
		}
	}

	/**
	 * this method prepares an Api Class for testing and executes the test
	 * 
	 * @param apiClass
	 */
	private void testApi(Class<?> apiClass) {
		String rawClassName = apiClass.getSimpleName();
		if (StringUtils.endsWith(rawClassName, SuffixApiClass)) {

			logger.debug("begin api test -> {}", rawClassName);

			String reflectionRoot = StringUtils.removeEnd(rawClassName, SuffixApiClass);

			Object apiInstance = initializeApi(apiClass);
			Object requestObject = initializeRequest(reflectionRoot + SuffixRequestClass);

			Method method = getApiMethod(apiClass, StringUtils.uncapitalize(reflectionRoot));

			invokeApiMethod(method, apiInstance, requestObject);
		}
	}

	/**
	 * this method invokes the designated API and performs a pass / fail check
	 * 
	 * @param method
	 * @param apiInstance
	 * @param requestObject
	 */
	private void invokeApiMethod(Method method, Object apiInstance, Object requestObject) {
		String methodName = method.getName();
		logger.debug("invoking method -> {}", methodName);
		Object response = null;
		try {
			response = method.invoke(apiInstance, requestObject);
		} catch (Exception e) {
			logger.warn("exception invoking method: " + methodName, e);
			String errorMessage = "exception: " + e.getClass().getSimpleName() + ", " + e.getMessage();
			logger.info("method: {} returns {}", method.getName(), errorMessage);
			markTestAsFailure(methodName, errorMessage);
			return;
		}

		Long errorCode = extractResponseValue(response, GetterNameErrorCode, Long.class);
		String errorMessge = extractResponseValue(response, GetterNameErrorMessage, String.class);
		logger.info("method: {} returns errorCode: {}, errorMessage: {}", method.getName(), errorCode, errorMessge);
		if (failureCodes.contains(errorCode)) {
			markTestAsFailure(methodName, errorMessge);
		} else {
			markTestAsSuccess(methodName);
		}
	}

	/**
	 * this is the Junit integration test that validates that all apis can
	 * successfully invoke its target Api on the QU system. This test ensures
	 * that each api can authenticate and find its matching api (which is enough
	 * to say that the "basic plumbing works for this API")
	 * 
	 * This test does not test for valid success calls (i.e. error_code of zero)
	 * as that scenario will require custom Api specific testing.
	 */
	@Test
	public void testAllApisForConnectivity() {
		logger.info("running reflective API tests");
		try {
			logger.trace("begin api iterations");

			// iterate through the API classes and invoke them
			for (Class<?> apiClass : apiClasses) {
				testApi(apiClass);
			}
		} finally {
			writeTestSummary();
		}
		if (testFailures.size() > 0) {
			Assert.fail("there are " + testFailures.size() + " test failures");
		}

	}

	private List<String> convertSetToSortedList(Set<String> set) {
		List<String> sorted = new ArrayList<String>();
		sorted.addAll(set);
		Collections.sort(sorted);
		return sorted;
	}

	private void outputLabel(String labelName) {
		logger.info("");
		logger.info("");
		logger.info("{}: ", labelName);
		logger.info("---------------------------------------------------------------------------------");
		logger.info("");
	}

	private void outputResultSet(String resultName, Set<String> set) {
		outputLabel(resultName);
		for (String row : convertSetToSortedList(set)) {
			logger.info("     api: {}", row);
		}
	}

	private void outputResultMap(String resultName, Map<String, String> map) {
		outputLabel(resultName);
		for (String key : convertSetToSortedList(map.keySet())) {
			logger.info("     api: {}, cause: {}", key, map.get(key));
		}
	}

	private void writeTestSummary() {
		logger.info("");
		logger.info("");
		logger.info("");
		logger.info("");
		outputLabel("Test Results");
		outputResultSet("Apis Tested", testComplete);
		outputResultSet("Test Successes", testSuccesses);
		outputResultMap("Test Failures", testFailures);
		outputLabel("Test Summary");
		logger.info("     total apis tested: {}", testComplete.size());
		logger.info("     total successes: {}", testSuccesses.size());
		logger.info("     total failures: {}", testFailures.size());
	}

}
