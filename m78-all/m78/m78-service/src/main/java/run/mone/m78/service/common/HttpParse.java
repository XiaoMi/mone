package run.mone.m78.service.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author goodjava@qq.com
 * @date 2024/1/15 21:03
 */
public class HttpParse {

    //解析osChina的文章内容
    public static String parseOsChina(String url) {
        return parseWithCssQuery(url, ".article-detail");
    }

    //解析FreeBuf文章内容
    public static String parseFreeBuf(String url) {
        return parseWithCssQuery(url, ".content-detail");
    }


    public static String parseWithCssQuery(String url, String cssQuery) {
        String html = HttpClient.get(url);
        Document doc = Jsoup.parse(html);
        Elements divs = doc.select(cssQuery);
        StringBuilder sb = new StringBuilder();
        for (Element div : divs) {
            String allText = div.text();
            sb.append(allText);
        }
        return sb.toString();
    }


}
