package cc.fozone.validation.validators;

import java.sql.Timestamp;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import cc.fozone.validation.IValidator;
import cc.fozone.validation.config.pojo.Rule;

/**
 * 当前时间需要大于等于
 * @author jimmysong
 *
 */
public class TimestampCreaterEqualValidator implements IValidator {
	private static final Logger logger = Logger.getLogger(TimestampCreaterEqualValidator.class);
	@SuppressWarnings("rawtypes")
	public boolean execute(Object context,Class type, Object value, Rule rule) {
		// TODO Auto-generated method stub
		if(value == null || !(value instanceof Timestamp)) return false;
		String toName = rule.getParameter("target");
		if(StringUtils.isBlank(toName)) {
			logger.warn("Creater And Equal target parameter missed");
			return false;
		}
		
		Object toValue = null;
		try {
			toValue = PropertyUtils.getProperty(context, toName);
			if(!(toValue instanceof Timestamp)) return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("Creater And Equal target value missed , "+toName);
		}
		Timestamp timestamp = (Timestamp) value;
		Timestamp toTimestamp = (Timestamp) toValue;
		return timestamp.getTime() >= toTimestamp.getTime();
	}

}
