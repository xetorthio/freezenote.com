import org.junit.Test;

import play.test.UnitTest;
import services.GeoIP;

import com.maxmind.geoip.Location;

public class GeoIPTest extends UnitTest {
    @Test
    public void locateCity() {
	Location cityLocation = GeoIP.locateCity("186.22.156.151");

	assertNotNull(cityLocation);
	assertEquals(-34, cityLocation.latitude, 0);
	assertEquals(-64, cityLocation.longitude, 0);
    }
}