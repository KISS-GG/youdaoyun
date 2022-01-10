package top.zymnb.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zym
 */
public class DownloadUtil {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static  String BASE_DIR = "";

    public static  String COOKIES = "";

    public static  String CSTK = "";

    public static FileWriter logFileW;

    public static final Map<String,String> requestProperty = new HashMap<>(2);

    public static void main(String[] args) {
        downloadWord();
    }

    public static void downloadWord(){
        File fileDir = new File(BASE_DIR);
        File file = new File(BASE_DIR + "/log.txt");
        if (!file.exists()){
            try {
                fileDir.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 创建日志 文件
        try (FileWriter logFile = new FileWriter(file)){
            logFileW = logFile;
            // 获取ROOT ID
            String rootJson = HttpUtil.sendPostSSL("https://note.youdao.com/yws/api/personal/file?method=getByPath&keyfrom=web&cstk="+CSTK+"&sev=j1",
                    requestProperty,"path=/&entire=true&purge=false");
            JSONObject rootObject = JSONObject.parseObject(rootJson);

            String rootDirId = rootObject.getJSONObject("fileEntry").getString("id");

            JSONArray lv1DirArr = getFileEntries(rootDirId);
            fileToWord(lv1DirArr,new File(BASE_DIR));
            logFileW.append("生成文件结束").append(System.getProperty("line.separator"));
            logFileW.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static JSONArray getFileEntries(String parentId){

        String rootJson = HttpUtil.sendGetSSL("https://note.youdao.com/yws/api/personal/file/"+parentId+"?all=true&f=true&len=30&sort=1&isReverse=false&method=listPageByParentId&keyfrom=web&cstk="+CSTK+"&sev=j1",requestProperty);
        JSONObject rootObject = JSONObject.parseObject(rootJson);

        JSONArray jsonArray = rootObject.getJSONArray("entries");

        // 分页处理
        int count = rootObject.getInteger("count");
        if (count > 30){

            while (true){
                String lastId = jsonArray.getJSONObject(jsonArray.size() - 1).getJSONObject("fileEntry").getString("id");
                rootJson = HttpUtil.sendGetSSL("https://note.youdao.com/yws/api/personal/file/"+parentId+"?all=true&f=true&len=30&lastId="+lastId+"&sort=1&isReverse=false&method=listPageByParentId&keyfrom=web&cstk="+CSTK+"&sev=j1",requestProperty);
                rootObject = JSONObject.parseObject(rootJson);
                JSONArray newJsonArray = rootObject.getJSONArray("entries");
                jsonArray.addAll(newJsonArray);
                if (newJsonArray.size() != 30){
                    break;
                }
            }


        }


        return jsonArray;
    }

    public static void fileToWord(JSONArray entriesJsonObject,File parentDir){

        for (int i = 0; i < entriesJsonObject.size(); i++) {
            JSONObject entryFile = entriesJsonObject.getJSONObject(i);
            JSONObject entryFileFileEntry = entryFile.getJSONObject("fileEntry");
            boolean isDir = entryFileFileEntry.getBoolean("dir");
            String fileId = entryFileFileEntry.getString("id");
            String fileName = entryFileFileEntry.getString("name");
            String version = entryFileFileEntry.getString("version");
            // 秒
            long createDateLong = entryFileFileEntry.getLongValue("createTimeForSort");
            // 转化为毫秒
            Date createDate = new Date(createDateLong * 1000);
            if (isDir){
                // 先创建目录
                File dir = new File(parentDir.getAbsolutePath()+"/"+fileName);
                if (!dir.exists()){
                    dir.mkdirs();
                }
                JSONArray fileArr = getFileEntries(fileId);
                fileToWord(fileArr,dir);
            }else {
                synchronized ("1"){
                    buildWordFile(fileId,fileName,parentDir,version,DATE_FORMAT.format(createDate));
                }
            }
        }

    }

    public static void buildWordFile(String fileId,String fileName,File parentFile,String version,String createDate){
        String url = "https://note.youdao.com/ydoc/api/personal/doc?method=download-docx&fileId="+fileId+"&cstk="+CSTK+"&keyfrom=web&sev=j1";

        if (fileName.toLowerCase().endsWith(".note")){
            fileName = fileName.substring(0,fileName.length()-5) + ".docx";
        }else {
            url = "https://note.youdao.com/yws/api/personal/sync?method=download&fileId="+fileId+"&version="+version+"&cstk="+CSTK+"&keyfrom=web&sev=j1";
        }

        String filePath = parentFile.getAbsolutePath()+"/"+createDate+"-"+fileName;

        File file = new File(filePath);
        try (InputStream is = HttpUtil.downloadGetSSL(url, requestProperty); FileOutputStream fos = new FileOutputStream(file)) {
            System.out.println("正在生成文件："+filePath);
            logFileW.append("正在生成文件：").append(filePath).append(System.getProperty("line.separator"));
            logFileW.flush();
            IOUtils.copy(is, fos);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("生成文件失败："+filePath);
            try {
                logFileW.append("生成文件失败：").append(filePath).append(System.getProperty("line.separator"));
                logFileW.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }


}
