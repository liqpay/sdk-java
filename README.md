liqpay.ua API SDK for Java
===========================

[![Build Status](https://travis-ci.org/stokito/sdk-java.png?branch=master)](https://travis-ci.org/stokito/sdk-java)

[liqpay.ua](https://www.liqpay.ua/) is payment system associated with [PrivatBank](https://privatbank.ua/). 

API Documentation [in Russian](https://www.liqpay.ua/documentation/ru) and [in English](https://www.liqpay.ua/documentation/en)

**WARNING:** This SDK is not thread safe. We would be very appreciated for your contribution.

Installation and usage
----------------------

This library is published at [GitHub](https://github.com/liqpay/sdk-java/raw/repository) and can be added as Maven dependency.

### Use as Maven dependency

Add to your `pom.xml` repository and dependency:

```xml
<repositories>
        <repository>
            <id>repository</id>
            <url>https://github.com/liqpay/sdk-java/raw/repository</url>
            <snapshots>
                <enabled>true</enabled>
                <updatePolicy>always</updatePolicy>
            </snapshots>
        </repository>
    </repositories>

<dependency>
            <groupId>com.liqpay</groupId>
            <artifactId>liqpay-sdk</artifactId>
            <version>0.7-SNAPSHOT</version>
  </dependency>
```

Then you can use it as described in API documentation:
 
```java
// Creation of the HTML-form
Map params = new HashMap();
params.put("amount", "1.50");
params.put("currency", "USD");
params.put("description", "description text");
params.put("order_id", "order_id_1");   
params.put("sandbox", "1"); // enable the testing environment and card will NOT charged. If not set will be used property isCnbSandbox() 
LiqPay liqpay = new LiqPay(PUBLIC_KEY, PRIVATE_KEY);
String html = liqpay.cnb_form(params);      
System.out.println(html);
```

It is recommended to use some Inversion of Control (IoC) container, like [Spring IoC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html) or [PicoContainer](http://picocontainer.codehaus.org/). 

#### Use proxy

To use `LiqPay` with proxy you can initialize it like:

```java
import java.net.InetSocketAddress;
import java.net.Proxy;

...

    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("proxy.host.com", 8080);
    LiqPay liqpay = new LiqPay(PUBLIC_KEY, PRIVATE_KEY, proxy, "proxyLogin", "some proxy password");
```


### Grails v2.x

In `grails-app/conf/BuildConfig.groovy` you should add repository and dependency:

```groovy
grails.project.dependency.resolution = {
...
    repositories {
        grailsPlugins()
        ...
        mavenRepo 'https://github.com/liqpay/sdk-java/raw/repository'
    }
    dependencies {
        ...
        compile 'com.liqpay:liqpay-sdk:0.7-SNAPSHOT'
    }
...
}
```

Then you can add `LiqPay` bean in `grails-app/conf/spring/resources.groovy`:

```groovy
import com.liqpay.LiqPay

// Place your Spring DSL code here
beans = {
    liqpay(LiqPay, '${com.liqpay.publicKey}', '${com.liqpay.privateKey}') {
        cnbSandbox = false // set true to enable the testing environment. Card is not charged
    }
}
```

It will create bean with name `liqpay` of class `com.liqpay.LiqPay` and pass to it's constructor public and private keys that defined in `grails-app/conf/Config.groovy` like this: 

```groovy
com.liqpay.publicKey = 'i31219995456'
com.liqpay.privateKey = '5czJZHmsjNJUiV0tqtBvPVaPJNZDyuoAIIYni68G'
```

Then you can use this `liqpay` bean with dependency injection in your services or controllers: 

```groovy
class UserController {
    LiqPayApi liqpay // this will inject liqpay bean defined in resources.groovy  

    def balanceReplenishment() {
        Map<String, String> params = [
                "amount"     : '30.5',
                "currency"   : 'UAH',
                "description": 'Balance replenishmenton on example.com',
                "order_id"   : "1",
                'result_url' : g.createLink(action: 'paymentResult', absolute: true).toString()]
        String button = liqpay.cnb_form(params);
        [button: button]
    }
}
```

And inside `grails-app/views/user/balanceReplenishment.gsp` you can output this button like this:

```gsp
    <div>
        ${raw(button)}
    </div>
```


Changelog
---------

[All releases](https://github.com/stokito/sdk-java/releases)

### v0.1 First Mavenized version.

[Source](https://github.com/stokito/grails-cookie/releases/tag/v0.1)

- Just reformatted code.
- Created some basic tests.
- API wasn't changed and this release can't broke compilation.

### v0.2 Improved tests

[Source](https://github.com/stokito/grails-cookie/releases/tag/v0.2)

- Refactoring
- More tests coverage
- Parameter `params` of methods `cnb_form()` and `api()` now can by any `Map`, not only `HashMap`. 
- API wasn't changed and this release can't broke compilation.

### v0.3 Some methods deprecated

[Source](https://github.com/stokito/grails-cookie/releases/tag/v0.3)

- Introduced API interface `LiqPayApi`
- Deprecated fields that should be constant `host_checkout` and `liqpayApiUrl`. They was replaced with private constants.
- Deprecated constructor `LiqPay(String publicKey, String privateKey, String liqpayApiUrl)` because `liqpayApiUrl` is constant and can't be rewritten.
- Deprecated method `cnb_signature` because signature is already calculated inside `cnb_form(Map)`.
- Deprecated shorthand method `setProxy(String host, Integer port)`, you should use full `setProxy(String host, Integer port, Proxy.Type)` instead. In next release v0.5 it will be deprecated too, and you should construct `Proxy` instance yourself.
- API wasn't changed and this release can't broke compilation.

### v0.4 Last release that API compatible with old lib

[Source](https://github.com/stokito/grails-cookie/releases/tag/v0.4)

- This release is recommended if you used original old lib since it shouldn't break compilation.   
- Params `version` and `public_key` are always set inside `cnb_form()` and `api()` methods.
- Old version of `cnb_form()` accepted `public_key` parameter that can be differ from `publicKey`initialized in constructor. 
- Methods `cnb_form()` and `api()` doesn't add `public_key` and `version` to instance of `params` method. I.e. now you can pass unmodifable map and reuse it without side effects. 
- API wasn't changed and this release can't broke compilation.


### v0.5 Removed deprecated methods

[Source](https://github.com/stokito/grails-cookie/releases/tag/v0.5)

- Removed deprecated method `cnb_signature` because signature is already calculated inside `cnb_form(Map)`.
- Method `api()` now returns general `Map` instead of concrete `HashMap`.
- Removed deprecated fields `liqpayApiUrl` and `host_checkout`. They replaced with constants `LiqPayApi.LIQPAY_API_URL` and `LiqPayApi.LIQPAY_API_CHECKOUT_URL`.
- Introduced two new properties `proxyLogin` and `proxyPassword` that should be used instead of deprecated method `setProxyUser(login, password)`.
- Introduced method `setProxy(Proxy)` that should be used instead of shorthand and deprecated `setProxy(host, port, Proxy.Type)`. 
- API **was changed** in this release and can broke compilation.

### v0.6 Enhanced usage

[Source](https://github.com/stokito/grails-cookie/releases/tag/v0.6)

- Created constructor `LiqPay(String publicKey, String privateKey, Proxy proxy, String proxyLogin, String proxyPassword)` that initialize API with proxy
- Defined new property `isCnbSanbox()` that can globally set `sandbox` param in `cnb_form()` instead of specifying it always in `params`


### v0.7-SNAPSHOT
- Changed url form liqpay.com to liqpay.ua

### v0.8-SNAPSHOT
- Fixed a checkout url from `/api/3/checkout` to `/api/checkout`.
