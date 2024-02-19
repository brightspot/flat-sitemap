package brightspot.sitemap.flat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.psddev.cms.db.PageFilter;
import com.psddev.cms.db.Site;
import com.psddev.dari.db.Query;
import com.psddev.dari.util.AbstractFilter;
import com.psddev.sitemap.SiteMapConfig;
import com.psddev.sitemap.SiteMapType;
import com.psddev.sitemap.SiteMapUtils;
import com.psddev.sitemap.StandardSiteMapType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Forward /sitemap.xml to /flat-sitemap-content.xml and /sitemap.1.xml to /flat-sitemap-content.1.xml, etc.
 * <p>
 * This only occurs if the flat sitemap type is enabled and the standard sitemap type is disabled.
 */
public class FlatSiteMapUrlRewriteFilter extends AbstractFilter implements AbstractFilter.Auto {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlatSiteMapUrlRewriteFilter.class);
    private static final String SITEMAP_FILTER_NAME = "com.psddev.sitemap.SiteMapFilter";
    private static final UUID PHONY_GLOBAL_SITE_ID = UUID.fromString("10000000-0000-0000-0000-000000000001");

    /**
     * 10 second cache to store whether the flat sitemap redirect filter should be enabled for a site.
     * <p>
     * This is defined by the flat sitemap type being enabled and the standard sitemap type being disabled.
     */
    private static final LoadingCache<UUID, Boolean> ENABLED_FOR_SITE_CACHE = Caffeine.newBuilder()
        .expireAfterWrite(10, TimeUnit.SECONDS)
        .build(siteId -> {
            Site site = getSiteFromSiteIdForCache(siteId);
            SiteMapConfig config = SiteMapConfig.getDefault();
            Collection<Class<? extends SiteMapType>> typeClasses = config.getSiteMapTypes(site).stream()
                .map(SiteMapType::getClass)
                .collect(Collectors.toSet());
            return (typeClasses.contains(FlatSiteMapType.class) && !typeClasses.contains(StandardSiteMapType.class));
        });

    // These are required because LoadingCache won't take null for a value.
    private static Site getSiteFromSiteIdForCache(UUID siteId) {
        return siteId.equals(PHONY_GLOBAL_SITE_ID)
            ? null
            : Optional.ofNullable(Query.from(Site.class).where("_id = ?", siteId).first())
                .orElseThrow(() -> new IllegalStateException("No site found for ID: " + siteId));
    }

    private static UUID getSiteIdForCache(HttpServletRequest request) {
        Site site = PageFilter.Static.getSite(request);
        return site == null ? PHONY_GLOBAL_SITE_ID : site.getId();
    }

    private static boolean isEnabledForSite(HttpServletRequest request) {
        UUID siteId = getSiteIdForCache(request);
        return Boolean.TRUE.equals(ENABLED_FOR_SITE_CACHE.get(siteId));
    }

    @Override
    protected void doRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
        String servletPath = request.getServletPath();

        if (servletPath.startsWith("/sitemap")
            && servletPath.endsWith(".xml")
            && isEnabledForSite(request)) {
            chain.doFilter(new FlatSiteMapUrlRewritingHttpServletRequestWrapper(request), response);
        } else {
            super.doRequest(request, response, chain);
        }
    }

    @Override
    public void updateDependencies(Class<? extends AbstractFilter> filterClass, List<Class<? extends Filter>> dependencies) {
        if (filterClass != null && SITEMAP_FILTER_NAME.equals(filterClass.getName())) {
            dependencies.add(getClass());
        }
    }

    /**
     * SiteMapFilter uses getServletPath to determine the sitemap file name. This wrapper changes the servlet path to the flat sitemap filename.
     */
    private static class FlatSiteMapUrlRewritingHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private final HttpServletRequest request;
        private final Site site;

        public FlatSiteMapUrlRewritingHttpServletRequestWrapper(HttpServletRequest request) {
            super((request));
            this.request = request;
            this.site = PageFilter.Static.getSite(request);
        }

        private String transform(String path) {
            String transformedPath = path.replaceAll("^/sitemap", "/flat-sitemap-content");
            if ("/flat-sitemap-content.xml".equals(transformedPath)) {
                // Special case for /sitemap.xml when there are more than one:
                // serve /flat-sitemap-content.1.xml. This is to avoid /sitemap.xml
                // 404ing when the flat sitemap is enabled and the number of
                // items in the sitemap exceeds the limit.

                if (SiteMapUtils.getSiteMap(site, new FlatSiteMapType(), transformedPath.substring(1)) == null) {
                    transformedPath = "/flat-sitemap-content.1.xml";
                }
            }
            return transformedPath;
        }

        @Override
        public String getServletPath() {
            String transformedPath = transform(request.getServletPath());
            LOGGER.info("Transformed path: " + transformedPath);
            return transformedPath;
        }

        @Override
        public String getRequestURI() {
            return transform(request.getRequestURI());
        }
    }
}
