package com.wcs.common.controller;

import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;

import com.google.common.collect.Lists;
import com.wcs.common.model.Dict;
import com.wcs.common.service.CommonService;
import com.wcs.tms.util.MessageUtils;

@ManagedBean(name="commonBean")
@ApplicationScoped
public class CommonBean {
	@EJB 
	private CommonService commonService;
	
	//构造方法
	//这个托管bean主要是对字典表(DICT)的读取
	public CommonBean(){
	}
	
	//这里是以CodeCat()+"."+CodeKey()来获取相应的value值.
	//请参照数据库的字段.通过这种方法获取Value.
	public String getValueByDictCatKey(String cat_point_key){
		Locale browserLang=FacesContext.getCurrentInstance().getViewRoot().getLocale();
		//这里的cat_point_key的值是DictConsts下的常量名称.请注意.而非数据库的值.
		//下面重新组合参数.
		String cat_point_key_lang=cat_point_key+"_"+browserLang.toString();
		return commonService.getValueByDictCatKey(cat_point_key_lang);
	}
	
	// 这里是以CodeCat(), CodeKey()来获取相应的value值.
    // 请参照数据库的字段.通过这种方法获取Value.
    public String getValueByDictCatAndKey(String cat,String keyValue) {
        String cat_point_key = cat + "_" + keyValue;
        Locale browserLang = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        // 这里的cat_point_key的值是DictConsts下的常量名称.请注意.而非数据库的值.
        // 下面重新组合参数.
        String cat_point_key_lang = cat_point_key + "_" + browserLang.toString();
        return commonService.getValueByDictCatKey(cat_point_key_lang);
    }
	
	//这是通过一个数据库的Code_Cat字段来获取对应的list集合,常用于下拉框.
	public List<Dict> getDictByCat(String cat) {
		//加入国际化之后直接带入获取的语言环境
		return commonService.getDictByCat(cat,"zh_CN");
	}
	
	/**
     * 
     * <p>Description: 得到具体的类型下拉</p>
     * @param code
     * @return
     */
    public List<SelectItem> getDictByCode(String code) {
        List<SelectItem> items = Lists.newArrayList();
        List<Dict> dicts = commonService.getDictByCat(code,"zh_CN");
        for (Dict d : dicts) {
            items.add(new SelectItem(d.getCodeKey(), d.getCodeVal()));
        }
        return items;
    }
	
	//刷新调用的方法
	public void refreshDictData(){
		commonService.queryDict();
		MessageUtils.addSuccessMessage("messages", "刷新字典成功!");
	}
	
}
