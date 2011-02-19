package services;

import java.io.IOException;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

public class GeoIP {
    public static Location locateCity(String ip) {
	Location city = null;
	try {
	    LookupService cl = new LookupService(play.Play.getFile(
		    "lib/GeoLiteCity.dat").getAbsolutePath(),
		    LookupService.GEOIP_MEMORY_CACHE);
	    city = cl.getLocation(ip);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return city;
    }
}
