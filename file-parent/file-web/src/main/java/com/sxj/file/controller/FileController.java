package com.sxj.file.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import third.rewrite.fastdfs.service.IStorageClientService;

import com.sxj.cache.manager.CacheLevel;
import com.sxj.cache.manager.HierarchicalCacheManager;
import com.sxj.file.fastdfs.IFileUpLoad;
import com.sxj.util.common.StringUtils;
import com.sxj.util.exception.WebException;
import com.sxj.util.logger.SxjLogger;

@Controller
public class FileController
{
    
    @Autowired
    private IStorageClientService storageClientService;
    
    @Autowired
    private SupervisorShiroRedisCacheManager redesCacheManager;
    
    @RequestMapping(value = "{group}/{st}/{f1}/{f2}/{id}", method = RequestMethod.GET)
    public void getImage(@PathVariable String group, @PathVariable String st,
            @PathVariable String f1, @PathVariable String f2,
            @PathVariable String id, HttpServletRequest request,
            HttpServletResponse response) throws WebException
    {
        try
        {
            // String rurl = getHeadersInfo(request).get("referer");
            //            if (rurl == null)
            //            {
            //                response.setStatus(404);
            //                return;
            //            }
            //            if (request.getCookies() == null
            //                    && !((rurl.contains("finance-manager")
            //                            || rurl.contains("manage.zijincaifu.net")
            //                            || rurl.contains("finance-website") || rurl.contains("www.zijincaifu.net"))))
            //            {
            //                response.setStatus(404);
            //                return;
            //            }
            //  Map<String, Cookie> cookieMap = ReadCookieMap(request);
            // if (rurl.contains("supervisor-website")
            //        || rurl.contains("www.menchuang.org.cn"))
            //  {
            //            if (cookieMap.get("SHAREJSESSIONID_WEBSITE") == null)
            //            {
            //                response.setStatus(404);
            //                return;
            //            }
            //            Boolean flag = redesCacheManager.get("website.session.cache.name",
            //                    cookieMap.get("SHAREJSESSIONID_WEBSITE").getValue());
            //            if (!flag)
            //            {
            //                response.setStatus(404);
            //                return;
            //            }
            
            //  }
            // else if (rurl.contains("supervisor-manager")
            //         || rurl.contains("manage.menchuang.org.cn"))
            //            {
            //                if (cookieMap.get("SHAREJSESSIONID_MANAGE") == null)
            //                {
            //                    response.setStatus(404);
            //                    return;
            //                }
            //                Boolean flag = redesCacheManager.get("webmanage.session.cache.name",
            //                        cookieMap.get("SHAREJSESSIONID_MANAGE").getValue());
            //                if (!flag)
            //                {
            //                    response.setStatus(404);
            //                    return;
            //                }
            //  }
            /// else if (rurl.contains("finance-manager")
            ///         || rurl.contains("manage.zijincaifu.net"))
            // {
            //  }
            //  else if (rurl.contains("finance-website")
            //          || rurl.contains("www.zijincaifu.net"))
            //  {
            //  }
            // else
            //  {
            ///     response.setStatus(404);
            //     return;
            //  }
            ServletOutputStream output = response.getOutputStream();
            response.setDateHeader("expries",
                    System.currentTimeMillis() + 1000 * 3600);
            StringBuffer modifyId = new StringBuffer();
            modifyId.append(group);
            modifyId.append("/");
            modifyId.append(st);
            modifyId.append("/");
            modifyId.append(f1);
            modifyId.append("/");
            modifyId.append(f2);
            modifyId.append("/");
            modifyId.append(id);
            modifyId.append("-LastModified");
            SimpleDateFormat dataformat = new SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            dataformat.setTimeZone(new SimpleTimeZone(0, "GMT"));
            
            Object lastModified = HierarchicalCacheManager.get(CacheLevel.REDIS,
                    IFileUpLoad.CACHE_NAME,
                    modifyId.toString());
            if (lastModified == null)
            {
                Date nowdate = new Date();
                lastModified = dataformat.format(nowdate);
                HierarchicalCacheManager.set(CacheLevel.REDIS,
                        IFileUpLoad.CACHE_NAME,
                        modifyId.toString(),
                        lastModified.toString());
                response.addHeader("Last-Modified", lastModified.toString());
            }
            else
            {
                String ifmodify = request.getHeader("If-Modified-Since");
                response.addHeader("Last-Modified", lastModified.toString());
                if (StringUtils.isNotEmpty(ifmodify))
                {
                    Date ifmodifydate = dataformat.parse(ifmodify);
                    Date lastmodifydate = dataformat.parse(lastModified.toString());
                    if (ifmodifydate.getTime() == lastmodifydate.getTime())
                    {
                        // response.setStatus(304);
                        // return;
                    }
                }
            }
            String url = request.getRequestURI();
            String type = url.substring(url.lastIndexOf(".") + 1, url.length());
            // type=type.toLowerCase();
            StringBuffer idbuff = new StringBuffer();
            // idbuff.append(group);
            // idbuff.append("/");
            idbuff.append(st);
            idbuff.append("/");
            idbuff.append(f1);
            idbuff.append("/");
            idbuff.append(f2);
            idbuff.append("/");
            idbuff.append(id);
            id = idbuff.append(".").append(type).toString();
            // id = id.replace("-", "/") + "." + type;
            if (StringUtils.isEmpty(id))
            {
                response.setStatus(404);
                return;
            }
            // id = id.substring("/upload/".length(), id.length());
            int index = id.lastIndexOf(".");
            int index2 = id.indexOf(type);
            if (index2 == index + 1)
            {
                storageClientService.downloadFile(group, id, output);
            }
            else
            {
                String size = id.substring(index2 + type.length(), index);
                id = id.substring(0, index2 + type.length());
                String[] sizes = size.split("[x]");
                int width = Integer.parseInt(sizes[0]);
                int height = Integer.parseInt(sizes[1]);
                if (width > 500 || height > 500)
                {
                    width = 500;
                    height = 500;
                }
                storageClientService.downloadSmallImage(group,
                        id,
                        width,
                        height,
                        output);
            }
            
            type = "image/" + "*";
            //response.setContentType(type);
            // output.write(image);
            output.flush();
            output.close();
            
        }
        catch (Exception e)
        {
            SxjLogger.error("获取图片错误", e, this.getClass());
            
        }
        
    }
    
    private Map<String, String> getHeadersInfo(HttpServletRequest request)
    {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
    
    private static Map<String, Cookie> ReadCookieMap(HttpServletRequest request)
    {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies)
        {
            for (Cookie cookie : cookies)
            {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }
    
    public static void main(String[] args) throws ParseException
    {
        SimpleDateFormat dataf = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dataf.setTimeZone(new SimpleTimeZone(0, "GMT"));
        String ifmodify = "Mon, 29 Aug 2011 07:34:33 GMT";
        long time = System.currentTimeMillis();
        
        System.out.println("time=" + dataf.format(new Date()));
        
        Date date = dataf.parse(ifmodify);
        System.out.println("time2=" + date.getTime());
        ifmodify = ifmodify.substring(ifmodify.indexOf(",") + 1,
                ifmodify.length());
        System.out.println(ifmodify);
        
    }
}
