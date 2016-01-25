package com.pyz.tool.weixintool.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


public class WeixinUtil {
  //静态常量   TOKEN_URL
    private static final String TOKNE_URL="https://api.weixin.qq.com/cgi-bin/token?"
                + "grant_type=client_credential&appid=APPID"
                + "&secret=SECRET";
    //静态常量    API_TICKET_URL
    private static final String API_TICKET_URL="https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
    //查询openId列表
    private static final String OPENID_LIST_URL="https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN";
    //查询openId列表
    private static final String OPENID_LIST_LONG_URL="https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID";
    //获取用户信息
    private static final String USER_INFO_URL="https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    //保存下一个openId
    public static String nextOpenId;
    
    //appID
    private static String appId;
    //secret
    private static String secret;
    
    private static Logger logger=Logger.getLogger(WeixinUtil.class);
    
    public WeixinUtil(String appId,String secret){
        this.appId=appId;
        this.secret=secret;
    }
    
    public static void main(String[] args) {
        WeixinUtil weixinUtil=new WeixinUtil("wx9917a2954fbe3dfb","2b738434cf1449ecb5429545b5e7cf5a");
        System.out.println(weixinUtil.getJSApiTicket(weixinUtil.getToken()));
        System.out.println(weixinUtil.searchMenu(weixinUtil.getToken()));
        System.out.println(weixinUtil.getSignature("http://www.melvon.cn/tfbtest/index.html","19910125","19910125"));
    }
    /**
     * 获取token 存储到文件  判断时间 定时刷新
     * @return
     */
    public String getToken(){
        String result="";
        //读取文件
        String path=getClass().getResource("/").getPath()  + java.io.File.separator + "session.txt";
        logger.debug("path="+path);
        LockFile file=new LockFile(path);
        file.getLock().writeLock().lock();
        
        String url=TOKNE_URL.replaceAll("APPID",appId).replaceAll("SECRET",secret);
        try{
            if(!file.exists()){
                file.createNewFile();
                String jsonParam=HttpTool.request(url);
                Map<String,Object> map = JsonUtil.jsonObjToMap(jsonParam);
                result=(String) map.get("access_token");
                OutputStreamWriter write = new OutputStreamWriter(
                        new FileOutputStream(file), "UTF-8");
                BufferedWriter writer = new BufferedWriter(write);
                writer.write(result);
                writer.newLine();
                writer.write(System.currentTimeMillis()+"");
                writer.flush();
                writer.close();
                write.close();
            }else{
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), "UTF-8");
                BufferedReader reader = new BufferedReader(read);
                result=reader.readLine();
                long time=Long.parseLong(reader.readLine());
                if(System.currentTimeMillis()-time>6000000){
                    //重新获取token 并写入token和时间 返回新token
                    String jsonParam=HttpTool.request(url);
                    Map<String,Object> map = JsonUtil.jsonObjToMap(jsonParam);
                    result=(String) map.get("access_token");
                    OutputStreamWriter write = new OutputStreamWriter(
                            new FileOutputStream(file), "UTF-8");
                    BufferedWriter writer = new BufferedWriter(write);
                    writer.write(result);
                    writer.newLine();
                    writer.write(System.currentTimeMillis()+"");
                    writer.close();
                    write.close();
                    reader.close();
                    read.close();
                }
            }
            file.getLock().writeLock().unlock();
        }catch(Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取jsapi
     * 处理方式和token相同
     * @param token
     * @return
     */
    public String getJSApiTicket(String token){
        String resu="";
        //读取文件
        String path=getClass().getResource("/").getPath()  + java.io.File.separator + "jsapi.txt";
        logger.debug("path="+path);
        LockFile file=new LockFile(path);
        file.getLock().writeLock().lock();
        String url=API_TICKET_URL.replaceAll("ACCESS_TOKEN", token);
        try{
            if(!file.exists()){
                file.createNewFile();
                String jsonParam=HttpTool.request(url);
                Map<String,Object> map = JsonUtil.jsonObjToMap(jsonParam);
                resu=(String) map.get("ticket");
                OutputStreamWriter write = new OutputStreamWriter(
                        new FileOutputStream(file), "UTF-8");
                BufferedWriter writer = new BufferedWriter(write);
                writer.write(resu);
                writer.newLine();
                writer.write(System.currentTimeMillis()+"");
                writer.flush();
                writer.close();
                write.close();
            }else{
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), "UTF-8");
                BufferedReader reader = new BufferedReader(read);
                resu=reader.readLine();
                long time=Long.parseLong(reader.readLine());
                if(System.currentTimeMillis()-time>6000000){
                    //重新获取token 并写入token和时间 返回新token
                    String jsonParam=HttpTool.request(url);
                    Map<String,Object> map = JsonUtil.jsonObjToMap(jsonParam);
                    resu=(String) map.get("ticket");
                    OutputStreamWriter write = new OutputStreamWriter(
                            new FileOutputStream(file), "UTF-8");
                    BufferedWriter writer = new BufferedWriter(write);
                    writer.write(resu);
                    writer.newLine();
                    writer.write(System.currentTimeMillis()+"");
                    writer.close();
                    write.close();
                    reader.close();
                    read.close();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.debug(e.getMessage());
        }
        return resu;
    }
    
    /**
     * 生成签名
     * @param url
     * @param timestamp
     * @param noncestr
     * @return
     */
    public String getSignature(String url,String timestamp,String noncestr){
        logger.debug("url="+url);
        logger.debug("timestamp="+timestamp);
        logger.debug("noncestr="+noncestr);
        String resu="";
        String str="jsapi_ticket="+getJSApiTicket(getToken())
                + "&noncestr="+noncestr
                + "&timestamp="+timestamp
                + "&url="+url;
        try {
            resu=WeixinUtil.sha1(new String[]{str});
            logger.debug("signature:"+resu);
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resu;
    }
    
    /**
     * 验证请求是否合法
     * @param token
     * @param timestamp
     * @param nonce
     * @param signature
     * @return
     */
    public boolean check(String token,String timestamp,String nonce,String signature){
        boolean result=false;
        try {
            String mysingature=WeixinUtil.sha1(new String[]{token,timestamp,nonce});
            if(mysingature.equals(signature)){
                result=true;
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 创建菜单
     * @param token
     */
    public String createMenu(String token){
        String result="";
        String url="https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+token;
        String content="{\"button\":[{\"name\":\"常用 \",\"sub_button\":[{\"type\":\"view\",\"name\":\"优酷\",\"url\":\"http://www.youku.com\"}"
                + ",{\"type\":\"view\",\"name\":\"百度\",\"url\":\"http://www.baidu.com\"}"
                + ",{\"type\":\"view\",\"name\":\"qq\",\"url\":\"http://www.qq.com\"}]}"
                + ",{\"type\":\"scancode_push\",\"name\":\"扫码\",\"key\":\"rselfmenu_0_1\"}"
                + ",{\"type\":\"click\",\"name\":\"抽奖\",\"key\":\"awardevent\"}]}";
        //String content=getBtnJson();
        try {
            result=HttpTool.postRequest(url,content);
            logger.debug("已提交添加菜单请求");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 查看菜单
     * @param token
     * @return
     */
    public String searchMenu(String token){
        String result="";
        String url="https://api.weixin.qq.com/cgi-bin/menu/get?access_token="+token;
        try {
            result=HttpTool.request(url);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    
    
    
    
    public String webAuth(){
        String resu="";
        
        return resu;
    }
    
    /**
     * 对数组 排序 并sha1求解
     * @param strs
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String sha1(String[] strs) throws NoSuchAlgorithmException{
        sort(strs);
        String str=unionStrs(strs);
        return sha1(str);
    }
    /**
     * 排序数组
     * @param strs
     */
    public static void sort(String[] strs){
        String temp;
        for(int i=0;i<strs.length-1;i++){
            for(int j=i+1;j<strs.length;j++){
                if(strs[i].compareTo(strs[j])>0){
                    temp=strs[i];
                    strs[i]=strs[j];
                    strs[j]=temp;
                }
            }
        }
    }
    
    /**
     * 合并数组为字符串
     * @param strs
     * @return
     */
    public static String unionStrs(String[] strs){
        String result="";
        for(int i=0;i<strs.length;i++){
            result+=strs[i];
        }
        return result;
    }
    
    /**
     * sha1求解
     * @param data
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String sha1(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(data.getBytes());
        StringBuffer buf = new StringBuffer();
        byte[] bits = md.digest();
        for(int i=0;i<bits.length;i++){
            int a = bits[i];
            if(a<0){
                a+=256;
            }
            if(a<16) buf.append("0");
                buf.append(Integer.toHexString(a));
            }
        return buf.toString();
     }  
}
