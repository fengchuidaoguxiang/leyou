package com.leyou.order.config;

import com.github.wxpay.sdk.WXPayConfig;
import lombok.Data;

import java.io.InputStream;

@Data
public class PayConfig implements WXPayConfig {

    private String appID; // 公众号ID

    private String mchID; // 商品号

    private String key = "Tmosdf454ojfdsfsdf"; // 生成签名的密钥

    private int httpConnectionTimeoutMs = 1000; // 连接超时时间

    private int httpReadTimeoutMs = 5000; // 读取超时时间

    private String notifyUrl = "http://www.leyou.com/"; // 下单通知回调地址

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return this.httpConnectionTimeoutMs;
    }
}
