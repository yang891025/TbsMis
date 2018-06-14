package com.tbs.tbsmis.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import resourcencodeUtil.IdentifyCode;
public class htmlJsoupUtil
{
   // private static final String prefix="http://e.m.tbs.com.cn/Proxy/servlet/";
    public static String modifyLink(String html,String baseUri,String rootUrl) throws IOException{
        Document doc= Jsoup.parse(html,rootUrl);
        Elements elements=doc.select("a[href!=#]");
        htmlJsoupUtil.adsoluteAHref(elements,baseUri,rootUrl);
        Elements jsElements=doc.select("script[src]");
        htmlJsoupUtil.absoluteScriptSrc(jsElements, baseUri, rootUrl);

        Elements sourceElements=doc.select("source[src]");
        htmlJsoupUtil.absoluteScriptSrc(sourceElements, baseUri, rootUrl);

        Elements formElements=doc.select("form[action]");
        htmlJsoupUtil.absoluteFormAction(formElements, baseUri, rootUrl);

        Elements linkElements=doc.select("link[href]");
        htmlJsoupUtil.absoluteLinkHref(linkElements, baseUri, rootUrl);

        Elements dataimagElements=doc.select("img[data-src]");
        htmlJsoupUtil.absoluteDataImagSrc(dataimagElements, baseUri, rootUrl);

        Elements imagElements=doc.select("img[src]");
        htmlJsoupUtil.absoluteImagSrc(imagElements, baseUri, rootUrl);

//        Element base=doc.select("base").first();
//        if(base!=null){
//            //System.out.println(base.attr("href"));
//            base.attr("href", baseUri);}else{
//            Element head=doc.select("head").first();
//            head.append("<base href=\""+baseUri+"\">");
//        }

        return doc.toString();
    }
//
//    public static void modifyCssurl(String urlpath) throws IOException{
//        URL url=new URL("http://localhost:8080/novel/User/register.jsp");//
////		URL url=new URL("http://www.baidu.com/");
//        Document doc=Jsoup.parse(url.openStream(), IdentifyCode.getFileCode(url), "http://sdfsdfsddfBYSJ/");
//        Elements  css=doc.select("[style]");//获取所有含有style属性的元素
//        IteratorElements(css);
//
//        Elements styleCss=doc.select("style");//获得html中的样式标签如<style type="text/css"></style>
//        IteratorStyle(styleCss);
//
//        Elements scriptJs=doc.select("script");//获得<script type="text/javascript"></script>
//        IteratorStyle(scriptJs);
//    }

    //处理<script type="text/javascript">脚本
    public static void IteratorStyle(Elements elements){
        Iterator<Element> iterator=elements.iterator();
        while(iterator.hasNext()){
            Element element=iterator.next();
           // System.out.println(element.data());//获得<script type="text/javascript">中的值
            //<style type="text/css"></style>中的值
        }
    }

    //得到元素中内嵌样式style中的值如<div style="width:100px;">
    public static void IteratorElements(Elements elements){
        Iterator<Element> iterator=elements.iterator();
        while(iterator.hasNext()){
            Element element=iterator.next();
            String style=element.attr("style");
            htmlJsoupUtil.getURL(style);
        }
    }
    //从style中获得 url的值
    public static void getURL(String url){
        Pattern p = Pattern.compile("url\\((.*)\\)");//匹配  url(任何)
        Matcher m = p.matcher(url);
        if(m.find()){
            System.out.println(m.group(1));//获取括号中的地址
        }
    }
    //将Form action 装换为绝对的url
    public static void absoluteFormAction(Elements formElements,String baseUri,String rootUrl){
        Iterator<Element> iterator=formElements.iterator();
        while(iterator.hasNext()){
            Element element=iterator.next();
            String action=element.attr("abs:action");//将所有的相对地址换为绝对地址;
            //添加隐藏域，用来传替url。
            if(htmlJsoupUtil.getWebPath(action).equalsIgnoreCase(htmlJsoupUtil.getWebPath(rootUrl))){
                element.attr("action",baseUri+ htmlJsoupUtil.getRelatePath(action));//装换为
            }
            //element.append("<input type='hidden' name='action' value='"+action+"'/>");
        }
    }

    //将<script src>转换为绝对地址
    public static void absoluteScriptSrc(Elements jsElements,String baseUri,String rootUrl) throws MalformedURLException{
        Iterator<Element> iterator=jsElements.iterator();
        while(iterator.hasNext()){
            Element element=iterator.next();
            String src=element.attr("abs:src");//将所有的相对地址换为绝对地址;
            //if(htmlJsoupUtil.getWebPath(src).equalsIgnoreCase(htmlJsoupUtil.getWebPath(rootUrl))){
                element.attr("src",baseUri+ htmlJsoupUtil.getRelatePath(src));//装换为
           // }
        }
    }

