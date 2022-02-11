package com.brotherhood.o2o.util;

import android.content.Context;
import android.text.TextUtils;
import com.brotherhood.o2o.R;

public class InputVerifyUtil {

	private static final String telRegex = "[1][34578]\\d{9}";
	
	private static String emailStr = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";

	public static String getMobileNumber(Context context){
		String phoneNumber = DeviceUtil.getMobileNumber(context);
		if (!TextUtils.isEmpty(phoneNumber)) {
			phoneNumber = phoneNumber.replaceAll("\\+86", "");
		}
		return phoneNumber;
	}

	/**
	 * 判断手机号码值
	 * @param phoneNumber
	 * @return
	 */
	public static boolean verifyPhoneNumber(Context context, String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber)) {
			DisplayUtil.showToast(context, context.getString(R.string.phone_no_is_empty));
			return false;
		}
		if (!phoneNumber.matches(telRegex)) {
			DisplayUtil.showToast(context, context.getString(R.string.phone_no_format_error));
			return false;
		}
		return true;
	}

	/**
	 * 绑定邮箱值验证
	 * @param email
	 * @return
	 */
	public static boolean verifyEmail(Context context, String email){
		if(TextUtils.isEmpty(email)){
			DisplayUtil.showToast(context, context.getString(R.string.email_is_empty));
			return false;
		}
		if (!email.matches(emailStr)) {
			DisplayUtil.showToast(context, context.getString(R.string.email_format_error));
			return false;
		}
		return true;
	}
}
