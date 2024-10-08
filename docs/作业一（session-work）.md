> **学院：省级示范性软件学院**
> 
> 题目：《作业一：session会话技术》
> 
> 姓名：潘文宝
> 
> 学号：2200770201
> 
> 班级：软工2203
> 
> 日期：2024-09-14

# 会话安全性

## 1. 会话劫持和防御

**会话劫持：** 会话劫持是指攻击者通过网络攻击，通过网络攻击者获取到用户会话信息，从而冒充用户进行攻击。

**防御措施：** 
1. 更改 `Session` 的名称，如果攻击者不分析站点，就猜不到 `Session` 的名称，能够阻挡部分攻击。
2. 关闭透明化 `Session ID`。
3. 设置 `HttOnly`。设置这个属性，可以防止客户端脚本访问这个Cookie，从而防止 `XSS` 攻击。
4. 验证请求头信息。
5. 使用 `Https` 协议。

```java
Session session = req.getSession();

// 设置 Cookie
Cookie sessionCookie = new Cookie("JSESSIONID", session.getId());

// 防御 XSS
sessionCookie.setHttpOnly(true);

// 通过 HTTPS 发送
sessionCookie.setSecure(true);

resp.addCookie(sessionCookie);

// 设置会话超时时间
session.setMaxInactiveInterval(30 * 60); // 30min

```

## 2. 跨站脚本攻击（XSS）和防御

攻击者在目标网站中注入恶意脚本，普通用户访问这个网站的时候，脚本被执行，导致用户的会话信息被窃取。

**例如：** 一个搜索页面根据 URL 参数决定关键词内容。功能代码如下：

```html
<input type="text" value="<%= getParameter("keyword") %>">
<button>搜索</button>
<div>
  您搜索的关键词是：<%= getParameter("keyword") %>
</div>
```
上线不久后，有一个神秘链接

`http://xxx/search?keyword="><script>alert('XSS');</script>`

如果脚本是一串恶意代码，则会导致这个恶意代码被执行，用户信息泄漏。

> 来自：[前端安全系列（一）：如何防止XSS攻击？](https://segmentfault.com/a/1190000016551188)

**如何防范？**

1. 前端开发时注意预防DOM型XSS攻击。
2. 对用户输入进行过滤。
3. 前端代码和数据分离。
4. 字符转义，防止部分XSS攻击。

## 3. 跨战请求伪造（CSRF）和防御

CSRF攻击的核心在于利用受害者浏览器中的认证凭据（如Cookie、Session等），向受信任的网站发送非预期的HTTP请求。由于这些请求附带了受害者的认证信息，因此，Web服务器可能会误认为这些请求是合法用户的行为，从而执行相应的操作，如转账、修改密码等。

**例如：**

假设银行网站有转账功能，URL结构为：`https://bank.example/transfer?to_account=12345&amount=1000`/

如果该操作没有合适的CSRF防护措施，攻击者可以通过构造恶意网页，诱使已登录银行网站的用户访问，从而在用户不知情的情况下发起转账请求。

**攻击原理：**

CSRF之所以有效，是因为Web浏览器遵循同源策略（Same-Origin Policy），但不会阻止从一个站点向另一个站点发送请求。当用户在银行网站保持登录状态时，其认证信息（Cookie）会被自动附加到任何向该网站发出的请求上，即便这个请求是由第三方网站触发的。

**防御措施：**

1. `Token` 验证
2. `Referer` 验证
3. 设置 `Cookie` 的 `SameSite` 属性
4. 设置严格的 `Content Security Policy`

> [腾讯云社区](https://cloud.tencent.com/developer/article/2413657)

# 分布式会话管理

## 1. 分布式环境下的会话同步问题

HTTP 协议是无状态的，它没有状态保持机制，无法实现会话状态的共享。Session通常存储在服务器端，
在单机环境下使用Session没有问题，但当系统部署在多台服务器上时，会产生Session共享问题。

例如，用户访问A服务器，A服务器存储了Session,但是用户第二次访问时，变成了B服务器，此时B服务器没有存储Session，导致用户无法获取到Session，出现用户访问异常。

## 2. Session集群解决方案

1. 集群间 `Session` 复制同步。

优点：tomcat支持配置，仅需修改配置文件。
缺点：同步时占用服务器资源（网络、存储都占用）。

2. 客户端存储

优点：节省服务器资源。
缺点：客户端发送请求，需要携带Session信息，导致每次请求浪费带宽，且容易泄漏。

3. Hash一致性

通过负载均衡，将请求分配到同一个服务器上，不存在跨服务器同步的问题。

优点：不需要修改服务器代码，负载均衡配置简单。
缺点：可能会存在部分用户路由出现错误，导致用户访问异常。

## 3. 使用Redis等缓存技术实现分布式会话

也可以使用Redis等缓存技术实现分布式会话。将用户的信息存入第三方的Redis,做统一存储，所有服务器都从Redis中获取用户信息，从而实现Session共享。

优点：服务器重启不会造成Session丢失。
缺点：请求需要经过Redis，增加网络开销。

# 会话状态的序列化和反序列化

## 1. 会话状态的序列化和反序列化

将`Java`的对象序列化成字节流，再反序列化成对象。

**序列化：** 将对象转换为字节流。
**反序列化：** 将字节流转换为对象。

## 2. 为什么需要序列化会话状态

**持久化存储：** 将对象保存到文件或者数据库中，方便后续使用。
**数据交换：** 方便在网络中传递Java对象。

## 3. Java对象序列化

Java提供了一个接口，`java.io.Serializable`，这个接口用于标识一个对象可以被序列化。

**例如：**

```java
package entity;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String username;
    private String password;
    private String email;
}
```

## 4. 自定义序列化策略

实现 `Externalizable` 接口，重写 `writeExternal` 和 `readExternal` 方法，定制化对象的序列化和反序列化过程

**例如：**

```java
package entity;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class UserInfoV2 implements Externalizable {
    private String username;
    private String password;
    private String email;

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(username);
        objectOutput.writeObject(password);
        objectOutput.writeObject(email);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        username = (String) objectInput.readObject();
        password = (String) objectInput.readObject();
        email = (String) objectInput.readObject();
    }
}

```