    //将Img src 装换为绝对的url
    public static void absoluteImagSrc(Elements imagElements,String baseUri,String rootUrl) throws MalformedURLException{
        Iterator<Element> iterator=imagElements.iterator();
        while(iterator.hasNext()){
            Element element=iterator.next();
            String src=element.attr("abs:src");//将所有的相对地址换为绝对地址;
            if(htmlJsoupUtil.getWebPath(src).equalsIgnoreCase(htmlJsoupUtil.getWebPath(rootUrl))){
                element.attr("src",baseUri+ htmlJsoupUtil.getRelatePath(src));//装换为
            }
        }
    }
    //将Img src 装换为绝对的url
    public static void absoluteDataImagSrc(Elements imagElements,String baseUri,String rootUrl) throws MalformedURLException{
        Iterator<Element> iterator=imagElements.iterator();
        while(iterator.hasNext()){
            Element element=iterator.next();
            String src=element.attr("abs:data-src");//将所有的相对地址换为绝对地址;
            //if(getWebPath(src).equalsIgnoreCase(getWebPath(rootUrl))){
                element.attr("src",src);//装换为
           // }
        }
    }
    //将Link href 装换为绝对的url
    public static void absoluteLinkHref(Elements linkElements,String baseUri,String rootUrl) throws MalformedURLException{
        Iterator<Element> iterator=linkElements.iterator();
        while(iterator.hasNext()){
            Element element=iterator.next();
            String src=element.attr("abs:href");//将所有的相对地址换为绝对地址;
            if(htmlJsoupUtil.getWebPath(src).equalsIgnoreCase(htmlJsoupUtil.getWebPath(rootUrl))){
                element.attr("href",baseUri+ htmlJsoupUtil.getRelatePath(src));//装换为
            }
            //			element.attr("charset",IdentifyCode.getFileCode(src));//设置外部文件的编码
        }
    }
    //将所有的的<a href>转换为绝对地址
    public static void adsoluteAHref(Elements AElements,String baseUri,String rootUrl){
        Iterator<Element> iterator=AElements.iterator();
        while(iterator.hasNext()){
            Element element=iterator.next();
            String href=element.attr("abs:href");//将所有的相对地址换为绝对地址;
//            System.out.println("href=" + href);
//            System.out.println("getWebPath(href)=" +getWebPath(href));
//            System.out.println("rootUrl=" +rootUrl);
//            System.out.println("getWebPath(href).equalsIgnoreCase(rootUrl)=" +(getWebPath(href).equalsIgnoreCase(getWebPath(rootUrl))));
            if(href != ""){
                if(htmlJsoupUtil.getWebPath(href).equalsIgnoreCase(htmlJsoupUtil.getWebPath(rootUrl))){
                    element.attr("href",baseUri+ htmlJsoupUtil.getRelatePath(href));//装换为
                }
            }
        }
    }
    private static String getRelatePath(String srcPath) {
        if (srcPath.contains("?"))
            srcPath = srcPath.substring(0, srcPath.indexOf("?"));
        if (srcPath.contains("/"))
            srcPath = srcPath.substring(srcPath.lastIndexOf("/") + 1);
        return "src/"+srcPath;
    }
    private static String getWebPath(String srcPath) {
        if (srcPath.contains("?"))
            srcPath = srcPath.substring(0, srcPath.indexOf("?"));
        if (srcPath.contains("//"))
            srcPath = srcPath.substring(srcPath.indexOf("//") + 2);
        if (srcPath.contains("/"))
            srcPath = srcPath.substring(0, srcPath.indexOf("/"));
        return "http://" + srcPath;
    }
//    public static void main(String args[]) throws ParseException, IOException{
//	    HttpClient httpClient=TestCookie.GetHttpClient();
//		String htmlpage=TestCookie.getPageByGet(httpClient, "http://e.tbs.com.cn:2011/tbsnews/page/BrowInfo4.cbs?ResName=sqpzsjk&no=499&order=499&ResultFile=%2Fhome%2Ftbs%2Ftbs_soft%2FTbsNews%2Fdata%2Fsqpzsjk%2Fsqpzsjk.tbf&indexval=");
//		System.out.println(modifyLink(htmlpage, "http://localhost:8080/Proxy"));
//       // modifyCssurl("http://www.hao123.com/?tn=98784002_hao_pg");
//    }
}