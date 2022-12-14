package curb.server.util;

import curb.core.model.Menu;
import curb.core.util.JsonUtil;
import curb.server.page.CurbPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;


public class CoreDataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreDataUtil.class);

    private CoreDataUtil() {
    }

    public static List<Menu> loadMenus(String path) {
        try (InputStream is = CoreDataUtil.class.getClassLoader().getResourceAsStream(path)) {
            String menuStr = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
            List<Menu> ret = AppMenuUtil.parse(menuStr);
            LOGGER.info("core-data loaded : {}", path);
            return ret;
        } catch (IOException e) {
            throw new MissingResourceException(e.getMessage(), "loadMenus", path);
        }
    }

    public static List<CurbPage> loadPages(String path) {
        try (InputStream is = CoreDataUtil.class.getClassLoader().getResourceAsStream(path)) {
            String str = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
            List<CurbPage> ret = JsonUtil.parseArray(str, CurbPage.class);
            if (ret == null) {
                ret = Collections.emptyList();
            }
            LOGGER.info("core-data loaded : {}", path);
            return ret;
        } catch (IOException e) {
            throw new MissingResourceException(e.getMessage(), "loadPages", path);
        }
    }
}
