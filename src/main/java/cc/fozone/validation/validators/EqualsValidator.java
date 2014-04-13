package cc.fozone.validation.validators;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cc.fozone.validation.IValidator;
import cc.fozone.validation.config.pojo.Rule;

/**
 * 相等匹配
 * @author jimmysong
 *
 */
public class EqualsValidator implements IValidator {
	private static final Logger logger = Logger.getLogger(EqualsValidator.class);
	@SuppressWarnings("rawtypes")
	public boolean execute(Object context,Class type, Object value, Rule rule) {
		// TODO Auto-generated method stub
		if(value == null) return false;
		String toName = rule.getParameter("target");
		if(StringUtils.isBlank(toName)) {
			logger.warn("Equals target parameter missed");
			return false;
		}
		
		Object toValue = null;
		try {
			toValue = PropertyUtils.getProperty(context, toName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("Equals target value missed , "+toName);
		}
		return value.equals(toValue);
	}

}
