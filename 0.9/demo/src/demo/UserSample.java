package demo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cc.fozone.validation.BasicValidateService;
import cc.fozone.validation.IValidateService;
import cc.fozone.validation.config.BasicValidateConfig;
import cc.fozone.validation.config.IValidateConfig;

public class UserSample {
	public static void main(String[] args) {
		new UserSample().doValidate();
	}
	
	private User user;
	private String passwordAgain;
	
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getPasswordAgain() {
		return passwordAgain;
	}

	public void setPasswordAgain(String passwordAgain) {
		this.passwordAgain = passwordAgain;
	}
}
