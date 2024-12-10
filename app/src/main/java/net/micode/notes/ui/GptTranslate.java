package net.micode.notes.ui;

import static net.micode.notes.data.Notes.TAG;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import net.micode.notes.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;

public class GptTranslate {

    private String mStringForTest = "{\n" +
            "\"id\": \"chatcmpl-8WkNTgSLRzM8ypGiwVCtcYS0vAcoJ\",\n" +
            "\"object\": \"chat.completion\",\n" +
            "\"created\": 1702815463,\n" +
            "\"model\": \"gpt-35-turbo\",\n" +
            "\"choices\": [\n" +
            "{\n" +
            "\"index\": 0,\n" +
            "\"message\": {\n" +
            "\"role\": \"assistant\",\n" +
            "\"content\": \"Hello there! How can I assist you today?\"\n" +
            "},\n" +
            "\"finish_reason\": \"stop\"\n" +
            "}\n" +
            "],\n" +
            "\"usage\": {\n" +
            "\"prompt_tokens\": 19,\n" +
            "\"completion_tokens\": 10,\n" +
            "\"total_tokens\": 29\n" +
            "}\n" +
            "}";

    private String apiKey = "sk-Gus0G4Qhx9bdXdZ60bAfE9D6A0144fBcB17fA8326401134c";

    private String url = "https://api1.zhtec.xyz/v1/chat/completions";
    private String promptForTest = "What is the meaning of life?";

    private static String gptResponse;

    public static void setGptResponse(String response) {
        GptTranslate.gptResponse = response;
    }

    public static String getResponse() {
        return gptResponse;
    }

    public enum RequestMode {TranslateMode, DefaultMode}

    private class MyThread extends Thread {

        private boolean debugMode;
        private String requestText;

        public MyThread(String request, boolean dbgMode) {
            requestText = request;
            debugMode = dbgMode;
        }

        @Override
        public void run() {
            super.run();

            String responseText = mStringForTest;
            if (!debugMode) {
                responseText = makeGptRequest(requestText);
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            String mContent = "400 Bad Request.Check your Language.";
            if (responseText != null) {
                mContent = getContentFromGpt(responseText);
            }
            setGptResponse(mContent);
        }
    }

    public void makeRequest(String request, RequestMode mRequestMode, Context context, boolean debugMode) {

        Log.d(TAG, "makeRequest()");
        String mRequest = request;
        if (mRequestMode == RequestMode.TranslateMode) {
            mRequest = decorateWithTranslator(mRequest);
            Log.d(TAG, mRequest);
        }
        mRequest = mRequest.replaceAll("\r|\n", " ");
        MyThread myThread = new MyThread(mRequest, debugMode);
        myThread.start();

        //loading
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context, R.string.sync_loading, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();

        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //拆包
    private String getContentFromGpt(String responseText) {
        String gptContent = null;
        try {
            JSONObject json = new JSONObject(responseText);
            JSONArray mJsonArray = json.getJSONArray("choices");
            JSONObject mcontent = (JSONObject) mJsonArray.get(0);
            gptContent = mcontent.getJSONObject("message").getString("content");
            Log.d(TAG, gptContent);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return gptContent;
    }

    private String makeGptRequest(String mRequest) {
        try {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            // 设置运行输入,输出:
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // Post方式不能缓存,需手动设置为false
            conn.setUseCaches(false);
            Log.d(TAG, mRequest);
            String postData = "{\n" +
                    "  \"model\": \"gpt-3.5-turbo\",\n" +
                    "  \"messages\": [\n" +
                    "    {\n" +
                    "      \"role\": \"system\",\n" +
                    "      \"content\": \"You are a helpful assistant.\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"role\": \"user\",\n" +
                    "      \"content\": \"" + mRequest+ "\"\n" +
                    //"      \"content\": \"" + "go fuck yourself." + "\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";
            Log.d(TAG, postData);

            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();
            Log.d(TAG, String.valueOf(conn.getResponseCode()));
            // 获取响应的输入流对象
            InputStream is = conn.getInputStream();
            // 创建字节输出流对象
            ByteArrayOutputStream message = new ByteArrayOutputStream();

            // 定义读取的长度
            int len = 0;
            // 定义缓冲区
            byte buffer[] = new byte[1024];
            // 按照缓冲区的大小，循环读取
            while ((len = is.read(buffer)) != -1) {
                // 根据读取的长度写入到os对象中
                message.write(buffer, 0, len);
            }
            // 释放资源
            is.close();
            message.close();
            // 返回字符串
            String responseText = new String(message.toByteArray());
            return responseText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String decorateWithTranslator(String text) {
        return "Translate it into Chinese:" + text;
    }
}