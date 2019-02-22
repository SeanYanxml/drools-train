### 前言

> Author Sean / Date 2019-02-22

本子项目主要包括如下几个要点:

1. 动态加载规则,并将项目与SpringBoot相结合.
2. `ksession.insert(pojo)`参数互相传递和回调.
3. `ksession.insert(service)`传递Spring单例的`service`.

---

### Demos

[1] localhost:8080/test/helloworld

```
# output

Rule Before - Message: STATUS_1 status:1
Hit Transmit HelloWorld Rule.
Rule After - Message: STATUS_2 status:2
```
RELATING-CLASS:HelloworldController

[2] http://localhost:8080/message/insert

```
# output

Hit Invoke HelloWorld Rule.
Message Service Insert SQL.
```
RELATING-CLASS:MessageServiceController
