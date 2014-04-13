package cc.fozone.validation.validators;

import java.sql.Date;
import java.sql.Timestamp;

import org.apache.commons.lang.ClassUtils;

import cc.fozone.validation.IValidator;
import cc.fozone.validation.config.pojo.Rule;

/**
 * 必填处理
 * @author jimmysong
 *
 */
public class RequiredValidator implements IValidator {

	@SuppressWarnings("rawtypes")
	public boolean execute(Object context,Class type,Object value,Rule rule) {
		// TODO Auto-generated method stub
		if(value == null) return false;
		if(ClassUtils.isAssignable(type, Object[].class)) 
			return value != null && ((Object[])value).length > 0;
		else if (type == String.class)
			return value != null && ((String)value).trim().length() > 0;
		else if (type == Timestamp.class || type == Date.class)
			return value != null;
		return false;
	}

}
