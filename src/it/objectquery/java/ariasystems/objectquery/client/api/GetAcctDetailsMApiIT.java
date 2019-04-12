package ariasystems.objectquery.client.api;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import ariasystems.objectquery.client.ApiException;
import ariasystems.objectquery.client.model.AccountDetailsM;
import ariasystems.objectquery.client.model.GetAccountDetailsMRequest;
import ariasystems.objectquery.client.model.GetAccountDetailsMResponse;

public class GetAcctDetailsMApiIT extends BaseObjectQueryApiIT {
	private static final Logger logger = LoggerFactory.getLogger(GetAcctDetailsMApiIT.class);

	private GetAccountDetailsMApi api = setupApi();

	private GetAccountDetailsMApi setupApi() {
		GetAccountDetailsMApi myApi = new GetAccountDetailsMApi();
		myApi.setApiClient(createTestApiClient());
		return myApi;
	}

	@Test
	public void testSuccess() throws ApiException {
		GetAccountDetailsMRequest body = new GetAccountDetailsMRequest();
		body.setClientNo(ClientNoA7);
		body.setAuthKey(AuthTokenA7);
		body.setQueryString("acct_no="+ AcctNoA7);

		GetAccountDetailsMResponse response = api.getAccountDetailsM(body);
		logger.info("response: {}", response);
		validateSuccess(response.getErrorCode(), response.getErrorMsg());
		List<AccountDetailsM> acctDetails = response.getAccountDetailsM();

		assertTrue(acctDetails.size() > 0);
	}

}
