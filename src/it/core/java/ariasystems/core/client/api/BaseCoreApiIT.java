package ariasystems.core.client.api;


import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.OkHttpClient;

import ariasystems.client.api.BaseApiIT;
import ariasystems.core.client.ApiClient;

public abstract class BaseCoreApiIT extends BaseApiIT {
	private static final Logger logger = LoggerFactory.getLogger(BaseCoreApiIT.class);
	protected static final String TestBasePath = "https://secure.future.stage.ariasystems.net/v1/core";
	
	protected static final String TestBasePathFuture = "https://secure.future.stage.ariasystems.net/v1/core";
	protected static final String TestBasePathProd = "https://secure.ariasystems.net/v1/core";

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

}
