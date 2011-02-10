
import java.util.List;

import models.Location;

import org.junit.Test;

import play.test.UnitTest;


public class LocationTest extends UnitTest{
    
   // @Test
   // public void shouldSearchCities(){
   //     List<Location> locations = Location.search("buenos ai");
   //     assertEquals(1, locations.size());
   //     Location location = locations.get(0);
   //     assertEquals("Buenos Aires", location.name);
   //     assertTrue(location.id != "");
   //     assertNotNull(location.id);
   // }
    
    @Test
    public void shouldSearchSeveralLocations(){
        List<Location> locations = Location.search("b");
        assertTrue(locations.size() > 1);
    }
    
    @Test
    public void shouldNotRetrieveLocationsThatDoesntExist(){
        List<Location> locations = Location.search("bfsde3qrad");
        assertEquals(0, locations.size());
    }
}