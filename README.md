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

## 快速开始

### 如何下载

#### Maven依赖

```Xml
<dependency>
	<groupId>cc.fozone.validation</groupId>
	<artifactId>fo-validation</artifactId>
	<version>0.9.0.RELEASE</version>
</dependency>
```

#### 或直接下载Jar包
[Validation.FO-0.9.0.RELEASE.jar](https://github.com/41zone/Validation.FO/releases/tag/0.9.0.RELEASE)

#### 下载配置文件

[Validation.FO-Configuration-0.9.0.zip](https://github.com/41zone/Validation.FO/releases/tag/0.9.0-CONFIGURATION)

#### 开源仓库

* GITHUB : [https://github.com/41zone/Validation.FO](https://github.com/41zone/Validation.FO)
* OS GIT : [https://git.oschina.net/41zone/Validation.FO](https://git.oschina.net/41zone/Validation.FO)

#### DEMO案例源代码

Demo可以在GITHUB中查看，[Validation.FO Demo](https://github.com/jimmy-song/fo-jimmysong-demo/tree/master/src/main/java/validationfo)

## 最基本的用法

#### 使用Validation.FO的步骤如下：

1. 配置验证规则 `rules.fo.xml`
2. 实例化 `IValidateService` 对象
3. 调用 `IValidateService.validate` 方法

### 第一个DEMO

源代码地址：[Basic Ustage](https://github.com/jimmy-song/fo-jimmysong-demo/tree/master/src/main/java/validationfo/basic)

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

```Xml
<?xml version="1.0" encoding="UTF-8"?>
<fozone-validation>
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