import java.util.TimeZone;

import models.GeoLocation;

import org.junit.Test;

import play.test.UnitTest;


public class GeoLocationTest extends UnitTest{
    
    @Test
    public void shouldRetrieveTimeZone() {
        GeoLocation location = new GeoLocation(-34.6131488101425, -58.3772277832031);
        TimeZone timeZone = location.getTimeZone();
        assertEquals("America/Argentina/Buenos_Aires", timeZone.getID());
    }
    
    @Test
    public void parse(){
        GeoLocation location = GeoLocation.parse("34.0|55.0");
        assertEquals(34d, location.latitude.doubleValue(),0);
        assertEquals(55d, location.longitude.doubleValue(),0);
    }
    
    @Test
    public void serializeToString(){
        String id = new GeoLocation(34,55).toString();
        assertEquals("34.0|55.0", id);
    }
}
