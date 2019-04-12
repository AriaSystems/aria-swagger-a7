package ariasystems.core.client.api;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ariasystems.core.client.ApiException;
import ariasystems.core.client.model.GetAcctDetailsAllMRequest;
import ariasystems.core.client.model.GetAcctDetailsAllMResponse;

public class GetAcctDetailsAllMApiIT extends BaseCoreApiIT {
	private static final Logger logger = LoggerFactory.getLogger(GetAcctDetailsAllMApiIT.class);

	private GetAcctDetailsAllMApi api = setupApi();

	private GetAcctDetailsAllMApi setupApi() {
		GetAcctDetailsAllMApi myApi = new GetAcctDetailsAllMApi();
		myApi.setApiClient(createTestApiClient());
		return myApi;
	}

	@Test
	public void testSuccess() throws ApiException {
		GetAcctDetailsAllMRequest body = new GetAcctDetailsAllMRequest();
		body.setClientNo(ClientNoA7);
		body.setAuthKey(AuthTokenA7);
		body.setAcctNo(AcctNoA7);

		GetAcctDetailsAllMResponse response = api.getAcctDetailsAllM(body);
		logger.info("response: {}", response);
		validateSuccess(response.getErrorCode(), response.getErrorMsg());
		Long outAcct = response.getAcctNo2();

		assertTrue(outAcct > 0L);
	}

}
