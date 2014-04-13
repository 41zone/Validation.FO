package cc.fozone.validation.config;
 
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cc.fozone.validation.IValidator;
import cc.fozone.validation.config.pojo.Validator;

/**
 * 基础验证配置
 * @author jimmysong
 *
 */
public class SpringValidateConfig extends BasicValidateConfig implements ApplicationContextAware {
	@SuppressWarnings("rawtypes")
	@Override
	public IValidator instanceValidator(Validator v)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		// TODO Auto-generated method stub
		String springName = v.getSpringName();
		String className = v.getClassName();
		if(StringUtils.isBlank(springName)) return super.instanceValidator(v);
		Class clazz = Class.forName(className);
		IValidator validator = (IValidator) this.getApplicationContext().getBean(springName,clazz);
		return validator;
	}
	private ApplicationContext applicationContext;
	
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		// TODO Auto-generated method stub
		this.applicationContext = applicationContext;
	}
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
}
