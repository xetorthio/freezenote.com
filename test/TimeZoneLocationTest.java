import java.util.List;

import models.TimeZoneLocation;

import org.junit.Test;

import com.maxmind.geoip.Location;

import play.test.UnitTest;
import services.GeoIP;

public class TimeZoneLocationTest extends UnitTest {

	@Test
	public void shouldSearchCities() {
		List<TimeZoneLocation> locations = TimeZoneLocation.search("buenos");
	
		boolean failed = true;
		for (TimeZoneLocation location : locations) {
			if (location.name.equals("Buenos Aires, Argentina")) {
				failed = false;
				assertNotNull(location.timeZoneId);
				break;
			}
		}
		assertFalse(failed);
	}	
	
	@Test
	public void shouldSearchCountries() {
		List<TimeZoneLocation> locations = TimeZoneLocation.search("Argentin");
		
		boolean failed = true;
		for (TimeZoneLocation location : locations) {
			if (location.name.equals("Argentina")) {
				failed = false;
				break;
			}
		}
		assertFalse(failed);
	}

	@Test
	public void shouldSearchSeveralLocations() {
		List<TimeZoneLocation> locations = TimeZoneLocation.search("b");
		assertTrue(locations.size() > 1);
	}

	@Test
	public void shouldNotRetrieveLocationsThatDoesntExist() {
		List<TimeZoneLocation> locations = TimeZoneLocation.search("bfsde3qrad");
		assertEquals(0, locations.size());
	}
	
	@Test
	public void shouldGetByGeoLocation() {
		Location location = GeoIP.locateCity("186.22.156.151");
		
		TimeZoneLocation tzLocation = TimeZoneLocation.from(location);
		assertEquals("Provincia de Córdoba, Argentina", tzLocation.name);
		assertEquals("America/Argentina/Cordoba", tzLocation.timeZoneId);
	}
}