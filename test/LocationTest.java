import java.util.List;

import models.Location;

import org.junit.Test;

import play.test.UnitTest;

public class LocationTest extends UnitTest {

	@Test
	public void shouldSearchCities() {
		List<Location> locations = Location.search("buenos");
	
		boolean failed = true;
		for (Location location : locations) {
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
		List<Location> locations = Location.search("Argentin");
		
		boolean failed = true;
		for (Location location : locations) {
			if (location.name.equals("Argentina")) {
				failed = false;
				break;
			}
		}
		assertFalse(failed);
	}

	@Test
	public void shouldSearchSeveralLocations() {
		List<Location> locations = Location.search("b");
		assertTrue(locations.size() > 1);
	}

	@Test
	public void shouldNotRetrieveLocationsThatDoesntExist() {
		List<Location> locations = Location.search("bfsde3qrad");
		assertEquals(0, locations.size());
	}
}