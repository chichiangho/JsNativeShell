package com.seeyon.cmp.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.seeyon.cmp.common.BuildConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by wangxk on 2017-6-9.
 */

public class SignCheckUtile {
    private static final String TAG = "SignCheck";
    private static final String a = "84:16:90:7C:48:0F:8D:DE:5D:8E:63:C7:D3:19:BF:00:C3:21:B0:DF";

    /**
     * 获取应用的签名
     *
     * @return
     */
    private static String getCertificateSHA1Fingerprint(Context context) {
        try {
            //获取包管理器
            PackageManager pm = context.getPackageManager();
            //获取当前要获取 SHA1 值的包名，也可以用其他的包名，但需要注意，
            //在用其他包名的前提是，此方法传递的参数 Context 应该是对应包的上下文。
            String packageName = context.getPackageName();
            //返回包括在包中的签名信息
            int flags = PackageManager.GET_SIGNATURES;
            PackageInfo packageInfo = null;
            //获得包的所有内容信息类
            packageInfo = pm.getPackageInfo(packageName, flags);
            //签名信息
            Signature[] signatures = packageInfo.signatures;
            byte[] cert = signatures[0].toByteArray();
            //将签名转换为字节数组流
            InputStream input = new ByteArrayInputStream(cert);
            //证书工厂类，这个类实现了出厂合格证算法的功能
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            //X509 证书，X.509 是一种非常通用的证书格式
            X509Certificate c = (X509Certificate) cf.generateCertificate(input);
            //加密算法的类，这里的参数可以使 MD4,MD5 等加密算法
            MessageDigest md = MessageDigest.getInstance("SHA1");
            //获得公钥
            byte[] publicKey = md.digest(c.getEncoded());
            //字节到十六进制的格式转换
            String hexString = byte2HexFormatted(publicKey);
            return hexString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    //这里是将获取到得编码进行16 进制转换
    private static String byte2HexFormatted(byte[] arr) {

        StringBuilder str = new StringBuilder(arr.length * 2);

        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1)
                h = "0" + h;
            if (l > 2)
                h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }

    /**
     * 检测签名是否正确
     * @return true 签名正常 false 签名不正常
     */
    public static boolean check(Context context) {
        if(BuildConfig.DEBUG){
            return true;
        }
        String cer = SignCheckUtile.getCertificateSHA1Fingerprint(context);
        if (cer != null) {
            cer = cer.trim();
            String ta = a.trim();
            if (cer.equals(ta)) {
                return true;
            }
        } else {//没有值 可能会值取值错误，默认有有签名
            return true;
        }
        return false;
    }
}
