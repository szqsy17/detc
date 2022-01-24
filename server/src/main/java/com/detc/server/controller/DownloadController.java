package com.detc.server.controller;

import com.detc.server.annotation.SkipVerify;
import com.detc.server.config.Variable;
import com.detc.server.util.HttpUtils;
import com.detc.server.util.JwtUtils;
import lombok.Cleanup;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author szqsy17
 */
@Log4j2
@RestController
@RequestMapping(value = "/download")
public class DownloadController {

    @SkipVerify
    @GetMapping(value = "/{filePath}/{fileName}")
    public void download(@PathVariable("filePath") String filePath, @PathVariable("fileName") String fileName, String t, HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean allowAccess = "mirror".equals(filePath) || ("log".equals(filePath) && JwtUtils.verifyJwt(t));
        if (!allowAccess) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(HttpUtils.setMessage("403", "拒绝访问"));
            return;
        }

        File downloadFile = new File(Variable.storagePath + "/" + filePath + '/' + fileName);
        if (!downloadFile.exists()) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(HttpUtils.setMessage("404", "文件不存在"));
            return;
        }

        String range = request.getHeader("range"), rangeStart = "bytes=", rangeSplit = "-";
        long startByte = 0, endByte = downloadFile.length() - 1;
        if (range != null && range.contains(rangeStart) && range.contains(rangeSplit)) {
            // response http状态码要为206
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            // 分割range
            String[] ranges = range.substring(range.lastIndexOf("=") + 1).trim().split(rangeSplit);
            try {
                if (ranges.length == 1 && range.endsWith(rangeSplit)) {
                    // range类型一枚举：bytes=-1234
                    startByte = Long.parseLong(ranges[0]);
                } else if (ranges.length == 1 && range.startsWith(rangeSplit)) {
                    // range类型二枚举：bytes=1234-
                    endByte = Long.parseLong(ranges[0]);
                } else if (ranges.length > 1) {
                    // range类型三枚举：bytes=22-2343
                    startByte = Long.parseLong(ranges[0]);
                    endByte = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                startByte = 0;
                endByte = downloadFile.length() - 1;
            }
        }

        // 要下载的长度（endByte为总长度-1，这时候要加回去）
        long contentLength = endByte - startByte + 1;

        // 文件类型
        String contentType = request.getServletContext().getMimeType(fileName) == null
                ? "application/octet-stream" : request.getServletContext().getMimeType(fileName);

        // 响应头设置
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", contentType);
        // inline：浏览器内嵌显示一个文件(如果浏览器支持的话)
        // Content-Disposition: inline;filename=xxx.pdf
        // attachment：下载一个文件
        // Content-Disposition: attachment;filename=xxx.pdf
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("Content-Length", String.valueOf(contentLength));
        // Content-Range，格式为：[要下载的开始位置]-[结束位置]/[文件总大小]
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + downloadFile.length());

        // 定义已传送数据
        long transmitted = 0;
        try {
            @Cleanup RandomAccessFile randomAccessFile = new RandomAccessFile(downloadFile, "r");
            @Cleanup BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream());
            // 开辟缓冲区8k
            byte[] buff = new byte[8192];
            int len = 0;
            randomAccessFile.seek(startByte);
            // 判断是否到了最后不足缓冲区的部分，"(transmitted + len) <= contentLength"要放前面
            while ((transmitted + len) <= contentLength && (len = randomAccessFile.read(buff)) != -1) {
                bufferedOutputStream.write(buff, 0, len);
                transmitted += len;
            }
            //处理不足buff.length部分
            if (transmitted < contentLength) {
                len = randomAccessFile.read(buff, 0, (int) (contentLength - transmitted));
                bufferedOutputStream.write(buff, 0, len);
                transmitted += len;
            }

            log.error("下载完毕，开始于：" + startByte + "，结束于：" + endByte + "，已下载：" +
                    (transmitted / 1024 > 1024 ? transmitted / 1024 / 1024 + "MB" : transmitted / 1024 + "KB"));
        } catch (ClientAbortException e) {
            // 捕获此异常表示用户停止下载
            log.error("停止下载，开始于：" + startByte + "，结束于：" + endByte + "，已下载：" +
                    (transmitted / 1024 > 1024 ? transmitted / 1024 / 1024 + "MB" : transmitted / 1024 + "KB"));
        }
    }

}
