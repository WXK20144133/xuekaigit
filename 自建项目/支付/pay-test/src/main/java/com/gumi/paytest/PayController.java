package com.gumi.paytest;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.gumi.paytest.tencent.WXPay;
import com.gumi.paytest.tencent.WXPayConfigImpl;
import com.gumi.paytest.tencent.WXPayConstants;
import com.gumi.paytest.tencent.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("pay")
@ResponseBody
public class PayController {

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private WXPayConfigImpl wxPayConfig;


    //支付宝获取参数
    @RequestMapping(value = "alipayParams", method = RequestMethod.POST)
    public String alipayParams() {
        System.out.println("支付宝： APP获取请求参数");
        OrderNoCreate.orderNo = OrderNoCreate.orderNo + 1;
        System.out.println("订单号:   ALPAYTESTNO1" + OrderNoCreate.orderNo);
//        //实例化客户端
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getServerUrl(), alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(), alipayConfig.getFormat(), alipayConfig.getCharset(),
                alipayConfig.getPublicKey(), alipayConfig.getSignType());
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody("我是测试数据");
        model.setSubject("App支付测试Java");
        model.setOutTradeNo("ALPAYTESTNO1" + OrderNoCreate.orderNo);
        model.setTimeoutExpress("30m");
        model.setTotalAmount("0.01");
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        String orderString = "";
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            orderString = response.getBody();//就是orderString 可以直接给客户端请求，无需再做处理。
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return orderString;
    }

    //支付宝获取回调
    @RequestMapping(value = "alipayNotify", method = RequestMethod.POST)
    public void alipayNotify(HttpServletRequest request, HttpServletResponse response) throws AlipayApiException {
        System.out.println("支付宝： 进入回调");
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
        //切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
        //boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        System.out.println("回调参数:   " + params.toString());
        boolean flag = AlipaySignature.rsaCheckV1(params, alipayConfig.getPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType());
        String notifyResp;
        if (flag) { //支付验证成功
            System.out.println("回调：支付验证成功");
            notifyResp = "success";
        } else { //验证失败
            System.out.println("回调：支付验证失败");
            notifyResp = "fail";
        }
        try {
            PrintWriter writer = response.getWriter();
            writer.print(notifyResp);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //微信获取参数
    @RequestMapping(value = "wxParams", method = RequestMethod.POST)
    public Map<String, String> wxParams() throws Exception {

        System.out.println("微信： APP获取请求参数");
        OrderNoCreate.orderNo = OrderNoCreate.orderNo + 1;
        System.out.println("订单号:   WXPAYTESTNO1" + OrderNoCreate.orderNo);

        Map<String, String> data = new HashMap<>();
        data.put("body", "test");  //确认支付标题
        data.put("out_trade_no", "WXPAYTESTNO1" + OrderNoCreate.orderNo);  //订单号
        data.put("spbill_create_ip", wxPayConfig.getSpbillCreateIp());
        data.put("total_fee", "1"); //金额 单位 分
        data.put("trade_type", "APP"); // APP类型
        data.put("notify_url", wxPayConfig.getNotifyUrl());  //回调地址
        Map<String, String> response = new WXPay(wxPayConfig).unifiedOrder(data);  //微信返回参数
        System.out.println(response.toString());
        if ("SUCCESS".equals(response.get("result_code"))) {
            System.out.println("获取请求参数成功");
        } else {
            System.out.println("获取请求参数失败");
            System.out.println(response.get("err_code_des"));
            return null;
        }
        Map<String, String> result = new HashMap<>();
        result.put("appid", wxPayConfig.getAppID());
        result.put("partnerid", wxPayConfig.getMchID());
        result.put("prepayid", response.get("prepay_id"));
        result.put("noncestr", response.get("nonce_str"));
        result.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        result.put("package", "Sign=WXPay");
        result.put("sign", WXPayUtil.generateSignature(result, wxPayConfig.getKey(), WXPayConstants.SignType.HMACSHA256));
        return result;
    }


    //微信回调
    @RequestMapping(value = "wxNotify", method = RequestMethod.POST)
    public void wxNotify(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("微信: 进入回调");
        InputStream inStream;
        try {
            inStream = request.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inStream.close();
            String result = new String(outSteam.toByteArray(), "utf-8");//获取微信调用我们notify_url的返回信息
            Map<String, String> responseMsg = WXPayUtil.xmlToMap(result);
            boolean flag = WXPayUtil.isSignatureValid(responseMsg, wxPayConfig.getKey(), WXPayConstants.SignType.HMACSHA256); //验证回调的合法性
            String responseStr;
            if (flag) {
                System.out.println("验证通过");
                System.out.println(responseMsg.toString());
                String orderNo = responseMsg.get("out_trade_no");  //商户订单号
                String transactionNo = responseMsg.get("transaction_id"); //微信支付订单号
                String total_fee = responseMsg.get("total_fee");  //支付金额
                if ("SUCCESS".equals(responseMsg.get("result_code"))) {
                    //支付成功
                    responseStr = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                } else {
                    responseStr = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[参数格式校验错误]]></return_msg></xml>";
                }
            } else {
                System.out.println("验证失败");
                responseStr = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[参数格式校验错误]]></return_msg></xml>";
            }
            PrintWriter writer = response.getWriter();
            writer.print(responseStr);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("微信: 回调结束");
    }
}
