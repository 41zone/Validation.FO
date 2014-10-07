## 框架说明

1. 这是一个验证框架，并且是一个 **独立的验证框架** ，不依赖与其他已有的框架；
2. 可以自由的嵌入到其他框架，比如Spring、Struts等流行框架，但实质来说他是独立的，所以无所谓嵌入到哪里，如果需要在GUI桌面应用中，也是完美的；
3. 配置简单，可自由扩展验证器，实际只要实现IValidator接口，以及在rules.fo.xml中添加相关的配置即可；
4. 支持Spring接口
5. 使用过程中，你会感觉好像只用了 `IValidateService.validate()` 一个方法，这会让人感觉良好

## 优点

1. 不与任何对象做绑定，最大限度解耦
2. 只要规则配置写的合理，规则可以复用到多个对象
3. 扩展验证器很简单
4. 以Map存储验证结果，非常简单的导出JSON，只要你愿意

## 相关文档

* API : [https://github.com/41zone/Validation.FO/wiki](https://github.com/41zone/Validation.FO/wiki)
* 开源社区 : [GITHUB](https://github.com/41zone/Validation.FO)，[GITOSC](https://git.oschina.net/41zone/Validation.FO)
* DEMO案例源代码，[Validation.FO Demo](https://github.com/jimmy-song/fo-jimmysong-demo/tree/master/src/main/java/validationfo)


## 如何下载

* Maven依赖 或 直接下载[Validation.FO](https://github.com/41zone/Validation.FO/releases/)包

```Xml
<dependency>
	<groupId>cc.fozone.validation</groupId>
	<artifactId>fo-validation</artifactId>
	<version>0.9.0.RELEASE</version>
</dependency>
```

* **(必须)** 下载配置文件 [Validation.FO-CONFIGURATION](https://github.com/41zone/Validation.FO/releases/)

## 快速开始

#### 使用Validation.FO的步骤如下：

1. 配置验证规则 `rules.fo.xml`
2. 实例化 `IValidateService` 对象
3. 调用 `IValidateService.validate` 方法

### 基本的使用方法

源代码地址：[Basic Usage](https://github.com/jimmy-song/fo-jimmysong-demo/tree/master/src/main/java/validationfo/basic)

#### 1. 创建POJO对象 `User.java`

```Java
package validationfo.basic;

import java.sql.Timestamp;

/**
 * 用户对象
 * @author Jimmy Song
 *
 */
public class User {
	// 用户名
	private String username;
	
	// 密码
	private String password;
	// 再次输入密码
	private String passwordOne;
	
	// 邮箱
	private String email;
	
	// 开始与结束时间
	private Timestamp starttime;
	private Timestamp endtime;
	
	/**
	* Setter & Getter
	*/
	...	
}
```

#### 2. 配置验证规则 `rules.fo.xml`

* `<include file=""/> `
	导入其他规则文件
* `<group name=""></group>`
	验证分组，name应该为全局唯一的
* `<field name=""></field>`
	验证的字段，name是被验证对象的属性字段名
* `<rule name="" message=""></rule>`
	验证规则，name是验证器名称，message是错误后返回的消息
* `<param name="" value=""></param>`
	是验证规则时可能需要传入的参数，name是参数名，value是参数值

```Xml
<?xml version="1.0" encoding="UTF-8"?>
<fozone-validation>
	<!-- include 标签导入其他配置 -->
	<include file="validationinfo/basic/another-rules.fo.xml"/>
	 
	<!-- 验证组ID，全局唯一 -->
	<group name="user.validate">
		<!-- 验证字段 -->
		<field name="email">
			<!-- 
				规则列表
			-->
			<rule name="required" message="邮件必须填写"/>
		<rule name="between" message="邮件长度应该3-100之间">
			<param name="min" value="3"/>
			<param name="max" value="100"/>
		</rule>
			<rule name="match" message="邮件格式不正确">
				<param name="regex" value="^[A-Za-z]+[\.\-_A-Za-z0-9]*@[A-Za-z0-9]+[\.\-_A-Za-z0-9]*$"/>
			</rule>
		</field>

		...

	</group>
</fozone-validation>
```

#### 3. 实例化测试 `BasicTest.java`

**主要步骤：**

1. 创建配置读取对象`IValidateConfig`
2. 创建验证服务对象`IValidateService`
3. 执行验证方法`IValidateService.validate(object, groupId)`

```Java
package validationfo.basic;

...

/**
 * 最基本的测试
 * @author Jimmy Song
 *
 */
public class BasicTest {
	public static void main(String[] args) {
		/**
		 * Validation.FO的配置资源
		 */
		// 验证器配置，系统默认配置
		String validatorsXML = "validationfo/basic/validators.fo.xml";
		// 规则配置
		String rulesXML = "validationfo/basic/rules.fo.xml";
		
		/**
		 * 实例化配置对象
		 */
		IValidateConfig config =new BasicValidateConfig(validatorsXML, rulesXML);
		/**
		 * 实例化验证服务层
		 */
		IValidateService validateService = new BasicValidateService(config);
		
		// 实例化用户
		User user = createUser();
		
		/**
		 * 执行验证
		 */
		Map<String,String> map = validateService.validate(user, "user.validate");
		// 输出结果
		if(map == null || map.size() == 0) {
			System.out.println("验证成功");
		} else {
			System.out.println("验证失败，结果如下");
			System.out.println(map);
		}
	}
}
```

#### 4. 结果输出

```Java
12:31:41,084  INFO BasicValidateConfig:44 - read validation main file , validationfo/basic/rules.fo.xml
验证失败，结果如下
{email=邮件格式不正确, password=两次密码输入不正确, starttime=开始时间不能大于结束时间, endtime=结束时间不能小于开始时间}
```

## 如何在Spring中使用

1. 在Spring中需要使用 `SpringValidateConfg` 配置对象
2. 需要额外配置 `SpringValidator` Bean对象
3. [Spring DEMO 源代码下载](https://github.com/jimmy-song/fo-jimmysong-demo/tree/master/src/main/java/validationfo/spring)

#### 1. 配置Spring配置 `context.xml`

```Xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

	<!-- 基于 Spring 配置读取 -->
	<bean id="springValidateConfig" class="cc.fozone.validation.config.SpringValidateConfig">
		<property name="validators">
			<value>validationfo/spring/validators.fo.xml</value>
		</property>
		<property name="rules">
			<value>validationfo/spring/rules.fo.xml</value>
		</property>
	</bean>
	
	<!-- 配置验证服务 -->
	<bean id="basicValidateService" class="cc.fozone.validation.BasicValidateService">
		<constructor-arg index="0" ref="springValidateConfig"/>
	</bean>
	
	<!-- 配置基于Spring的验证器 -->
	<bean class="cc.fozone.validation.validators.SpringValidator"/>
</beans>
```

#### 2. 如何通过Spring进行验证，`SpringTest.java`

```Java
public class SpringTest {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
				"validationfo/spring/context.xml");

		// 获取验证服务
		IValidateService service = context.getBean(IValidateService.class);
		// 创建用户对象
		User user = createUser();

		// 执行验证
		Map<String, String> map = service.validate(user, "user.validate");
		// 输出结果
		if (map == null || map.size() == 0) {
			System.out.println("验证成功");
		} else {
			System.out.println("验证失败，结果如下");
			System.out.println(map);
		}
	}
}
```

#### 3. 执行结果

```Java
13:26:00,150  INFO ClassPathXmlApplicationContext:510 - Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@283b4947: startup date [Tue Oct 07 13:26:00 CST 2014]; root of context hierarchy
13:26:00,194  INFO XmlBeanDefinitionReader:315 - Loading XML bean definitions from class path resource [validationfo/spring/context.xml]
13:26:00,373  INFO DefaultListableBeanFactory:598 - Pre-instantiating singletons in org.springframework.beans.factory.support.DefaultListableBeanFactory@212b0f8a: defining beans [springValidateConfig,basicValidateService,cc.fozone.validation.validators.SpringValidator#0]; root of factory hierarchy
13:26:00,442  INFO BasicValidateConfig:44 - read validation main file , validationfo/spring/rules.fo.xml
验证失败，结果如下
{email=邮件格式不正确, password=两次密码输入不正确, starttime=开始时间不能大于结束时间, endtime=结束时间不能小于开始时间}
```

## Validator验证器与规则

系统默认的验证器文件 `validators.fo.xml`

```Xml
<?xml version="1.0" encoding="UTF-8"?>
<fozone-validators>
	<validator name="required" class="cc.fozone.validation.validators.RequiredValidator"/>
	<validator name="match" class="cc.fozone.validation.validators.MatchValidator"/>
	<validator name="between" class="cc.fozone.validation.validators.BetweenValidator"/>
	<validator name="min" class="cc.fozone.validation.validators.MinValidator"/>
	<validator name="max" class="cc.fozone.validation.validators.MaxValidator"/>
	<validator name="equals" class="cc.fozone.validation.validators.EqualsValidator"/>
	<validator name="timestampLessEqual" class="cc.fozone.validation.validators.TimestampLessEqualValidator"/>
	<validator name="timestampCreaterEqual" class="cc.fozone.validation.validators.TimestampCreaterEqualValidator"/>
	<validator name="spring"  useSpring="true" class="cc.fozone.validation.validators.SpringValidator"/>
</fozone-validators>
```

### 默认验证规则

#### `required` - 必填字段

消息message： **必填**

参数param： 无

案例：

```Xml
<group name="user.validate">
	<field name="username">
		<rule name="required" message="姓名必须填写。"/>
	</field>
</group>
```

***

#### `match` - 正则匹配

消息message： **必填**

参数param： **有**

|Name|Must|Description|
|:--|:--:|:--|
|regex|Yes|正则表达式，如果为空，验证直接返回true|

案例：

```Xml
<group name="user.validate">
	<field name="email">
		<rule name="match" message="邮件格式不正确">
			<param name="regex" value="^[A-Za-z]+[\.\-_A-Za-z0-9]*@[A-Za-z0-9]+[\.\-_A-Za-z0-9]*$"/>
		</rule>
	</field>
</group>
```

***

#### `between` - 判断字符串或数组非空长度是否介于两者之间，min <= length <= max

消息message: **必填**

参数param： **有**

|Name|Must|Description|
|:--|:--:|:--|
|min|Yes|最小长度(包含)|
|max|Yes|最大长度(包含)|

案例：

```Xml
<group name="user.validate">
	<field name="email">
		<rule name="between" message="邮件长度应该3-100之间">
			<param name="min" value="3"/>
			<param name="max" value="100"/>
		</rule>
	</field>
</group>
```

***

#### `min` - 判断字符串或数组非空长度是否大于等于最小长度，length >= min

消息message: **必填**

参数param： **有**

|Name|Must|Description|
|:--|:--:|:--|
|value|Yes|最小长度(包含)|

案例：

```Xml
<group name="user.validate">
	<field name="password">
		<rule name="min" message="密码至少5个字符">
			<param name="value" value="5"/>
		</rule>
	</field>
</group>
```

***

#### `max` - 判断字符串或数组非空长度是否小于等于最大长度，length <= max

消息message: **必填**

参数param： **有**

|Name|Must|Description|
|:--|:--:|:--|
|value|Yes|最大长度(包含)|

案例：

```Xml
<group name="user.validate">
	<field name="password">
		<rule name="max" message="密码最多20个字符">
			<param name="value" value="20"/>
		</rule>
	</field>
</group>
```

***

#### `equals` - 判断字段是否与指定的字段值是否相同

消息message: **必填**

参数param： **有**

|Name|Must|Description|
|:--|:--:|:--|
|target|Yes|指定的字段名称，并非确定的值|

案例：

POJO对象部分字段

```Java
public class User {
	private String password;
	private String passwordOne;
}
```

```Xml
<group name="user.validate">
	<field name="password">
		<rule name="equals" message="两次输入的密码不相同">
			<param name="target" value="passwordOne"/>
		</rule>
	</field>
</group>
```

> 注：这里会将`password`与目标`passwordOne`字段的值进行`equals`比较
> 有些时候你可能需要重写`equals`方法

***

#### `timestampLessEqual` - 时间戳是否小于等于指定的目标时间字段

消息message: **必填**

参数param： **有**

|Name|Must|Description|
|:--|:--:|:--|
|target|Yes|指定的时间字段名称，并非确定的值|

案例：

POJO对象部分字段

```Java
public class User {
	private Timestamp starttime;
	private Timestamp endtime;
}
```

```Xml
<group name="user.validate">
	<field name="starttime">
		<rule name="timestampLessEqual" message="开始时间不能大于结束时间">
			<param name="target" value="endtime"/>
		</rule>
	</field>
</group>
```

**注：**

1. 这里会将`starttime`与目标`endtime`字段的值进行大小比较
2. 这里的字段必须是`java.sql.Timestamp`类型

***

#### `timestampCreaterEqual` - 时间戳是否大于等于指定的目标时间字段

消息message: **必填**

参数param： **有**

|Name|Must|Description|
|:--|:--:|:--|
|target|Yes|指定的时间字段名称，并非确定的值|

案例：

POJO对象部分字段

```Java
public class User {
	private Timestamp starttime;
	private Timestamp endtime;
}
```

```Xml
<group name="user.validate">
	<field name="endtime">
		<rule name="timestampCreaterEqual" message="结束时间不能小于开始时间">
			<param name="target" value="starttime"/>
		</rule>
	</field>
</group>
```

**注：**

1. 这里会将`endtime`与目标`starttime`字段的值进行大小比较
2. 这里的字段必须是`java.sql.Timestamp`类型

***

#### `spring` - 通过Spring调用其他对象的指定方法进行判断

DEMO： [点击查看](https://github.com/jimmy-song/fo-jimmysong-demo/tree/master/src/main/java/validationfo/springadvance)

消息message: **必填**

参数param： **有**

|Name|Must|Description|
|:--|:--:|:--|
|beanName|Yes|Spring Context中的Bean标识|
|methodName|Yes|Bean对象中的指定执行方法，该方法必须返回 **boolean** 类型|
|parameterName|No|执行方法中传递的参数，**默认** 传递被验证字段的值，如果使用**this** 表示传递被验证的对象， **其他** 表示传递对象指定的字段|

案例：

**POJO对象部分字段**

```Java
public class User {
	private String username;
	private int usernameLength;
}
```

**验证配置**

```Xml
<group name="user.validate">
	<field name="username">
		<rule name="spring" message="该用户已存在">
			<param name="beanName" value="userService"/>
			<param name="methodName" value="exist"/>
		</rule>
	</field>
	
	<field name="usernameLength">
		<!-- 使用 this 关键字 -->
		<rule name="spring" message="用户名长度验证错误">
			<param name="beanName" value="userService"/>
			<param name="methodName" value="length"/>
			<param name="parameter" value="this"/>
		</rule>
		
		<!-- 指定字段 -->
		<rule name="spring" message="用户名长度不能小于5个字符">
			<param name="beanName" value="userService"/>
			<param name="methodName" value="lengthByName"/>
			<param name="parameter" value="username"/>
		</rule>
	</field>
</group>
```

**Spring中的配置**

```Xml
<beans>
	<!-- 通过Spring进行验证 -->
	<bean id="userService" class="validationfo.springadvance.UserService"/>
</beans>
```

**通过Spring验证的接口**

```Java
package validationfo.springadvance;

public class UserService {
	public boolean exist(String value) {
		return !"superman".equals(value);
	}
	
	public boolean length(User user){
		return user.getUsername().length() == user.getUsernameLength();
	}
	
	public boolean lengthByName(String username){
		return username.length() >= 5;
	}
}
```
**注：**

1. 要启用spring配置，需要参照上述的 **如何在Spring使用** 进行Spring与Validation.FO的整合配置
2. 指定的对象方法必须返回 **boolean** 类型

## 高级部分：如何自定义验证器 `IValidator`

1. 实现 `IValidator` 接口
2. 在 `validators.fo.xml` 验证器配置中添加已实现的验证器
3. [点击这里查看Demo源代码](https://github.com/jimmy-song/fo-jimmysong-demo/tree/master/src/main/java/validationfo/custom)

### 一个简单的例子

**POJO对象**

```Java
package validationfo.custom;

public class Money {
	private int number;
	private String text;
	
	...
	
}
```

**CustomValidator实现**

```Java
package validationfo.custom;
/**
 * 构造一个判断 参数 是否能够被 指定的值 整出的验证器
 * 例如：对象值为4 ，验证参数为2，那么 4可以被2整除，表示成功
 * 
 * @author Jimmy Song
 *
 */
public class CustomValidator implements IValidator {
	private static final Logger logger = Logger.getLogger(CustomValidator.class);
	
	/**
	 * 要处理的对象
	 * @param context 上下文
	 * @param type 对象类型
	 * @param value 对象值
	 * @param rule 对象参数
	 * @return 是否成功
	 */
	public boolean execute(Object context, Class type, Object value, Rule rule) {
		// TODO Auto-generated method stub
		/**
		 * 这里只是演示一下context的用处
		 * 可以通过context以及反射获取对象的属性值
		 * 举个例子
		 */
		try {
			System.out.println("TEXT = "+PropertyUtils.getProperty(context, "text"));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
		}
		
		/**
		 * 通过 value 实际验证的值
		 */
		if(value == null) return true;
		
		/**
		 * 通过 type 获取值的类型
		 */
		if(!(type == Integer.class || type == int.class)) {
			return false;
		}
		int objectValue = ((Integer)value).intValue();
		
		
		/**
		 * 通过 rule 获取规则参数
		 */
		String toValue = rule.getParameter("value");
		if(StringUtils.isBlank(toValue)) return false;
		
		int intValue = -1;
		try {
			intValue = Integer.parseInt(toValue);
		} catch(NumberFormatException e){
			logger.error("倍数值转换错误！");
			return false;
		}
		
		if(intValue <= 0) {
			logger.warn("倍数值不能小于等于0");
			return false;
		}
		
		return objectValue % intValue == 0;
	}

}
```

**配置 validators.fo.xml 中添加自定义验证器**

关于validators.fo.xml配置的参数说明

**语法** ： `<validator name="" useSpring="true|false"  class=""  beanId=""/>`

|参数名|必填|含义|
|:--|:--|:--|
|name|是|验证器名称|
|useSpring|否|是否通过 **Spring** 实例化验证器，这样做可以为验证器注入 **ApplicationContext** |
|class|否|当 **useSpring=false** 时，按照正常的方式进行实例化；当 **useSpring=true** 时，且使用了 **beanId** ，则 **class** 无效，如果没有使用 **beanId** ，那么 **Spring** 通过 **class** 进行实例化|
|beanId|否|Spring中的bean id|


```Xml
<?xml version="1.0" encoding="UTF-8"?>
<fozone-validators>
	...
	<validator name="custom" class="validationfo.custom.CustomValidator"/>
</fozone-validators>
```

**配置验证规则**

```Xml
<?xml version="1.0" encoding="UTF-8"?>
<fozone-validation>
	<!-- 验证组ID，全局唯一 -->
	<group name="money.validate">
		<!-- 验证字段 -->
		<field name="number">
			<rule name="custom" message="验证失败，不能被指定的数整除">
				<param name="value" value="3"/>
			</rule>
		</field>
	</group>
</fozone-validation>
```

**执行测试**

```Java
package validationfo.custom;

public class CustomTest {
	public static void main(String[] args) {
		/**
		 * Validation.FO的配置资源
		 */
		// 验证器配置，系统默认配置
		String validatorsXML = "validationfo/custom/validators.fo.xml";
		// 规则配置
		String rulesXML = "validationfo/custom/rules.fo.xml";
		
		/**
		 * 实例化配置对象
		 */
		IValidateConfig config =new BasicValidateConfig(validatorsXML, rulesXML);
		/**
		 * 实例化验证服务层
		 */
		IValidateService validateService = new BasicValidateService(config);
		
		// 实例化数据
		Money money = new Money();
		money.setNumber(7);
		money.setText("演示Context使用的Text");
		
		/**
		 * 执行验证
		 */
		Map<String,String> map = validateService.validate(money, "money.validate");
		// 输出结果
		if(map == null || map.size() == 0) {
			System.out.println("验证成功");
		} else {
			System.out.println("验证失败，结果如下");
			System.out.println(map);
		}
	}	
}
```

**结果输出**

```Java
16:44:17,178  INFO BasicValidateConfig:44 - read validation main file , validationfo/custom/rules.fo.xml
TEXT = 演示Context使用的Text
验证失败，结果如下
{number=验证失败，不能被指定的数整除}
```