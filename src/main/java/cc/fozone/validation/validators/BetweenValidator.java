package cc.fozone.validation.validators;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cc.fozone.validation.IValidator;
import cc.fozone.validation.config.pojo.Rule;

/**
 * 字符长度区间验证器
 * @author jimmysong
 *
 */
public class BetweenValidator implements IValidator {
	private static final Logger logger = Logger.getLogger(BetweenValidator.class);
	@SuppressWarnings("rawtypes")
	public boolean execute(Object context,Class type, Object value, Rule rule) {
		// TODO Auto-generated method stub
		if(value == null) return false;
		int length = 0;
		if(ClassUtils.isAssignable(type, Object[].class)) {
			length = ((Object[])value).length;
		} else if(type == String.class) {
			length = ((String)value).trim().length();
		}
		String minString = rule.getParameter("min");
		int min = StringUtils.isNumeric(minString)?Integer.parseInt(minString):-1;
		String maxString = rule.getParameter("max");
		int max = StringUtils.isNumeric(maxString)?Integer.parseInt(maxString):-1;
		if(min == -1 || max == -1) {
			logger.warn("Invalid Parameter for minimun or maximun.");
			return false;
		}
		return length >= min && length <= max;
	}

}
