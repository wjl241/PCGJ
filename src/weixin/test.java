package weixin;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

import util.HttpClientUtil;

public class test {
	public static void main(String[] args) {
		test5();
		
	}
	private static void test5() {
		 new QFMain().setVisible(true);
	}
	private static void test4() {
		HttpClientUtil httpClientUtil = new HttpClientUtil();
		//String api_url = "https://apiway.jihes.com/temai?os_type=1&os_version=10.2&app_version=1.0.0&api_version=2.0.0&device_name=myphone&client_id=83a14e030ad94096b4cdbfc2a94dc203&api=user_login/login";
		String api_url = "http://localhost:8080/qfzs/queue/synQueue?selectHour=13";
		Map<String,String> createMap = new HashMap<String,String>();
		
		JSONArray sdfs = new JSONArray();
		JSONArray zdlb = new JSONArray();
		
		JSONObject sd = new JSONObject();
		sd.put("item_id", "430185930581");
		sd.put("expire_time", 15);
		sd.put("number", 2);
		sd.put("status", (byte)1);
		sd.put("remark", "这是一个很棒的商品1");
		JSONObject sd2 = new JSONObject();
		sd2.put("item_id", "430185930582");
		sd2.put("expire_time", 15);
		sd2.put("number", 3);
		sd2.put("status", (byte)1);
		sd2.put("remark", "这是一个很棒的商品2");
		sdfs.add(sd);
		sdfs.add(sd2);
		
		JSONObject zd = new JSONObject();
		zd.put("item_id", "430185930583");
		zd.put("expire_time", 16);
		zd.put("number", 2);
		zd.put("status", (byte)0);
		zd.put("remark", "这是一个很棒的商品3");
		JSONObject zd2 = new JSONObject();
		zd2.put("item_id", "4301859305822");
		zd2.put("expire_time", 16);
		zd2.put("number", 3);
		zd2.put("status", (byte)0);
		zd2.put("remark", "这是一个很棒的商品4");
		zdlb.add(zd);
		zdlb.add(zd2);
		System.err.println(sdfs.toJSONString());
		System.err.println(zdlb.toJSONString());
		
		createMap.put("sdfs", sdfs.toJSONString());
		createMap.put("zdlb", zdlb.toJSONString());
		String ret = httpClientUtil.doPost2(api_url,createMap,"utf-8");
		System.err.println(ret);
		
		
	}
	private static void test() {
		String url = "http://gw.api.taobao.com/router/rest";
		String appkey = "23704658";
		String secret = "7342d0a35e984c61a907a0d58435822e";
		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setSmsType("normal");
		req.setSmsFreeSignName("集合特卖");
		req.setSmsParamString("{\"code\":\"123456\",\"product\":\"集合特卖群发工具\"}");
		req.setRecNum("15869171597");
		req.setSmsTemplateCode("SMS_73535001");
		AlibabaAliqinFcSmsNumSendResponse rsp;
		try {
			rsp = client.execute(req);
			System.out.println(rsp.getBody());
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	private static void test2() {
		String url = "http://dev.jihes.com/doc/index.php?s=/1&page_id=291";
		Map<String,String> createMap = new HashMap<String,String>();
		HttpClientUtil httpClientUtil = new HttpClientUtil();
		String ret = httpClientUtil.doPost2(url,createMap,"utf-8");  
		System.err.println(ret);
		
	}
	
	private static void test3(){
		for(int i =0;i<100;i++) {
			System.out.println((int)((Math.random()*9+1)*100000));  
		}
		
	}
}
