package com.wcs.common.util;

import java.text.MessageFormat;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.wcs.base.util.StringUtils;

/**
 * <p>
 * Project: RegexValidator Description:为自定义校验器开发专用标签--自定义校验器类 Copyright (c) 2012
 * Wilmar Consultancy Services All Rights Reserved.
 * 
 * @author <a href="mailto:zhaoqian@wcs-global.com">zhaoqian</a>
 */
@FacesValidator("customValidator")
public class CustomValidator implements Validator {

	private Log logger = LogFactory.getLog(CustomValidator.class);
	
	// 占位符处理,需要传入资源文件中的name值,资源文件的
	private String disposePlaceholder(ResourceBundle rb, String rbName,String placeholderStr) {
		String errorinfo;
		if(StringUtils.isEmpty(placeholderStr)){
			placeholderStr="";
			errorinfo="请核对自定义验证标签中的xxxMark属性是否和资源文件中的占位符对应!";
		}else{
			errorinfo = MessageFormat.format(rb.getString(rbName),placeholderStr.split(","));
		}
		return errorinfo;
	}

	// 构造方法
	public CustomValidator() {
	}

	@Override
	public void validate(FacesContext context, UIComponent component,
			Object toValidate) throws ValidatorException {
		
		Map<String, Object> attrs = component.getAttributes();
		//获取非空方面的参数
		String required=(String) attrs.get("requiredP");
		String requiredMessage=(String) attrs.get("requiredMessageP");
		String requiredMark=(String) attrs.get("requiredMarkP");
		//获取验证最小长度方面的参数
		String minLength=(String) attrs.get("minLengthP");
		String minLengthMessage=(String) attrs.get("minLengthMessageP");
		String minLengthMark=(String) attrs.get("minLengthMarkP");
		//获取验证最大长度方面的参数
		String maxLength=(String) attrs.get("maxLengthP");
		String maxLengthMessage=(String) attrs.get("maxLengthMessageP");
		String maxLengthMark=(String) attrs.get("maxLengthMarkP");
		//获取验证正则表达式方面的参数
		String regex=(String)attrs.get("regexP");
		String regexMark=(String)attrs.get("regexMarkP");
		String regexMessage=(String)attrs.get("regexMessageP");
		
		// 加载messages,获取浏览器的语言.
		ResourceBundle rb = ResourceBundle.getBundle("messages", context.getViewRoot().getLocale());

		// 获取要验证的数值
		String value = toValidate == null ? null : toValidate.toString();

		// 进行非空的验证
		if (required!=null) {
			// 为NULL的情况
			if (value.isEmpty()) {
				String errorMessage = this.disposePlaceholder(rb, "msg_notNull",requiredMark);
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						requiredMessage == null ? errorMessage : requiredMessage,""));
			}
			// 为空格的情况
			if (value.trim().equals("")) {
				String errorMessage = this.disposePlaceholder(rb,"msg_notSpace",requiredMark);
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						requiredMessage == null ? errorMessage : requiredMessage,""));
			}
		}

		// 进行最小长度的验证,定义的数字是最小的长度
		if (minLength !=null) {
			// 如果没有值就不进行验证
			if (value.isEmpty() || value.length() == 0) {
				return;
			}
			if (value.length() < Integer.valueOf(minLength)) {
				String errorMessage = this.disposePlaceholder(rb,"msg_minLength",minLengthMark);
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						minLengthMessage == null ? errorMessage : minLengthMessage,""));
			}

		}

		// 进行最长长度的验证,定义的数字是最大的长度
		if (maxLength!=null) {
			// 如果没有值就不进行验证
			if (value.isEmpty() || value.length() == 0) {
				return;
			}
			if (value.length() > Integer.valueOf(maxLength)) {
				String errorMessage = this.disposePlaceholder(rb,"msg_maxLength",maxLengthMark);
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR,
						maxLengthMessage == null ? errorMessage : maxLengthMessage,""));
			}
		}
		
		//前台传过来的regexAarray中的数据,即可作为参数查询后台的正则.也可以在前台直接输入正则表达式.
		if (regex !=null) {
			// 如果没有值就不进行验证
			if (value.isEmpty() || value.length() == 0) {
				return;
			}
			
			//这里需要处理循环里的一堆数据,regex,mark,message,解析并匹配
			String[] regexArray=regex.split("##");
			int regexNum=regexArray.length;
			String[] regexMarkArray;
			String[] regexMessageArray;
			
			if(regexMark == null){
				//处理不带占位符的情况
				regexMarkArray=new String[regexNum];
				for(int p=0;p<regexNum;p++){
					regexMarkArray[p]="none";
				}
			}else{
				regexMarkArray=regexMark.split("##");
			}
			if(regexMessage ==null){
				//处理不带自定义消息的情况
				regexMessageArray=new String[regexNum];
				for(int p=0;p<regexNum;p++){
					regexMessageArray[p]="none";
				}
			}else{
				regexMessageArray=regexMessage.split("##");
			}
			
			
			// 正则验证
			// 获取资源文件
			ResourceBundle regexrb = ResourceBundle.getBundle("regex");
			// 定义一个String,在正则资源文件regex.properties中查找是否存在对应的正则表达式.
			String backgroundRegex = "";
			
			//for循环读取对应的regex,mark,message.
			for(int i=0;i<regexArray.length;i++){
				//首先确定他们的值,为none的赋值为null.
				if(regexMarkArray[i].equals("none")){
					regexMarkArray[i]=null;
				}
				if(regexMessageArray[i].equals("none")){
					regexMessageArray[i]=null;
				}
				
				// 因ResourceBundle没有判断是否存在的方法,所以用trycatch来扑捉到丢失文件的异常,
				//以此来判断是否有对应的正则.如果没有就捕捉missingResourceException的错误,并直接使用regexArray[i]作为正则.
				try {
					// 如果没有在资源文件中找到所属值,就判定为前台写的是正则表达式,catch捕捉到丢失文件异常,直接抛出验证信息.
					backgroundRegex = regexrb.getString(regexArray[i]);
				} catch (MissingResourceException e) {
					//这里其实判断的是不是参数,而是前台输入正则表达式的情况.!!!!!!!!!!!!!
					// 验证正则,错误就抛出异常,正确就return.
					if (!value.matches(regexArray[i])) {
						String errorMessage = this.disposePlaceholder(rb,"regex_msg_formatError",regexMarkArray[i]);
						throw new ValidatorException(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								regexMessageArray[i] == null ? errorMessage : regexMessageArray[i], ""));
					}
					return;
				} catch (Exception e) {
					logger.error("validate方法异常 regexArray[i]错误", e);
					// 不是丢失文件的异常,就报这个错误.
					throw new ValidatorException(new FacesMessage(
							FacesMessage.SEVERITY_ERROR, null, "系统异常:"
									+ e.getMessage() + "请联系管理员!错误是:" + regexArray[i]+"!"));
				}

				// 下面是正常的处理.即regex资源文件中存在的正则验证.!!!!!!!!!!!!!!!!!
				if (!(value).matches(backgroundRegex)) {
					// 追加的针对此正则的错误信息的String.
					try {
						//看是否可以调到,掉到就运行到最下方,调不到就进入catch语句的missing里.这里是分支.
						@SuppressWarnings("unused")
						String tempStr = rb.getString("regex_msg_" + regexArray[i]);
					} catch (MissingResourceException e) {
						logger.error("validate方法异常", e);
						// 如果捕捉到丢失文件的异常,在此输出前台message或者格式错误.
						//这里是分支是在没有特定的报错格式下的报错信息.
						String errorMessage = this.disposePlaceholder(rb,"regex_msg_formatError",regexMarkArray[i]);
						throw new ValidatorException(new FacesMessage(
								FacesMessage.SEVERITY_ERROR,
								regexMessageArray[i] == null ? errorMessage : regexMessageArray[i], ""));
					} catch (Exception e) {
						logger.error("validate方法异常", e);
						// 其他的异常.
						throw new ValidatorException(new FacesMessage(
								FacesMessage.SEVERITY_ERROR, null, "系统异常:"
										+ e.getMessage() + "请联系管理员!" + regexArray[i]));
					}
					// 抛出异常.
					String errorMessage = this.disposePlaceholder(rb, "regex_msg_"+ regexArray[i],regexMarkArray[i]);
					throw new ValidatorException(new FacesMessage(
							FacesMessage.SEVERITY_ERROR,
							regexMessageArray[i] == null ? errorMessage : regexMessageArray[i], "")

					);
				}
			}//for end
		}// if end

	}
	
}
