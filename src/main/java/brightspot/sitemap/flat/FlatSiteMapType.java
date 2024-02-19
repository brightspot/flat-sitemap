package brightspot.sitemap.flat;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.psddev.cms.db.Site;
import com.psddev.sitemap.PartitionStrategy;
import com.psddev.sitemap.SinglePartitionStrategy;
import com.psddev.sitemap.SiteMapEntry;
import com.psddev.sitemap.SiteMapItem;
import com.psddev.sitemap.SiteMapType;

/**
 * A {@link SiteMapType} that represents a flat site map.
 * <p>
 * The sitemap URLs will be /flat-sitemap-content.xml and /flat-sitemap-content.1.xml,
 * etc., unless Flat Site Map is enabled and Standard Site Map is disabled, in
 * which case the URLs will be /sitemap.xml, /sitemap.1.xml, etc.
 * <p>
 * Contents are updated every 6 hours.
 */
public class FlatSiteMapType implements SiteMapType<SiteMapItem> {

    @Override
    public String getPrefix() {
        return "flat-";
    }

    @Override
    public Class<SiteMapItem> getSiteMapItemClass() {
        return SiteMapItem.class;
    }

    @Override
    public List<SiteMapEntry> getEntries(SiteMapItem item, Site site) {
        return item.getSiteMapEntries(site);
    }

    @Override
    public PartitionStrategy getPartitionStrategy() {
        return new SinglePartitionStrategy();
    }

    @Override
    public int getDefaultMaximumContentAgeDays() {
        // Days since epoch is close enough to "forever" for our purpose
        //noinspection NumericCastThatLosesPrecision
        return (int) ChronoUnit.DAYS.between(LocalDate.ofEpochDay(0), LocalDate.now());
    }

    @Override
    public int getDefaultMaximumLatestContentAgeDays() {
        // Latest is not needed for flat sitemap.
        return 0;
    }
}
