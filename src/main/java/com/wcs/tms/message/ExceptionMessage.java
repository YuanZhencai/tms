package com.wcs.tms.message;


import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: 11-8-11
 * Time: 下午1:51
 * To change this template use File | Settings | File Templates.
 */
@BaseName("exception")
@LocaleData({
   @Locale("zh_CN")
})
public enum ExceptionMessage {
    @MessageId("E0001") FN_CONNECT,
    @MessageId("E0002") FN_NO_CLASS,
    @MessageId("E0003") INSTANCE_CREATE,
    @MessageId("E0004") PREGITCAPTIAL_SAVE,
    @MessageId("E0005") PROCESS_DETAIL,
    @MessageId("E0006") PROCESS_LOCK,
    @MessageId("E0007") PROCESS_MAP,
    
}
