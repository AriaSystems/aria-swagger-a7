package ariasystems.client.api;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * test the generated api classes. This test uses a client in the stage future environment
 * 
 * @author anorman
 * 
 */
public abstract class BaseApiIT {
	private static final Logger logger = LoggerFactory.getLogger(BaseApiIT.class);
	protected static long ErrorCodeSuccess = 0L;
	protected static String ErrorMsgSuccess = "OK";
	protected static final Long ErrorCodeApiNotFound = -1L;
	protected static final Long ErrorCodeAuthError = 1004L;
	protected static final Long ErrorCodeUnknownError = 1001L;

	// values from stage future
	protected static final Long ClientNoA7 = 5000002L;
	protected static final String AuthTokenA7 = "rSdtXfhdUHk3T3g8YffrppSnVQUw88eQ";
	protected static final Long AcctNoA7 = 24029325L;

	protected static Set<Long> setupFailureCodes() {
		Set<Long> codes = new HashSet<Long>();
		codes.add(ErrorCodeApiNotFound);
		codes.add(ErrorCodeAuthError);
		return codes;
	}

	protected void validateSuccess(long errorCode, String errorMsg) {
		logger.trace("validate success");
		assertEquals(errorMsg, ErrorCodeSuccess, errorCode);
		assertEquals(ErrorMsgSuccess, errorMsg);
	}

}
