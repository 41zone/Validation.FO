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



## 配合Spring使用

## 使用Struts2标签
