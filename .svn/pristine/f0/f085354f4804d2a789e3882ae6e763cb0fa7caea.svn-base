package com.wcs.tms.conf;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PostConstructApplicationEvent;
import javax.faces.event.PreDestroyApplicationEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.MessageConveyor;
 
public class ApplicationListener implements SystemEventListener { 
 
  public boolean isListenerForSource(Object app) { 
    boolean result = false; 
    result = (app instanceof Application); 
    return result; 
  } 
 
  public void processEvent(SystemEvent event) 
      throws AbortProcessingException { 
		Map<String, Object> appMap = FacesContext.getCurrentInstance()
				.getExternalContext().getApplicationMap();
    if (event instanceof PostConstructApplicationEvent) { 
    	for (SysConfig sc : SysConfig.values()){
			if (sc.toString().startsWith("SYS")){
				appMap.put(sc.toString(), val(sc));
			}
		}
    } else if (event instanceof PreDestroyApplicationEvent) { 
    	for (SysConfig sc : SysConfig.values()){
			if (sc.toString().startsWith("SYS")){
				appMap.remove(sc.toString());
			}
		}
    } 
  }
  
  private String val(SysConfig jco) {
		IMessageConveyor mc = new MessageConveyor(Locale.CHINA);
		return mc.getMessage(jco);
	}
 
}
