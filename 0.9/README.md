# Validation API for 0.9 version

## 框架说明
1. 这是一个验证框架，并且是一个独立的验证框架，不依赖与其他已有的框架； 
2. 可以自由的嵌入到其他框架，比如Spring、Struts等流行框架，但实质来说他是独立的，所以无所谓嵌入到哪里，如果需要在GUI桌面应用中，也是完美的；
3. 配置简单，可自由扩展验证器，实际只要实现`IValidator`接口，以及在`rules.fo.xml`中添加相关的配置即可；
4. 提供`struts2`的简单标签使用，其他的标签可以自己编写；
5. 添加了一个对`Spring`特殊的验证扩展；
6. 在使用时，会高兴的发现这的确给自己扩展验证提供了很好的支持，因为这个很简单。

## 快速使用
### ValidateSample.java
	public class ValidateSample {
		/**
		 * 执行验证
		 */
		public void doValidate(){
			...
		}
		
		/**
		 * 用户名，必须实现GETTER方法
		 */
		private String username;
		
		public String getUsername() {
			return username;
		}
	
		public void setUsername(String username) {
			this.username = username;
		}
		
		public static void main(String[] args) {
			new ValidateSample().doValidate();
		}
	}

#### doValidate方法的实现

	public void doValidate(){
		/*
		 * 实例化配置，默认配置在CLASSPATH下的validators.fo.xml以及rules.fo.xml两个文件中
		 */
		IValidateConfig config = new BasicValidateConfig();
		
		/*
		 * 实例化服务
		 */
		IValidateService service = new BasicValidateService(config);
		
		/*
		 * 执行验证；
		 * 
		 * 在执行验证之前需要在rules.fo.xml规则文件中配置好规则
		 * 
		 * 需要传入两个参数，
		 * 第一个参数：需要被验证的对象，不可以为空
		 * 第二个参数：在验证规则中的 规则组代号
		 * 
		 * 执行验证后返回验证结果集合，以Map形式返回
		 */
		Map<String,String> results = service.validate(this, "group.sample");
		
		/*
		 * 通过验证结果集是否为空来判断是否验证通过
		 */
		if(results.isEmpty()) {
			System.out.println("验证通过！");
		} else {
			System.err.println("验证不通过，有验证失败的项目！");
			
			/*
			 * 验证失败的字段存储在KEY中，而VALUE则是验证的失败的提示字符串
			 */
			Set<String> keys = results.keySet();
			Iterator<String> it = keys.iterator();
			while(it.hasNext()) {
				String key = it.next();
				System.out.println(key+"（验证不通过）");
				System.out.println("原因："+results.get(key));
			}
		}
	}

**运行结果**

	21:23:09,958  INFO BasicValidateConfig:44 - read validation main file , rules.fo.xml
	验证不通过，有验证失败的项目！
	username（验证不通过）
	原因：用户名必须要填写！

**编写重点**

1. 读取配置，`IValidateConfig config = new BasicValidateConfig();`
2. 实例化服务，`IValidateService service = new BasicValidateService(config);`
3. 配置相关规则，编写`rules.fo.xml`文件
4. 执行验证，`service.validate(this,"group.sample");`

**配置文件**

	<!--rules.fo.xml-->
	<?xml version="1.0" encoding="UTF-8"?>
	<fozone-validation>
		<group name="group.sample">
			<field name="username">
				<rule name="required" message="用户名必须要填写！"/>
			</field>
		</group>
	</fozone-validation>

## 编写配置
### 配置接口 IValidateConfig
**接口中只有一个方法`readConfiguration`**

	/**
	 * 验证框架的配置器
	 * @author jimmysong
	 *
	 */
	public interface IValidateConfig {
		/**
		 * 读取配置
		 * @return 配置对象
		 */
		public Configuration readConfiguration();
	}

**默认使用`BasicValidateConfig`类就可以满足基本的验证要求**

	public class BasicValidateConfig implements IValidateConfig {
		private String validators,rules;
		
		/*
		* 默认的构造方法默认设定了两个配置文件名称
		* validator.fo.xml, 验证器配置
		* rules.fo.xml, 验证规则配置
		*/
		public BasicValidateConfig(){
			this("validators.fo.xml","rules.fo.xml");
		}
		
		/*
		* 当然可以自己通过传入参数进行设定
		*/
		public BasicValidateConfig(String validators, String rules) {
			this.validators = validators;
			this.rules = rules;
		}
	}

**提供对Spring的接入支持，`SpringValidateConfig`，在后续讲述**

	public class SpringValidateConfig extends BasicValidateConfig implements ApplicationContextAware{...}

**重点注意**，配置文件必须放置在**CLASSPATH**环境目录下

### 配置文件
如上所述，配置文件包括两类，分别是`验证器`和`验证规则`配置。

#### 验证器配置：`validators.fo.xml`

1. 提供了默认**9种**规则控制器；
2. 可以自己通过实现`cc.fozone.validation.IValidator`接口构建自己的验证规则；

**9种默认验证器**

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
		<validator name="spring" class="cc.fozone.validation.validators.SpringValidator" spring="springValidator"/>
	</fozone-validators>

#### 验证规则配置：`rules.fo.xml`

