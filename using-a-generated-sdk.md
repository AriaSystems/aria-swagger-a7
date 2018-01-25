# User guide for using a generated swagger sdk to call the aria endpoint

## Generic Swagger SDK usage, standards, and support
The swagger/ OpenAPI standard and swagger code generation are open source and community driven. For 
generic information about how to use a swagger client, please refer to one of the following swagger 
sites:

[swagger docs](https://swagger.io/docs/)
[swagger community user's forum](https://swagger.io/forum/)
[swagger community real-time discussion site](https://swagger.io/irc/)

## Using a swagger-generated SDK with Aria
  This section talks about how to use a generated SDK to call Aria Core APIs. There are many different languages that one can generate a Swagger SDK. 
  Since SDKs are language specific and generate with their own templates by swagger there can 
  be differences in how they might be structured or used. Below we describe how a java 
 -based client is structured in Swagger. Some of this description will be specific 
 to the java SDK and some will be pertainent to any language-based generation

### SDK language-specific README file
  once generated the SDK often has a companion [README file](./target/generated-sources/README.md) that lives in the generated-sources directory. 
  It will contain instructions on how to use / initialize / embed the SDK in your codebase. 
  This file is a recommended read for initial SDK setup into your codebase. 
 
### Java based clients
  This section describes the layout and usage of a swagger client that is generated in java 
  using the structure that configured in our [example maven client generation script](./example-client-generation-script.xml)

### General layout
  Our example client java-based generation sets up the SDK to generate in a "./target/generated-sources" 
directory. Inside this directory you will see the following structure:
  * docs - contains documentation in markdown (.md) format describing each API and schema object
  * src/main/java - the generated SDK source code
  * src/test/java - test stubs of classes that you can build out into writing integration 
  tests

### Code layout

In Swagger, classes are named using Java's "CamelCase" conventional naming standard as opposed to the "under_score" format commonly
used to describe Aria APIs and data structures. For example the API, "create_order_m", is represented 
in the class, "CreateOrderM". The data structure commonly referred to as "invoice_detail" 
is represented by the class, "InvoiceDetail". 

#### API classes
In Swagger, every Aria API is generated as an individual class. All API classes are 
located in the "ariasystems.core.client.API" package. Each API class offers three ways 
to call its represented API:
  * Simplified
  * with HTTP info
  * Asynchronous
  
##### Simplified APIs
  This type of API is the most straightforward way to call an Aria endpoint. 
  It takes a single "body" parameter that represents the 'Request' payload to send 
  to the Aria Core API endpoint. It waits for the endpoint to respond and either returns 
  a Response object containing the Aria Core response or it can throw an exception 
  if there was a failure in the transmission. These methods are named the same way 
  as the API class they reside in except that they are in "lowerCamelCase". For example 
  the "acctPlanInstallCompleteM" method found in the AcctPlanInstallCompleteM class.

##### 'with HTTP info' APIs
  This type of API works nearly the same way as the above described simplified  
  API. The one difference is that, instead of returning the Response object containing 
  the data returned by the endpoint, this returns an APIResponse object. The APIResponse object 
  contains HTTP info like response codes and header info as well as the data payload 
  Response object. These methods are named with a "WithHttpInfo" suffix (such as acctPlanInstallCompleteMWithHttpInfo).

##### Asynchronous API
  This type of API enables the caller to make the call to the Aria endpoint asynchronously. In 
  addition to the standard request parameter that the other APIs require, a second 'callback'is 
  defined. The callback parameter signifies the logic to invoke upon an successful 
  response from the server and logic to invoke upon a failure scenario. The method 
  returns a Call object which can be used to gather the current status of the API call or 
  to cancel it. These methods are named with an "Async" suffix (such as acctPlanInstallCompleteMAsync).

#### Model classes
Model classes are found in the ariasystems.core.client.model project. Each API has 
a specific Request object that represents the request payload sent to the Server endpoint as well as a Response object 
that represents the data returned. Each of these objects are named after the API they 
are associated with it. For example the "acct_plan_install_complete_m" / "AcctPlanInstallCompleteM" API has a request 
object named AcctPlanInstallCompleteMRequest and a response object named "AcctPlanInstallCompleteMResponse". 

The Request and Response objects themselves are simple POJOs that contain "getters" and 
"setters" for the fields they contain. Each of these fields are typed in their expected 
format. For example, a textual field will be typed as a String. Numeric fields will have 
a numeric type (either as a long or as a BigDecimal). Fields with a compound structure 
to them will be defined as a typed object that represents that compound structure. Request 
objects always contain getters and setters for clientNo and authKey values used for the 
endpoint's authentication and authorization.


#### Calling an API
To call an Aria Core API, simply follow the following steps:

  * instantiate an object from the class that represents the target Aria core API.
  * instantiate an object from the class that represents the target APIs request payload 
  and populate it with the data to send to the API.
  * invoke one of the three types of API method described above in the API classes 
  section.
  * wait for the call to complete and process the response object returned. (In the 
  case of the asynchronous API method types you are not waiting for the response as you've 
  already provided the response handing in the callback parameter)

#### API example

        CreateInstanceContractMRequest body = new CreateInstanceContractMRequest();
        body.setClientNo(ClientNoA7);
        body.setAuthKey(AuthTokenA7);
        body.setAcctNo(AcctNoA7);
        body.setTypeNo(3L);
        body.setLengthMonths(18L);

        ArrayList<ContractPlanInstance> contractPlanInstances = new ArrayList<>();
        ContractPlanInstance contractPlanInstance = new ContractPlanInstance();
        contractPlanInstances.add(contractPlanInstance);

        contractPlanInstance.setPlanInstanceNo(PlanInstanceA7);
        body.setContractPlanInstances(contractPlanInstances);

        CreateInstanceContractMResponse response = API.createInstanceContractM(body);
        logger.info("response: {}", response);
        validateSuccess(response.getErrorCode(), response.getErrorMsg());
        assertTrue(response.getContractNo() > 0);


## Best Practices for calling Aria APIs

  * ensure that your SDK is using the right endpoint (production, stage future, stage current, 
etc). To ensure the proper target endpoint you can manually set it in the ApiClient attribute of the API classes. 
On the java SDK the endpoint is set via the ApiClient.setBasePath(String basePath). 
method.
  * configuring client timeout times - The default timeout times on the generated SDK 
  normally need to larger values when dealing with production payloads. These can be 
  set in the ApiClient attribute's httpClient attribute. For the java client there 
  are 3 different timeout values to set:
    * connectTimeout - via httpClient.setConnectTimeout()
    * readTimeout - via httpClient.setReadTimeout()
    * writeTimeout - via httpClient.setWriteTimeout()

