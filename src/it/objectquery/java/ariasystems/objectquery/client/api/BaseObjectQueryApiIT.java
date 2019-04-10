package ariasystems.objectquery.client.api;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.OkHttpClient;

import ariasystems.client.api.BaseApiIT;
import ariasystems.objectquery.client.ApiClient;

/**
 * test the generated api classes. This test uses accounts in the QU env farm
 * 
 * @author anorman
 *
 */
public abstract class BaseObjectQueryApiIT extends BaseApiIT {
	private static final Logger logger = LoggerFactory.getLogger(BaseObjectQueryApiIT.class);
	protected static final String ClientSkuA7 = "Test";

	protected static final String TestBasePath = "https://secure.future.stage.ariasystems.net/api/AriaQuery/objects.php";

	protected static ApiClient createTestApiClient() {
		logger.trace("create TestApiClient");
		ApiClient client = new ApiClient();
		OkHttpClient httpClient = client.getHttpClient();
		client.setBasePath(TestBasePath);
		httpClient.setConnectTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS);
		httpClient.setReadTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS);
		httpClient.setWriteTimeout(5 * 60 * 1000, TimeUnit.MILLISECONDS);
		return client;
	}

	@Override
	protected void validateSuccess(long errorCode, String errorMsg) {
		logger.trace("validate success");
		assertEquals(errorMsg, ErrorCodeSuccess, errorCode);
		assertEquals(ErrorMsgSuccess, errorMsg);
	}

}
