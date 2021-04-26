package idscp2client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.camel.spi.CamelContextTracker;
import org.apache.commons.io.IOUtils;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Activator class for getting the Bundle Context
 */
public class Activator implements BundleActivator {
	static private BundleContext bundleContext = null;
	private Logger Logging = LoggerFactory.getLogger(this.getClass());
 private static CamelContextTracker myTracker;
	public static void setBundleContext(BundleContext context) {
		bundleContext = context;
	}

	public static BundleContext getBundleContext() {
		return bundleContext;
	}

	@Override
	public void start(BundleContext context) {
		
		setBundleContext(context);
		String ksPath = "provider-keystore.p12";
		String tsPath = "truststore.p12";
		try {
		URL url = context.getBundle().getEntry(ksPath);
		InputStream is = url.openConnection().getInputStream();
		
		IDSClient.ksFullPath= stream2file(is,"provider-keystore",".p12").getAbsolutePath();
		url = context.getBundle().getEntry(tsPath);
		
		//IDSServer.kpsFullPath= stream2file(is,"consumer-keystore",".p12").getAbsolutePath();
		url = context.getBundle().getEntry(tsPath);
		
		is = url.openConnection().getInputStream();
		IDSClient.tsFullPath=  stream2file(is,"truststore",".p12").getAbsolutePath();
	
		}catch(Exception e) {
			e.printStackTrace();
			Logging.error("failed to get resource file:"+e.getMessage());
		}
			
	}

	@Override
	public void stop(BundleContext context) {
		try {
			myTracker.close();
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Logging.error("Exception:" + e.getMessage());
		}
		
		bundleContext = null;
	
	}
	
	   public static File stream2file (InputStream in, String prefix, String suffix) throws IOException {
	        final File tempFile = File.createTempFile(prefix, suffix);
	        tempFile.deleteOnExit();
	        try (FileOutputStream out = new FileOutputStream(tempFile)) {
	            IOUtils.copy(in, out);
	        }
	        return tempFile;
	    }

}
