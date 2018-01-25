package ariasystems.core.client.api;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.OkHttpClient;

import ariasystems.core.client.ApiClient;

/**
 * test the generated api classes. This test uses a client in the stage future environment
 * 
 * @author anorman
 *
 */
public abstract class BaseApiIT {
	private static Logger logger = LoggerFactory.getLogger(BaseApiIT.class);
	protected static long ErrorCodeSuccess = 0L;
	protected static String ErrorMsgSuccess = "OK";
	protected static final Long ErrorCodeApiNotFound = -1L;
	protected static final Long ErrorCodeAuthError = 1004L;
	protected static final Long ErrorCodeUnknownError = 1001L;

	// values from stage future
	protected static final Long ClientNoA7 = 5000002L;
	protected static final String AuthTokenA7 = "rSdtXfhdUHk3T3g8YffrppSnVQUw88eQ";

	protected static final String TestBasePath = "https://api.future.stage.ariasystems.net/api/ws/api_ws_class_dispatcher.php";

	protected static Set<Long> setupFailureCodes() {
		Set<Long> codes = new HashSet<Long>();
		codes.add(ErrorCodeApiNotFound);
		codes.add(ErrorCodeAuthError);
		return codes;
	}

	protected static ApiClient createTestApiClient() {
		logger.trace("setting up client");
		ApiClient client = new ApiClient();
		OkHttpClient httpClient = client.getHttpClient();
		client.setBasePath(TestBasePath);
		httpClient.setConnectTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS);
		httpClient.setReadTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS);
		httpClient.setWriteTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS);
		logger.trace("finished setting up client");
		return client;
	}

	protected void validateSuccess(long errorCode, String errorMsg) {
		assertEquals(ErrorCodeSuccess, errorCode);
		assertEquals(ErrorMsgSuccess, errorMsg);
	}

}
