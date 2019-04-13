# Users guide for generating Aria Core API clients from Swagger / Open API

## Swagger overview
We provide a Swagger-based description of our Crescendo core and object query APIs. Swagger is a format for describing REST-based APIs 
and can be written in one of two different formats (either JSON or YAML). It can be thought of as a similar mechanism for REST-based APIs 
as WSDLs are for SOAP-based ones. Compared to WSDL files, Swagger files can contain more information 
about its described APIs and is also thought to be much more human readable than WSDL.
Describing our APIs in Swagger allows the ability to generate an SDK client to access the APIs in one of many languages supported
by the Swagger open source community. In addition to API SDK generation, Swagger also provides an 
HTML-based user interface, "SwaggerUI", that provides a user-friendly way to view APIs, to describe their parameters and responses, and to 
structure live calls to them.

## Swagger UI
Swagger UI is the html-based application that reads a Swagger file and renders its 
information in a user friendly way. You can find an online version of the SwaggerUI 
[here](http://petstore.swagger.io/). The online SwaggerUI preloads with the 'Swagger Petstore' demo, but you 
can load any public-accessible Swagger file using the explore link at the top of the 
page. In addition to the online copy of Swagger UI, you can also download your own 
copy that you can run locally and can use with local Swagger files.

## Swagger-codegen
Swagger-codegen is the open source library used to generate typed clients for calling 
Swagger-defined APIs. This allows for a very seamless way for clients to invoke our 
APIs. Swagger clients pass arguments and return objects as typed objects. This typed 
format makes large APIs like ours more manageable to use. Many of the language-based 
client platforms provide additional ways to make asynchronous calls and can perform 
client-side validations. The open-source community for Swagger-codegen continue to 
push new functionality out to these platforms over time.

### languages supported by the Swagger client code generator

Swagger community-supported clients include: 

 * Andriod

 * Csharp

 * Cpprest

 * Flash

 * Groovy

 * HTML

 * Java (9 or more varieties)

 * Javascript / Node.js (several different varieties including angular)

 * Perl

 * PHP

 * Python

 * Ruby

 * Scala (3 varieties)

 * Swift

 * Typescript (several different varieties including Angular, jQuery, and Node)

The full list of currently supported clients and languages and more information about 
  the generator can be found [here](https://github.com/swagger-api/swagger-codegen#overview) and 
   [here](https://swagger.io/swagger-codegen/)

## Obtaining a swagger file from a target environment

Swagger files are obtained from the target environment that you want Apis described 
from. Each environment provides 
an individual swagger file per each describable Api set: (Core or Object Query)

the url for swagger-described core apis:
    https://{environment host}/v1/core/a7/api-docs/all_A7_swagger.json

the url for swagger-described object-query apis:
    https://{environment host}/v1/objectquery/a7/api-docs/all_A7_swagger.json

note: we do not offer swagger described apis for the admin tools apis or for any of the A6 
apis.

## How to generate client SDKs from Swagger

The Swagger SDKs can be generated in one of two ways with the Swagger Codegen open-source library.  
One way is to generate them using a Maven build script (either directly with the aria-swagger-a7 GitHub 
project or with your own local script). Another way is to generate them directly via Java.

### Generation option 1: generating with the Swagger-codegen Maven plugin 

An easy way to generate the Swagger-codegen in a repeatable way is by using the Swagger-codegen Maven plugin. 
Using the Maven plugin allows the ability to configure the client generation settings 
so it can be invoked in a repeatable way. In addition to running the generation from 
a command line, this plugin also allows the ability to generate the SDK from within a Maven-supported IDE (like eclipse or InteliJ). 

This approach requires Java 8 and Apache Maven 3.3.3 or greater as prerequisites.

#### Using the aria-swagger-a7 GitHub project

We have a GitHub project, aria-swagger-a7, that can be used to generate an SDK. The project is configured with 
a Maven pom.xml and a swagger file describing the latest production A7 APIs.  To 
use this project to generate your SDK clone the aria-swagger-a7 GitHub project locally and follow 
the configuration and generation steps listed below.

to generate an internal SDK from this repository the following command (requires maven):

__mvn clean install__

##### command parameters: 

__-Dapi__

 * applies the api profile (configures the set of APIs to generate)
 * values:
    * core - generates a client for Core A7 Apis (default)
    * objectquery - generates a client for ObjectQuery A7 Apis
   
__-Dlang__

 * applies the language profile (configures the language of the client)
 * values: 
    * java (default)
    * php 
    * scala 
    * additional supported languages can be added by implementing the language profile pattern
    
__-Denv__

 * applies the environment profile (configures the environment to pull the swagger 
   from)
 * values: 
    * sf (default)
    * sf-eu
    * sf-aus
    * sc
    * sc-eu
    * sc-aus
    * prod
    * prod-eu
    * prod-aus
    

##### usage examples:

  * __"mvn clean install"__
   generates an A7 core java SDK with the current swagger file on the stage future environment. (default 
   settings)
   Note: The java profile is enabled to run IT tests against stage future to validate the connectivity of the APIs defined in the SDK

 * __"mvn clean install -Dapi=objectquery -Dlang=php -Denv=prod-eu"__
    
   generates an A7 objectquery PHP SDK with the latest swagger form the prod-eu environment
Note: The php profile uses PHP specfic naming for the generated sdk files and tells maven to not attempt to compile or jar the output

 * __"mvn clean install -Dapi=core -Dlang=scala -Denv=sf-aus"__
    
   generates an A7 core scala SDK with the current swagger file on the sf australian environment

 * __"mvn clean install -P php,oq,sc"__
   generates an A7 objectquery php SDK with the current swagger file on the stage current environment. 
   (This example uses the shortened -P profile parameter format to chain profiles)
    

##### additional generation notes:

 * the api,lang, and env parameters apply profiles. If none of these are specified they default to enabling the A7 and java profiles
 * if the api parameter is set, then the lang and env parameters must also be set.
 * if the lang parameter is set, then the api and env parameters must also be set.
 * if the env parameter is set, then the api and lang parameters must also be set.
 * values used in parameters are case sensitive and are always lower case

#### Using your own Maven project to build an sdk
If you would rather use your own independent Maven project to generate an SDK you can 
do that too. (see our example Maven client generation script below as a reference on 
how to configure the script). 

#### Configuring your Maven script
Ensure that the pom.xml file is configured to use the correct settings for the following values in the  
Swagger-codegen Maven plugin configuration section: 
  * inputSpec - points to the correct location/name of the target Swagger file
  * language - designates the target code language for the generated SDK (Java, PHP, 
  Ruby, etc)
  * output - designates the target directory for the generated SDK files
  * apiPackage, modelPackage - these are used to define the package location for the api 
    and model classes. They are pre-configured with dot-notation used for many languages 
    like Java, Scala, etc to defined package hierarchies. The dot notation is incompatible  
    with some languages, such as PHP (which uses slashes instead of dots in its namespace 
    values). If you are generating with a language that doesn't support dot notation, 
    you'll want to set apiPackage and modelPackage to a compoatible equivalent for 
    the language being generated.

#### Executing your Maven script
the Maven script can be executed as:

```
mvn clean install
``` 

### Generation option 2: Generating the SDKs from Java command line

#### Prerequisites for generating the SDK

 * Java 8 - [download](https://java.com/en/download/)
 * Apache Maven 3.3.3 or greater - [download](https://maven.apache.org/download.cgi)  

#### Obtaining / building the generator jar

```
git clone https://github.com/swagger-api/swagger-codegen
cd swagger-codegen
mvn clean package
```

the above script builds a swagger-codegen-cli.jar that is found in the modules/swagger-codegen-cli/target 
subdirectory. This jar will be used in the command to generate the Swagger 
client.

Swagger-codegen can be directly invoked from a command line using a Java command with -D parameters. 

```
java -jar ./swagger-codegen-2.2.1.jar generate \
  -i ./all_A7_13.0_swagger.json \
  -l java \
  -o ariaClient
```

The above example showcases a couple of the generate command options:

 * -i is the reference to the Swagger file that defines the client APIs

 * -l is the type of programming language to generate the client in

 * -o is the name of the subdirectory for the target client SDK files to be generated 

You can get the full list of available described command options using the help generate command: 

```
java -jar Swagger-codegen-cli.jar help generate
```

Additional details about the Java generate client functionality can 
be found on the [Swagger-api Github page](https://github.com/swagger-api/swagger-codegen#to-generate-a-sample-client-library)

## Compiling the generated SDK files
SDKs that are generated in a compiled language require an additional step of compiling the generated 
SDK code into the binaries required for execution. To aid in this process, depending 
on your target-language, you may find a Maven-based pom.xml file in the ./target/generated-sources 
directory. This Maven file can be used to compile and artifact the jar. This is done 
by running the following command in the ./target/generated-sources directory: 
```
mvn clean install
``` 

## Notes on SDK generation 
  * the generated pom.xml may reference old or dated library dependencies. As 
    a result you may want to update these references to use more current libraries when 
    generating your binaries.
  * Scala SDK generation - the pom.xml generated for scala is configured to compile 
    against Scala 2.10; however, this version of Scala is incompatible with the Aria Core APIs. This is 
    because the 2.10 version of Scala limits case classes to have fewer than 22 parameters 
    and Aria's Apis entities can have 22 or more fields. Because of this, Scala generations 
    need to be manually set and compiled against Scala 2.11 or higher.
  * CsharpDotNet2 SDK generation - Aria's Core API is incompatible with the CSharpDotNet2 
    SDK generation. This is due to the use of a language reserved word, 'Event', as a response attribute in the UnsubscribeEventClassM API.   
  * SDK language specific README file - once generated the SDK often has a companion 
    README file that lives in the generated-sources directory. It will contain instructions on how to use / initialize / 
    embed the SDK in your codebase.  

## Sample Maven configuration for generating Aria Core APi Swagger clients:
[example-client-generation-script.xml](./example-client-generation-script.xml) is our sample Maven 
script for generating the Aria client with the Swagger-codegen Maven plugin

Additional details on the Swagger-codegen Maven plugin can be found on its [github site](https://github.com/swagger-api/swagger-codegen/tree/master/modules/swagger-codegen-maven-plugin#swagger-codegen-maven-plugin)

## Using a generated SDK
[details on how to use a generated SDK to call Aria APIs](./using-a-generated-sdk.md)

## Important links

 * [Swagger spec guide](https://swagger.io/specification/)

 * [Swagger home page](https://swagger.io/)

 * [Swagger UI home page](https://swagger.io/swagger-ui/)

 * [Swagger codegen home page](https://swagger.io/swagger-codegen/)

 * [Swagger git-hub site](https://github.com/swagger-api)

 * [Swagger codegen git-hub site](https://github.com/swagger-api/swagger-codegen)

 * [about Swagger-codegen-maven-plugin usage and configuration parameters](https://github.com/swagger-api/swagger-codegen/tree/master/modules/swagger-codegen-maven-plugin)

 * [Maven download info for the Swagger-codegen Maven plugin](https://mvnrepository.com/artifact/io.swagger/swagger-codegen-maven-plugin)



