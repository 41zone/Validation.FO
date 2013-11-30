package demo;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cc.fozone.validation.BasicValidateService;
import cc.fozone.validation.IValidateService;
import cc.fozone.validation.config.BasicValidateConfig;
import cc.fozone.validation.config.IValidateConfig;

public class ValidateSample {
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
	
	/**
	 * 执行验证
	 */
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
	
	public static void main(String[] args) {
		new ValidateSample().doValidate();
	}
}
