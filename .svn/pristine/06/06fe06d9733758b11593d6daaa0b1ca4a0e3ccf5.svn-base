/** * SysLogVo.java 
* Created on 2013-8-15 下午2:49:08 
*/

package com.wcs.sys.ejbtimer.vo;

import com.wcs.sys.ejbtimer.model.SysJobInfo;
import com.wcs.sys.ejbtimer.model.SysJobLog;

/** 
 * <p>Project: wcsoa</p> 
 * <p>Title: SysLogVo.java</p> 
 * <p>Description: </p> 
 * <p>Copyright (c) 2013 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:lujiawei@wcs-global.com">Lu jiawei</a>
 */

public class SysJobLogVo {

	private SysJobInfo sysJobInfo;

	private SysJobLog sysJobLog;

	private String detail;

	public SysJobLogVo() {
		sysJobInfo = new SysJobInfo();
		sysJobLog = new SysJobLog();
	}

	public SysJobLogVo(Object obj1, Object obj2) {
		if (obj1 != null && obj1 instanceof SysJobLog) {
			this.sysJobLog = (SysJobLog) obj1;
		} else {
			sysJobLog = new SysJobLog();
		}
		if (obj2 != null && obj2 instanceof SysJobInfo) {
			this.sysJobInfo = (SysJobInfo) obj2;
		} else {
			sysJobInfo = new SysJobInfo();
		}

	}

	public SysJobInfo getSysJobInfo() {
		return sysJobInfo;
	}

	public void setSysJobInfo(SysJobInfo sysJobInfo) {
		this.sysJobInfo = sysJobInfo;
	}

	public SysJobLog getSysJobLog() {
		return sysJobLog;
	}

	public void setSysJobLog(SysJobLog sysJobLog) {
		this.sysJobLog = sysJobLog;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
}
