package run.mone.local.docean.controller;

import com.alibaba.fastjson.JSON;
import com.xiaomi.youpin.docean.anno.Controller;
import com.xiaomi.youpin.docean.anno.RequestMapping;
import com.xiaomi.youpin.docean.anno.RequestParam;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.service.tool.ChromeService;
import run.mone.local.docean.web.WebReq;

import javax.annotation.Resource;
import java.util.List;
/**
 * @author liuchuankang
 * @date 2024/02/27
 */
@Slf4j
@Controller
public class ChromeController {
    @Resource
    private ChromeService chromeService;
    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    @RequestMapping(path = "/chrome/open", method = "get")
    public Boolean open(@RequestParam("openUrl") String openUrl,@RequestParam("title") String title) {
        try {
            log.info("chrome:{}",openUrl);
            return chromeService.open(openUrl);
        } catch (Exception e) {
            log.error("chrome error", e);
        }
        return false;
    }
    @RequestMapping(path = "/chrome/openWithCookie", method = "post")
    public String openWithCookie(@RequestParam("params") WebReq webReq) {
        try {
            log.info("webReq:{}", JSON.toJSONString(webReq));
            return chromeService.openWithCookie(webReq.getOpenUrl(),webReq.getCookies());
        } catch (Exception e) {
            log.error("chrome error", e);
        }
        return null;
    }
    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    @RequestMapping(path = "/chrome/list", method = "get")
    public List<String> list() {
        try {
            return chromeService.list();
        } catch (Exception e) {
            log.error("chrome error", e);
        }
        return null;
    }
    @RequestMapping(path = "/chrome/imageList", method = "get")
    public List<String> getImageList(){
        try {
            return chromeService.getImageList();
        } catch (Exception e) {
            log.error("chrome error", e);
        }
        return null;
    }
    @RequestMapping(path = "/chrome/allText", method = "get")
    public String getAllText(){
        try {
            return chromeService.getAllText();
        } catch (Exception e) {
            log.error("chrome error", e);
        }
        return null;
    }
    @RequestMapping(path = "/chrome/clickText", method = "get")
    public String clickText(@RequestParam("text") String text){
        try {
            return chromeService.clickText(text);
        } catch (Exception e) {
            log.error("chrome error", e);
        }
        return null;
    }
    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    @RequestMapping(path = "/chrome/currentUrl", method = "get")
    public String getCurrentUrl() {
        try {
            return chromeService.getCurrentUrl();
        } catch (Exception e) {
            log.error("chrome error", e);
        }
        return null;
    }
    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    @RequestMapping(path = "/chrome/content", method = "get")
    public String getContent(@RequestParam("title") String title,@RequestParam("url") String url) {
        try {
            return chromeService.getContent(title,url);
        } catch (Exception e) {
            log.error("chrome error", e);
        }
        return null;
    }

    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    @RequestMapping(path = "/chrome/title", method = "get")
    public String getTitile() {
        try {
            return chromeService.getTitle();
        } catch (Exception e) {
            log.error("chrome error", e);
        }
        return null;
    }
    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    @RequestMapping(path = "/chrome/close", method = "get")
    public Boolean close() {
        try {
            return chromeService.close();
        } catch (Exception e) {
            log.error("chrome error", e);
        }
        return false;
    }
    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    @RequestMapping(path = "/chrome/quit", method = "get")
    public Boolean quit() {
        try {
            return chromeService.quit();
        } catch (Exception e) {
            log.error("chrome error", e);
        }
        return false;
    }

}
