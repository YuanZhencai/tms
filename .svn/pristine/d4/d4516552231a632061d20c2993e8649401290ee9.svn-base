/**
 * DictService.java
 * Created: 2012-2-16 下午01:37:43
 */
package com.wcs.common.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import com.wcs.common.model.Dict;

/**
 * <p>Project: tih</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2012 Wilmar Consultancy Services</p>
 * <p>All Rights Reserved.</p>
 * @author <a href="mailto:chengchao@wcs-global.com">ChengChao</a>
 * 注解Starup,启动程序就立即加载.
 */

@Startup
@Singleton
public class CommonService {
	
	private Log log = LogFactory.getLog(CommonService.class);
	
	//查询所有语言类 loadDict()使用
	private List<String> langList; 
	
	//语言分类的list MAP集合,开始程序就就获取到. loadDict()得到
	private Map<String, List<Dict>> langKeyDictMap=new  HashMap<String, List<Dict>>();

	//数据库中所有的数据,根据JPQL查出,总的数据
	private List<Dict> dicts=new ArrayList<Dict>();
	
	//专门用于查询getValueByDictCatKey的Value值的map集合.
	private Map<String,String> getValueMap=new HashMap<String, String>();
	
	@PersistenceContext(unitName = "pu") 
	private EntityManager em;
	
	/**
	 * <p>Description: 系统加载时查询DICT表，将所有defunct_ind!='Y'的记录放到map,key=CODE_CAT+"."+CODE_KEY,value=CODE+VAL并放到application级别的bean中。</p>
	 * @return
	 */
	@PostConstruct
	public void loadDict() {
		this.queryDict();
	}
	
	/**
	 * <p>Description: 一系列的取值,刷新时以及程序刚运行时调用这个方法</p>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void queryDict(){
		//查询出数据库中一共有几种语言
		String langSQL="SELECT distinct lang FROM Dict";
		langList =em.createNativeQuery(langSQL).getResultList();
		
		//循环查询出每种语言的所有数据,然后装如一个MAP集合中去,以语言类型为key,value为所有的数据是一个list集合.
		for(int i=0;i<langList.size();i++){
			String strJPQL="SELECT d FROM Dict d where d.defunctInd <> 'Y' and d.lang = '"+langList.get(i)+"' order by d.lang,d.codeCat,d.seqNo";
			//用原生SQL查询的是List<Object>类型.而JPQL是List<Dict>类型.
			List<Dict> result=em.createQuery(strJPQL).getResultList();
			langKeyDictMap.put( langList.get(i).toString(),result);
		}
		
		String allJQPL="SELECT d FROM Dict d";
		List<Dict> allResult=em.createQuery(allJQPL).getResultList();
		for(int l=0;l<allResult.size();l++){
			//查询出所有的数据,以cat_point_key_lang为key,Value为value.Map形式存储,在取值时效率高.
			String keyData=(allResult.get(l).getCodeCat().toString()+"."+allResult.get(l).getCodeKey().toString()+"."+allResult.get(l).getLang().toString()).replace(".", "_");
			getValueMap.put(keyData, allResult.get(l).getCodeVal().toString());
		}
		
		log.info("重新加载字典成功！");
	}
	
	/**
	 * <p>Description: 从application级别的bean获取该值，不要直接从数据库获取
	 * 这里是以d.getCodeCat()+"."+d.getCodeKey()来dictMap中获取相应的value值.
	 * </p>
	 * @param catKey
	 * @return
	 */
	public String getValueByDictCatKey(String cat_point_key_lang) {
		return getValueMap.get(cat_point_key_lang);
	}

	/**
	 * <p>Description: 根据cat值获得所有的Dict列表</p>
	 * @param codeCat
	 * @return
	 */
	public List<Dict> getDictByCat(String cat,String lang) {
		//根据浏览器语言环境选取langKeyMap中的 List<Dict>集合.
		dicts=langKeyDictMap.get(lang);
		List<Dict> listByCat=new ArrayList<Dict>();
		for( int i=0;i<dicts.size();i++){
			if(dicts.get(i).getCodeCat().equals(cat)){
				listByCat.add(dicts.get(i));
			}
		}
		//返回结果.
		return listByCat;
	}
}
