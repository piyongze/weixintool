package com.pyz.tool.weixintool.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.PropertyFilter;


/**
 * 某些情况下不需要大量的bean类，因而转换过程可以如下：
 * 普通bean类，可以不必定义类，通过map的键值对 存放属性和值，则转换结果和普通bean类转换结果相同。
 * List<Bean>则通过List<Map>对应存放，进行转换
 * 
 * 注，但是bean类对应不同的类属性，比如时间或者日期，慎重使用。
 * 如果key的类型是string、list或者bean类则均可正常转换
 * 如果key类型为日期或者其他特殊类型，则可能出现异常的转换
 * @author Yongze.Pi
 *
 */
public class JsonUtil {

    private static final int JSON_ARRAY=0;
    private static final int JSON_OBJECT=1;
    private static final int JSON_OTHER=2;
    
    public static String obj2Json(Object object){
        
        //配置控制不参与序列化
        JsonConfig config = new JsonConfig();  
        config.setJsonPropertyFilter(new PropertyFilter()  
        {  
            public boolean apply(Object source, String name, Object value)  
            {  
                return value == null || value == "" || (value instanceof List)?((List)value).size()==0:false;  
            }  
        });
        
        //如果是list 转换为jsonArray  否则转换为jsonObject
        if(object instanceof List){
            return JSONArray.fromObject(object,config).toString();
        }else{
            return JSONObject.fromObject(object,config).toString();
        }
    }
    
    public static Map<String,Object> jsonObjToMap(String str){
        
        //配置控制不参与序列化
        Map<String,Object> map=new HashMap<String,Object>();
        JsonConfig config = new JsonConfig();  
        config.setJsonPropertyFilter(new PropertyFilter()  
        {  
            public boolean apply(Object source, String name, Object value)  
            {  
                return value == null;  
            }  
        });
        JSONObject jsonObj = JSONObject.fromObject(str,config);
        Iterator iterator=jsonObj.keys();
        while(iterator.hasNext()){
            String key=(String) iterator.next();
            String value=jsonObj.getString(key);
            if(getJSONType(value)==2){
                map.put(key, value);
            }else if(getJSONType(value)==1){
                JSONArray array=JSONArray.fromObject(value);
                List<Object> innerMap=new ArrayList<Object>();
                for(int i=0;i<array.size();i++){  
                    JSONObject jsonobject = array.getJSONObject(i);
                    innerMap.add(jsonObjToMap(jsonobject.toString()));
                    map.put(key,innerMap);
                }  
            }else{
                map.put(key, jsonObjToMap(value));
            }
        }
        return map;
    }
    
    public static int getJSONType(String str){
        if(str.charAt(0)=='{'){
            return 0;
        }else if(str.charAt(0)=='['){
            return 1;
        }else{
            return 2;
        }
    }
}
