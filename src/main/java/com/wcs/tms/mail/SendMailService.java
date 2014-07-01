package com.wcs.tms.mail;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.wcs.base.util.ArrayUtil;

/**
 * 
 * <p>Project: tms</p>
 * <p>Description: 邮件工具类</p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chenlong@wcs-global.com">Chen Long</a>
 */
@Stateless
public class SendMailService implements Serializable {
    private Log log = LogFactory.getLog(SendMailService.class);
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /** 队列名称*/
    private static final String QUEUE_PHYSICAL_NAME = "unsQueue";
    /** 工厂JNDI*/
    private static final String cfJNDI = "java:comp/env/jms/ConnectionFactory";
    private Queue queue = null;
    // 部署在生产环境中时请将，下面的JNDI释放，Queue已配置在资源适配器中。
    private static final String queueJNDI = "java:comp/env/jms/UNSINBOX";
    private ConnectionFactory connectionFactory;
    

    public SendMailService() {
        InitialContext ic;
        try {
            ic = new InitialContext();
            connectionFactory = (ConnectionFactory) ic.lookup(cfJNDI);
            // 部署在生产环境中时请将，下面的JNDI释放，Queue已配置在资源适配器中。
            // this.queue = (Queue) ic.lookup(queueJNDI);
        } catch (NamingException e) {
            log.error("SendMailService初始化异常", e);
        }

    }

    /**
     * 
     * <p>Description: 发送邮件</p>
     * @param msg
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void send(List<Mail> maillist) {
    	if(ArrayUtil.isEmpty(maillist)){
    		return;
    	}    	
        Connection connection = null;
        MessageProducer producer = null;
        try {
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // 部署在glassfish上时，可以直接创建消息队列。在WAS上部署时，由于在初始化构造函数中已经创建了消息队列，所以请将下面的这行代码注释掉。
            queue = session.createQueue(QUEUE_PHYSICAL_NAME);
            producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage();
            String email = generationJson(maillist);
            log.info(">>> emailJson:"+email);
            message.setText(email);
            producer.send(message);
        } catch (JMSException e) {
            log.error(">>> JMSException occured", e);
            return;
        } finally {
            if (producer != null) {
                try {
                    producer.close();
                } catch (JMSException e) {
                    log.error(">>> JMSException occured", e);
                    return;
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    log.error(">>> JMSException occured", e);
                    return;
                }
            }
        }
    }

    /**
     * 
     * <p>Description: 生成单独发送邮件的JSON字符消息</p>
     * @param mail
     * @return
     */
    private String generationJson(Mail mail) {
        String email = "[";
        if (mail != null) {
            email = JSON.toJSONString(mail);
        }
        return email.concat("]");
    }

    /**
     * 
     * <p>Description: 得到多条邮件JSON字符消息</p>
     * @param mailList
     * @return
     */
    private String generationJson(List<Mail> mailList) {
        String email = "[";
        if (mailList != null && !mailList.isEmpty()) {
            int size = mailList.size();
            /** email、telno和pernr均支持多条，以|隔开*/
            String em = "";
            String tel = "";
            String per = "";
            /** 不支持多条*/
            String subject = mailList.get(0).getSubject();
            String aux = mailList.get(0).getAux();
            String body = mailList.get(0).getBody();
            for (int i = 0; i < size; i++) {
                if (!"".equals(mailList.get(i).getEmail())) {
                    em = em.concat(mailList.get(i).getEmail());
                    if (i != size - 1) {
                        em = em.concat("|");
                    }
                }
                if (!"".equals(mailList.get(i).getTelno())) {
                    tel = tel.concat(mailList.get(i).getTelno());
                    if (i != size - 1) {
                        tel = tel.concat("|");
                    }
                }

                if (!"".equals(mailList.get(i).getPernr())) {
                    per += mailList.get(i).getPernr();
                    if (i != size - 1) {
                        per = per.concat("|");
                    }

                }
            }
            Mail m = new Mail();
            m.setSubject(subject);
            m.setAux(aux);
            m.setEmail(em);
            m.setPernr(per);
            m.setTelno(tel);
            m.setBody(body);
            email = email + JSON.toJSONString(m);
        }
        return email.concat("]");
    }
}
