sdk-java
========

LiqPay JAVA sdk 


#### Создание кнопки для оплаты ####

```java
LiqPay liqpay = new LiqPay(public_key, private_key);
HashMap<String, String> params = new HashMap<String, String>();
params.put("amount", "1.2");
params.put("currency", "USD");
params.put("description", "my comment");
params.put("language", "en");
String form = liqpay.cnb_form(params);
System.out.println(form);
```

### Возможные параметры ###

**параметр**                    | **обязательный**
--------------------------------|--------------------------------
`amount`                        | `Да`
`currency`                      | `Да`
`description`                   | `Да`
`order_id`                      | `Нет`
`result_url`                    | `Нет`
`server_url`                    | `Нет`
`type`                          | `Нет`
`language`                      | `Нет`
`order_id`                      | `Нет`



#### Создание сигнатуры для оплаты ####
```java
LiqPay liqpay = new LiqPay(public_key, private_key);
HashMap<String, String> params = new HashMap<String, String>();
params.put("amount", "1.2");
params.put("currency", "USD");
params.put("description", "my comment");
params.put("language", "en");
String cnb_signature = liqpay.cnb_signature(params);
System.out.println(cnb_signature);
```



#### Проверка статуса платежа ####
```java
LiqPay liqpay = new LiqPay(public_key, private_key);
HashMap<String, String> params = new HashMap<String, String>();
params.put("order_id", "order_id_123");
HashMap<String, Object> result = liqpay.api("payment/status", params);
System.out.println(result.get("status"));
```