1. 以组`group`为单元划分，name必须唯一，不能发生重复；
2. 可以使用`include`通过引入外部文件，有效做配置分类；
3. **规则不与任何对象做绑定**，一组配置可能会应用不同的对象做验证条件；

**规则结构**

	<fozone-validation>
		<group name="组名">
			<field name="验证的字段名">
				<rule name="上述验证器的name值" message="错误提示消息"/>
				<rule name="上述验证器的name值" message="错误提示消息">
					<param name="对应验证器的参数名" value="参数值"/>
				</rule>
			</field>
		</group>
		<include file="引入CLASSPATH路径" />
	</fozone-validation>

**案例参考**

	<group name="student.save">
		<field name="student.name">
			<rule name="required" message="姓名必须填写。"/>
			<rule name="max" message="姓名最多20个字符">
				<param name="value" value="20"/>
			</rule>
		</field>
		
		<field name="student.phone">
			<rule name="required" message="手机号必须填写"/>
			<rule name="match" message="手机号规则错误,只能是纯数字">
				<param name="regex" value="[0-9]*"/>
			</rule>
			<rule name="spring" message="该手机号已存在！">
				<param name="beanName" value="studentServiceImpl"/>
				<param name="methodName" value="queryStudentNotExist"/>
			</rule>
		</field>
		
		<field name="student.tel">
			<rule name="match" message="固话规则错误">
				<param name="regex" value="[0-9\-\+\.]*"/>
			</rule>
		</field>
	</group>
	

### 验证器参数

#### `required`：必填
参数：无

#### `min`：最小字符长度
参数：
* (int)`value`: 最小长度值

#### `max`：最大字符长度
参数：
* (int)`value`: 最大长度值

#### `between`：字符长度区域
参数：
* (int)`min`: 最小长度值 
* (int)`max`: 最大长度值

#### `equals`：是否与某个字段值相同
参数：
* (string)`target`: 字段名称

#### `match`：是否符合对应的正则
参数：
* (string)`regex`: 正则表达式

#### `timestampLessEqual`：是否小于等于某个字段的时间
参数：
* (string)`target`: 比较的字段名称
 
重点：
* 对应的字段必须是`java.sql.Timestamp`类型

#### `timestampCreaterEqual`：是否大于等于某个字段的时间
参数：
* (string)`target`: 比较的字段名称
 
重点：
* 对应的字段必须是`java.sql.Timestamp`类型

#### `spring`：通过Spring委托给某个对象处理结果
参数：
* (string)`beanName`: 委托的对象在Spring中的名称
* (string)`methodName`: 执行验证的方法名称，必须返回`true`或`false`

## 简单的例子：`UserSample.java`

	public class UserSample {
		public static void main(String[] args) {
			new UserSample().doValidate();
		}
		
		private User user;
		private String passwordAgain;
		
		//初始化User
		public UserSample() {
			// TODO Auto-generated constructor stub
			user = new User();
			user.setUsername("Jimmy Song");
			user.setPassword("123456");
			this.setPasswordAgain("654321");
			user.setEmail("jimmy.song#aliyun.com");
			long timestamp = Calendar.getInstance().getTimeInMillis();
			Timestamp starttime = new Timestamp(timestamp);
			Timestamp endtime = new Timestamp(timestamp-1000);
			user.setStarttime(starttime);
			user.setEndtime(endtime);
		}
		
		//执行验证
		public void doValidate(){
			...
		}
	
	}
	
### 实现验证：`doValidate`

	public void doValidate(){
		IValidateConfig config = new BasicValidateConfig();
		IValidateService service = new BasicValidateService(config);
		Map<String,String> results = service.validate(this, "group.user");
		
		if(results.isEmpty()) {
			System.out.println("验证通过！");
		} else {
			System.err.println("验证不通过，有验证失败的项目！");

			Set<String> keys = results.keySet();
			Iterator<String> it = keys.iterator();
			while(it.hasNext()) {
				String key = it.next();
				System.out.println(key+"（验证不通过）");
				System.out.println("原因："+results.get(key));
			}
		}
	}
	
**输出结果**

	22:54:08,603  INFO BasicValidateConfig:44 - read validation main file , rules.fo.xml
	22:54:08,679  INFO BasicValidateConfig:77 - read include validation file , user.fo.xml
	验证不通过，有验证失败的项目！
	user.password（验证不通过）
	原因：两次输入密码不一致
	passwordAgain（验证不通过）
	原因：两次输入密码不一致
	user.email（验证不通过）
	原因：邮件格式不正确
	user.starttime（验证不通过）
	原因：开始时间不能大于结束时间
	user.endtime（验证不通过）
	原因：结束时间不能小于开始时间


### 配置文件：`rules.fo.xml`/`user.fo.xml`
**rules.fo.xml**

	<?xml version="1.0" encoding="UTF-8"?>
	<fozone-validation>
		<group name="group.sample">
			<field name="username">
				<rule name="required" message="用户名必须要填写！"/>
			</field>
		</group>
		
		<include file="user.fo.xml"/>
	</fozone-validation>
	
**user.fo.xml**

链接 [user.fo.xml](https://github.com/41zone/validation/blob/master/0.9/demo/src/user.fo.xml)


## 配合Spring使用

## 使用Struts2标签
