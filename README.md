<a href="http://predixdev.github.io/predix-websocket-client/javadocs/index.html" target="_blank" >
	<img height="50px" width="100px" src="images/javadoc.png" alt="view javadoc"></a>
&nbsp;
<a href="http://predixdev.github.io/predix-websocket-client" target="_blank">
	<img height="50px" width="100px" src="images/pages.jpg" alt="view github pages">
</a>
# predix-websocket-client



Welcome to Predix Web Socket Client, a [Microcomponent](https://github.com/PredixDev/predix-rmd-ref-app/blob/master/docs/microcomponents.md) Utility.

The predix-websocket-client project provides websocket utility methods with helpers to manage Predix OAuth Security in the cloud. All the reference app microservices use this utility to make WebSocket calls in the cloud.

1. Most of the time, you will make a dependency to predix-websocket-client by adding this to your pom.xml
  
  ```xml
 	<dependency>
		<groupId>com.ge.predix.solsvc</groupId>
		<artifactId>predix-websocket-client</artifactId>
		<version>${predix-websocket-client.version}</version>
	</dependency>
  ```
  
  
1. If you started with a [Predix Microservice Template](https://github.com/predixdev/predix-microservice-templates) you will have a property in config/application.properties which spins up certain beans marked with the Local Profile.  Otherwise, add this property to your project.
  ```
  spring.profiles.active=local
  ```
  
1. You also will want to check that Autowiring of these package is occurring by adding this file to your spring context 
  ```xml
  	"classpath:META-INF/spring/predix-websocket-client-scan-context.xml" 
  
  	
  	which contains
	<context:component-scan
            base-package="
		com.ge.predix.solsvc.websocket.client
        " />

  ```
##Implement the Interface IWebSocketConfig  
1. In your microservice that uses the Predix-Websocket-Client, implement IWebSocketConfig interface. This interface sets all the websocket related properties required by the WebSocketClient object. This design enables you to connect to more than one websocket server through your microservice. For each websocket server connection provide a set of websocket connections with a unique property name such as below:

  ```
	#properties for WS connection #1
	predix.timeseries.test.websocket.uri1=wss://put.your.first.service.instance.here/v1/stream/messages
	predix.timeseries.test.zoneid1=put.your.first.zoneid.aka.instanceid.here
	
	predix.timeseries.test.websocket.pool.maxIdle1=5
	predix.timeseries.test.websocket.pool.maxActive1=5
	predix.timeseries.test.websocket.pool.maxWait1=8000
	
	#properties for WS connection #2
	predix.timeseries.test.websocket.uri2=wss://put.your.second.service.instance.here/v1/stream/messages
	predix.timeseries.test.zoneid2=put.your.second.zoneid.aka.instanceid.here
	
	predix.timeseries.test.websocket.pool.maxIdle2=5
	predix.timeseries.test.websocket.pool.maxActive2=5
	predix.timeseries.test.websocket.pool.maxWait2=8000
   ```
1. Now, implement the instances of the interface IWebSocketConfig. For an example please view [TestWebSocketConfig1.java](https://github.com/PredixDev/predix-websocket-client/blob/develop/src/test/java/com/ge/predix/solsvc/websocket/test/config/TestWebSocketConfig1.java)

1. Or to view the source code you can download the project  
  ```sh
  $ git clone https://github.com/PredixDev/predix-websocket-client.git  
  
  $ cd predix-websocket-client
  
  $ mvn clean package  
  
    note: mvn clean install may run integration tests against services you may not have set up yet
  ```
##Proxy Settings
If you are behind a corporate proxy, please set the following in the properties file:
```
predix.oauth.proxyHost=(For Example: sjc1intproxy01.crd.ge.com)

predix.oauth.proxyPort=(For Example: 8080)
```
##Running the Integration Tests (IT)
The ITs include tests to create a connection to a user specified Timeseries service instance, post data using different formats and sizes, and close connection. In order to execute the ITs, please follow these instructions:

1) Set the following properties in predix-websocket-client/config/application.properties with your settings:
```
 #e.g. predix.oauth.issuerId=https://36492c1e-657c-4377-ac51-add963552460.predix-uaa.cloud.com/oauth/token

 predix.oauth.issuerId=https://put.your.uaa.issuerId.here

 #you may put client:secret as unencoded cleartext by setting predix.oauth.clientIdEncode=true
 predix.oauth.clientIdEncode=false
 predix.oauth.clientId=you.should.base64encode(put.your.clientId:put.your.clientSecret separated by a colon)  

 #e.g. predix.timeseries.websocket.uri=wss://gateway-predix-timeseries.cloud.com/v1/stream/messages

 predix.timeseries.websocket.uri=wss://put.your.websocket.service.instance.here/v1/stream/messages

 predix.timeseries.zoneid=put.your.websocket.endpoint.zoneid.aka.instanceid.here 
```
2) If you are behind a corporate proxy, please uncomment and set these properties as well:
```
predix.oauth.proxyHost =put.your.proxyHost.here

predix.oauth.proxyPort=put.your.proxyPort.here
```

##Dependencies
|Required - latest unless specified | Note |
| ------------- | :----- |
| Java 8 | |
| GitHub Acct | logged in |
| Git | |
| Maven | https://artifactory.predix.io/artifactory/PREDIX-EXT |
| CloudFoundry ClI 6.12.2 |  https://github.com/cloudfoundry/cli/tree/v6.12.2#downloads.  There is bug on this page, so you have to manually get the URL and the add "&version=6.12.2".  For example for Windows32 it would look like this...https://cli.run.pivotal.io/stable?release=windows32&source=github&version=6.12.2 |

[![Analytics](https://ga-beacon.appspot.com/UA-82773213-1/predix-websocket-client/readme?pixel)](https://github.com/PredixDev)
