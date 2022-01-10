package top.zymnb.util;



import com.alibaba.fastjson.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.25 Safari/537.36 Core/1.70.3880.400 QQBrowser/10.8.4554.400";

    public static String sendPostSSL(String urlPath) {
        return sendPostSSL(urlPath , new HashMap<>());
    }

    public static String sendGetSSL(String urlPath) {
        return sendGetSSL(urlPath , new HashMap<>());
    }

    public static String sendPost(String urlPath) {
        return sendPost(urlPath , new HashMap<>());
    }

    public static String sendGet(String urlPath) {
        return sendGet(urlPath , new HashMap<>());
    }

    public static String sendPostSSL(String urlPath , Map<String ,String> requestProperty){
        return sendPostSSL(urlPath,requestProperty,null);
    }

    public static String sendPostSSL(String urlPath , Map<String ,String> requestProperty,String data) {
        try {
            URL url = new URL(null,urlPath, new sun.net.www.protocol.https.Handler());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-agent",USER_AGENT);
            //设置HTTPS
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            TrustManager[] tm = {new MyX509TrustManager()};
            ctx.init(null, tm, new SecureRandom());
            connection.setSSLSocketFactory(ctx.getSocketFactory());
            connection.setHostnameVerifier((arg0, arg1) -> true);

            connection.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
           //设置requestProperty
            for (Map.Entry<String,String> entry: requestProperty.entrySet()){
                connection.setRequestProperty(entry.getKey(),entry.getValue());
            }

            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(
            connection.getOutputStream());
            if (data != null){
                out.writeBytes(data);
            }


            //获取数据
            InputStream inStream = connection.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] b = outStream.toByteArray();//网页的二进制数据
            outStream.close();
            inStream.close();
            return new String(b, StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String sendGetSSL(String urlPath , Map<String ,String> requestProperty) {
        try {
            URL url = new URL(null,urlPath, new sun.net.www.protocol.https.Handler());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-agent",USER_AGENT);
            //设置HTTPS
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            TrustManager[] tm = {new MyX509TrustManager()};
            ctx.init(null, tm, new SecureRandom());
            connection.setSSLSocketFactory(ctx.getSocketFactory());
            connection.setHostnameVerifier((arg0, arg1) -> true);
            //设置requestProperty
            for (Map.Entry<String,String> entry: requestProperty.entrySet()){
                connection.setRequestProperty(entry.getKey(),entry.getValue());
            }
            //获取数据
            InputStream inStream = connection.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] b = outStream.toByteArray();//网页的二进制数据
            outStream.close();
            inStream.close();
            return new String(b, StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static InputStream downloadGetSSL(String urlPath , Map<String ,String> requestProperty) {
        try {
            URL url = new URL(null,urlPath, new sun.net.www.protocol.https.Handler());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("User-agent",USER_AGENT);
            //设置HTTPS
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            TrustManager[] tm = {new MyX509TrustManager()};
            ctx.init(null, tm, new SecureRandom());
            connection.setSSLSocketFactory(ctx.getSocketFactory());
            connection.setHostnameVerifier((arg0, arg1) -> true);
            //设置requestProperty
            for (Map.Entry<String,String> entry: requestProperty.entrySet()){
                connection.setRequestProperty(entry.getKey(),entry.getValue());
            }
            //获取数据
            return connection.getInputStream();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String sendGet(String urlPath , Map<String ,String> requestProperty) {
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-agent",USER_AGENT);
            //设置requestProperty
            for (Map.Entry<String,String> entry: requestProperty.entrySet()){
                connection.setRequestProperty(entry.getKey(),entry.getValue());
            }
            //获取数据
            InputStream inStream;
            if (connection.getResponseCode() == 200){
                inStream = connection.getInputStream();
            }else {
                inStream = connection.getErrorStream();
            }

            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] b = outStream.toByteArray();//网页的二进制数据
            outStream.close();
            inStream.close();
            return new String(b, StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String sendPost(String urlPath , Map<String ,String> requestProperty) {
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-agent",USER_AGENT);
            connection.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            //设置requestProperty
            for (Map.Entry<String,String> entry: requestProperty.entrySet()){
                connection.setRequestProperty(entry.getKey(),entry.getValue());
            }
            
            //获取数据
            InputStream inStream;
            if (connection.getResponseCode() == 200){
                inStream = connection.getInputStream();
            }else {
                inStream = connection.getErrorStream();
            }
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] b = outStream.toByteArray();//网页的二进制数据
            outStream.close();
            inStream.close();
            return new String(b, StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String sendPost(String urlPath , Map<String ,String> requestProperty, JSONObject param) {
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-agent",USER_AGENT);
            connection.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            connection.setDoOutput(true);
            connection.setDoInput(true);
            //设置requestProperty
            for (Map.Entry<String,String> entry: requestProperty.entrySet()){
                connection.setRequestProperty(entry.getKey(),entry.getValue());
            }

            // 得到请求的输出流对象
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(),"UTF-8");
            writer.write(param.toString());
            writer.flush();

            //获取数据
            InputStream inStream;
            if (connection.getResponseCode() == 200){
                inStream = connection.getInputStream();
            }else {
                inStream = connection.getErrorStream();
            }
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            byte[] b = outStream.toByteArray();//网页的二进制数据
            outStream.close();
            inStream.close();
            return new String(b, StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}

class MyX509TrustManager implements X509TrustManager {


    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}
