package com.dingtalk.h5app.quickstart.config;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ExcelUtils {


    public static void export(HttpServletResponse response) {

        try {
            OutputStream out = response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.setContentType("application/vnd.ms-excel");
        try {
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode("商品订单", "UTF-8") + ".xls");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
