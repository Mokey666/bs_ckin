package com.controller;

import com.common.Const;
import com.common.ServerResponse;
import com.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/file/")
public class FileController {

    private static final String UPLOAD_PATH_PREFIX = "static/file/";



    private boolean flag = false;

    //单文件上传
    //url：/file/upload.do
    //上传文件
    @ResponseBody
    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    public ServerResponse<String> fileUpload(HttpSession session, Integer groupId, @RequestParam("file") MultipartFile uploadfile){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }

        if (uploadfile == null){
            return ServerResponse.creatByErrorMessage("上传文件为null");
        }
        //获取文件名
        String fileName = uploadfile.getOriginalFilename();
        if (isExist(new File("src/main/resources/"+UPLOAD_PATH_PREFIX + groupId+"/"),fileName)){
            return ServerResponse.creatByErrorMessage("文件名重复");
        }
        //文件保存路经
        String realPath = new String("src/main/resources/"+UPLOAD_PATH_PREFIX + groupId +"/");

        File file = new File(realPath);
        if (!file.exists()){
            file.mkdirs();
        }

        try {
            File newFile = new File(file.getAbsolutePath()+File.separator+fileName);
            uploadfile.transferTo(newFile);
            //返回真正的文件路经;
            String realFilePath = newFile.getPath();
            return ServerResponse.creatBySuccess(realFilePath);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ServerResponse.creatByErrorMessage("上传失败");
    }


    //多文件上传
    //url: /file/multi_file_upload.do
    //多文件上传
    @ResponseBody
    @RequestMapping(value = "multi_file_upload.do", method = RequestMethod.POST)
    public ServerResponse<List<String>> multifileUpload(Integer groupId, HttpSession session,  @RequestParam("uploadfiles")List<MultipartFile> uploadfiles){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }

        if (uploadfiles == null && uploadfiles.size() == 0){
            return ServerResponse.creatByErrorMessage("上传文件为null");
        }
        //返回所有路经
        List<String> pathlist = new ArrayList<>();
        for (MultipartFile uploadfile : uploadfiles){
            if (uploadfile == null){
                return ServerResponse.creatByErrorMessage("上传文件为null");
            }
            //获取文件名
            String fileName = uploadfile.getOriginalFilename();
            if (isExist(new File("src/main/resources/"+UPLOAD_PATH_PREFIX + groupId + "/"),fileName)){
                return ServerResponse.creatByErrorMessage("文件名重复");
            }

            String realPath = new String("src/main/resources/"+UPLOAD_PATH_PREFIX+groupId+"/");

            File file = new File(realPath);
            if (!file.exists()){
                file.mkdirs();
            }
            try {
                File newFile = new File(file.getAbsolutePath()+File.separator+fileName);
                uploadfile.transferTo(newFile);
                //返回真正的文件路经;
                String realFilePath = newFile.getPath();
                pathlist.add(realFilePath);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return ServerResponse.creatBySuccess(pathlist);
    }


    //文件下载
    //url：/file/file_download.do
    //文件下载：通过文件名(这里文件名没什么太大意义)和文件路径，来进行文件下载。
    /**
    * 做了改动
     * **/
    @RequestMapping(value = "file_download.do", method = RequestMethod.POST)
    public ServerResponse<String> fileDownload(HttpSession session,String fileName, String filePath, HttpServletResponse response) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }

        File file = new File(filePath);
        if (!file.exists()){
            return ServerResponse.creatByErrorMessage("文件不存在");
        }else {
            response.setContentType("application/force-download");
            response.addHeader("Content-Disposition","attachment;fileName="+fileName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while(i != -1){
                    os.write(buffer,0,i);
                    i = bis.read(buffer);
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(bis != null){
                    try {
                        bis.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                if (fis != null){
                    try {
                        fis.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
            return ServerResponse.creatByErrorMessage("下载成功");
        }
    }

    //返回所有文件
    //url：/file/get_all_filepath.do
    //返回所有文件的路经
    @ResponseBody
    @RequestMapping(value = "get_all_filepath.do", method = RequestMethod.POST)
    public ServerResponse<List<String>> getAllFilePath(Integer groupId, HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null){
            return ServerResponse.creatByErrorMessage("用户未登录");
        }

        File file = new File("src/main/resources/"+UPLOAD_PATH_PREFIX + groupId+"/");
        if (!file.exists()){
            return ServerResponse.creatBySuccess(null);
        }
        List<String> allPath = new ArrayList<>();
        return ServerResponse.creatBySuccess(getAllFilePath(file,allPath));
    }

    //判断文件名是否重复
    private boolean isExist(File file,String targetfilename){
        File[] files = file.listFiles();
        if (files == null){
            return flag;
        }
        for (File newfile : files){
            if (newfile.isDirectory()){
                isExist(newfile,targetfilename);
            }else {
                if (targetfilename.equals(newfile.getName())){
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    //获得所有文件地址
    private List<String> getAllFilePath(File file, List<String> pathlist){
        File[] files = file.listFiles();
        for (File newfile : files){
            if (newfile.isFile()){
                pathlist.add(newfile.getAbsolutePath());
            }else {
                getAllFilePath(newfile,pathlist);
            }
        }
        return pathlist;
    }

}
