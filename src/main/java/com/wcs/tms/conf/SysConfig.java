package com.wcs.tms.conf;

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

@BaseName("config.sys_conf")
@LocaleData({
	   @Locale("zh_CN")
	})
public enum SysConfig {
	//系统名称
	SYS_NAME,
	//版本名称
	SYS_VERSION_NAME,
	//版本号
	SYS_VERSION,
	//皮肤
	SYS_SKIN,
}
