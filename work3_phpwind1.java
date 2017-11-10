package com.Httpclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class work3_phpwind1 {

	public static void main(String[] args) throws IOException {
		String url = "http://localhost/phpwind/index.php?m=u&c=login&a=logout";
		String content = null;
		String token = null;
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		HttpGet httpget = null;
		HttpPost httppost = null;
		HttpEntity entity = null;
		Pattern p = null;
		Matcher m = null;
		//会话保持
		HttpContext context1 = new BasicHttpContext();
		//设置代理
		HttpHost proxy = new HttpHost("127.0.0.1",8888, "http");
		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
		
		//1、退出登录
		httpclient = HttpClients.createDefault();
		httpget = new HttpGet(url);
		httpget.setConfig(config);
		response = httpclient.execute(httpget, context1);
		entity = response.getEntity();
		content = EntityUtils.toString(entity, "utf-8");
		httpclient.close();
		
		//2、打开phpwind页面抓取csrf_token
		httpclient = HttpClients.createDefault();
		url = "http://localhost/phpwind/index.php";
		httpget = new HttpGet(url);
		httpget.setConfig(config);//代理
		response = httpclient.execute(httpget, context1);
		entity = response.getEntity();
		content = EntityUtils.toString(entity, "utf-8");
		p = Pattern.compile("name=\"csrf_token\" value=\"(.+?)\"/>");
		m = p.matcher(content);
		while(m.find()){
			System.out.println("csrf_token："+m.group(1));
			token = m.group(1);
		}
		httpclient.close();
		
		//3、登录
		httpclient = HttpClients.createDefault();
		url = "http://localhost/phpwind/index.php?m=u&c=login&a=dologin";
		httppost = new HttpPost(url);
		httppost.setConfig(config);
		List<NameValuePair> values1 = new ArrayList<NameValuePair>();
		values1.add(new BasicNameValuePair("csrf_token", token));
		values1.add(new BasicNameValuePair("username", "zhangsan"));
		values1.add(new BasicNameValuePair("password", "123456"));
		httppost.setEntity(new UrlEncodedFormEntity(values1, "utf-8"));
		response = httpclient.execute(httppost, context1);
		entity = response.getEntity();
		content = EntityUtils.toString(entity, "utf-8");
		p = Pattern.compile("statu=(.+?)\"");
		m = p.matcher(content);
		String statu = null;
		while(m.find()){
			System.out.println("statu:"+m.group(1));
			statu = m.group(1);
		}
		httpclient.close();
		
		//3、显示登录成功
		httpclient = HttpClients.createDefault();
		url = "http://localhost/phpwind/index.php?m=u&c=login&a=welcome&_statu="+statu;
		httpget = new HttpGet(url);
		httpget.setConfig(config);
		httpclient.execute(httpget, context1);
		httpclient.close();
		
		//4、点击发帖第一步
		httpclient = HttpClients.createDefault();
		url = "http://localhost/phpwind/index.php?c=forum&a=list";
		httppost = new HttpPost(url);
		httppost.setConfig(config);
		List<NameValuePair> values2 = new ArrayList<NameValuePair>();
		values2.add(new BasicNameValuePair("csrf_token", token));
		httppost.setEntity(new UrlEncodedFormEntity(values2, "utf-8"));
		httpclient.execute(httppost, context1);
		httpclient.close();
		
		//5、发帖第二步
		httpclient = HttpClients.createDefault();
		url = "http://localhost/phpwind/index.php?c=post&fid=2";
		httpget = new HttpGet(url);
		httpget.setConfig(config);
		httpclient.execute(httpget, context1);
		httpclient.close();
		
		//6、输入标题和内容
		httpclient = HttpClients.createDefault();
		url = "http://localhost/phpwind/index.php?c=post&a=doadd&_json=1&fid=2";
		httppost = new HttpPost(url);
		httppost.setConfig(config);
		List<NameValuePair> values3 = new ArrayList<NameValuePair>();
		values3.add(new BasicNameValuePair("atc_content", "11-04-01"));
		values3.add(new BasicNameValuePair("atc_title", "11-04-01"));
		values3.add(new BasicNameValuePair("csrf_token", token));
		values3.add(new BasicNameValuePair("reply_notice", "1"));
		values3.add(new BasicNameValuePair("special", "default"));
		httppost.setEntity(new UrlEncodedFormEntity(values3, "utf-8"));
		response = httpclient.execute(httppost, context1);
		entity = response.getEntity();
		content = EntityUtils.toString(entity, "utf-8");
		p = Pattern.compile("tid=(.+?)&fid");
		m = p.matcher(content);
		String tid = null;
		while(m.find()){
			System.out.println("tid:"+m.group(1));
			tid = m.group(1);
		}
		httpclient.close();
		
		//7、发帖成功
		httpclient = HttpClients.createDefault();
		url = "http://localhost/phpwind/read.php?tid="+tid+"&fid=2";
		httpget = new HttpGet(url);
		httpget.setConfig(config);
		httpclient.execute(httpget, context1);
		httpclient.close();
		
		//8、回帖
		httpclient = HttpClients.createDefault();
		url = "http://localhost/phpwind/index.php?c=post&a=doreply&_getHtml=1";
		httppost = new HttpPost(url);
		httppost.setConfig(config);
		List<NameValuePair> values4 = new ArrayList<NameValuePair>();
		values4.add(new BasicNameValuePair("atc_content", "回复01"));
		values4.add(new BasicNameValuePair("tid", tid));
		values4.add(new BasicNameValuePair("csrf_token", token));
		httppost.setEntity(new UrlEncodedFormEntity(values4, "utf-8"));
		httpclient.execute(httppost, context1);
		httpclient.close();
		
	}

}
