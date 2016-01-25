/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package com.pyz.tool.weixintool.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * 进行get或post请求的辅助类
 * @author PiYongze
 *
 */
public class HttpTool {

	/**
	 * get 方法请求 url 并获取返回值
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String request(String url) throws Exception{
		String result="";
		HttpClient httpClient = new DefaultHttpClient(); 
        try {  
            HttpGet httpget = new HttpGet(url);  
            HttpResponse httpresponse = httpClient.execute(httpget); 
            HttpEntity entity = httpresponse.getEntity();
            result = EntityUtils.toString(entity,"UTF-8"); 
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        return result;
	}
	
	/*
	 * post请求
	 * 传递内容为json
	 */
	public static String postRequest(String url,String content) throws Exception{
		String result="";
		HttpClient httpClient = new DefaultHttpClient(); 
        try {  
        	HttpPost httppost = new HttpPost(url);
            StringEntity myEntity = new StringEntity(content,"utf-8"); 
            myEntity.setContentEncoding("utf-8");
            myEntity.setContentType("application/json");
            httppost.setEntity(myEntity); 
            HttpResponse httpresponse=httpClient.execute(httppost);
            HttpEntity entity = httpresponse.getEntity();  
            result = EntityUtils.toString(entity); 
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        return result;
		
	}
	
	
    public static String getTokenJson()throws Exception {
    	 String body = null;  
    	 HttpClient httpClient = new DefaultHttpClient(); 
         try {  
             // Get����  
        	 String url="https://api.weixin.qq.com/cgi-bin/token?"
        	 		+ "grant_type=client_credential&appid=wxffb214fd3c3e"
        	 		+ "0198&secret=3b2cf8d774450d1d48692b7622d5774d";
             HttpGet httpget = new HttpGet(url);  
             HttpResponse httpresponse = httpClient.execute(httpget);  
             HttpEntity entity = httpresponse.getEntity();  
             body = EntityUtils.toString(entity);  
         } catch (ParseException e) {  
             e.printStackTrace();  
         } catch (UnsupportedEncodingException e) {  
             e.printStackTrace();  
         } catch (IOException e) {  
             e.printStackTrace();  
         }
         return body;
    }
    
    public static String getApiTicket(String token)throws Exception {
   	 String body = null;  
   	 HttpClient httpClient = new DefaultHttpClient(); 
        try {  
            // Get����  
       	 String url="https://api.weixin.qq.com/cgi-bin/ticket/getticket?"
       	 		+ "access_token="+token+"&type=jsapi";
       	 System.out.println("url="+url);
            HttpGet httpget = new HttpGet(url);  
            HttpResponse httpresponse = httpClient.execute(httpget);  
            HttpEntity entity = httpresponse.getEntity();  
            body = EntityUtils.toString(entity);  
        } catch (ParseException e) {  
            e.printStackTrace();  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        return body;
   }
    
    
    
    public int getRandom(){
    	return (int)Math.random();
    }

}
