/**
 * 
 */
package idscp2client;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.apache.camel.util.jsse.SSLContextParameters;
import org.slf4j.LoggerFactory;

import de.fhg.aisec.ids.idscp2.app_layer.AppLayerConnection;
import de.fhg.aisec.ids.idscp2.default_drivers.daps.aisec_daps.DefaultDapsDriver;
import de.fhg.aisec.ids.idscp2.default_drivers.secure_channel.tlsv1_3.NativeTLSDriver;
import de.fhg.aisec.ids.idscp2.default_drivers.secure_channel.tlsv1_3.NativeTlsConfiguration;
import de.fhg.aisec.ids.idscp2.example.GetClientConnection;
import de.fhg.aisec.ids.idscp2.idscp_core.api.configuration.AttestationConfig;
import de.fhg.aisec.ids.idscp2.idscp_core.api.configuration.AttestationConfig.Builder;
import de.fhg.aisec.ids.idscp2.idscp_core.api.configuration.Idscp2Configuration;
import de.fhg.aisec.ids.idscp2.idscp_core.api.idscp_connection.Idscp2Connection;
import de.fhg.aisec.ids.idscp2.idscp_core.api.idscp_connection.Idscp2ConnectionImpl;
import de.fhg.aisec.ids.idscp2.idscp_core.drivers.DapsDriver;
import de.fhg.aisec.ids.idscp2.idscp_core.drivers.SecureChannelDriver;
import de.fhg.aisec.ids.idscp2.idscp_core.secure_channel.SecureChannel;
import kotlin.jvm.functions.Function2;


/**
 * @author I050368
 *
 */
public class IDSClient {

	   private static SecureChannelDriver<AppLayerConnection, NativeTlsConfiguration> secureChannelDriver;
	   private static Idscp2Configuration clientConfiguration;
	   private static NativeTlsConfiguration secureChannelConfig;

	/**
	 * @param args
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// TODO Auto-generated method stub
			
			try {
		     String host = "34.200.42.174";
	         int port = 29292;

	         String ks_cert_path="C:\\IDS\\trusted-connector\\examples\\cert-stores\\provider-keystore.p12";
	         String ts_cert_path="C:\\IDS\\trusted-connector\\examples\\cert-stores\\truststore.p12";
	         
	         // create attestation config
	         AttestationConfig localAttestationConfig =  (new Builder()).setSupportedRatSuite(new String[]{"Dummy"}).setExpectedRatSuite(new String[]{"Dummy"}).setRatTimeoutDelay(Long.parseLong("3600000")).build();
	         
	        
	        // secure channel config
	         de.fhg.aisec.ids.idscp2.default_drivers.secure_channel.tlsv1_3.NativeTlsConfiguration.Builder secureChannelConfigBuilder = new de.fhg.aisec.ids.idscp2.default_drivers.secure_channel.tlsv1_3.NativeTlsConfiguration.Builder().setHost(host).setServerPort(port);
	                  
	         secureChannelConfigBuilder.setKeyPassword("password".toCharArray());
	         
	         
	         secureChannelConfigBuilder.setKeyStorePath(Paths.get(ks_cert_path));
             
	         secureChannelConfigBuilder.setKeyStoreKeyType("RSA");
             
	         secureChannelConfigBuilder.setKeyStorePassword("password".toCharArray());
	         
	         secureChannelConfigBuilder.setTrustStorePath(Paths.get(ts_cert_path));
	         
	         secureChannelConfigBuilder.setTrustStorePassword("password".toCharArray());

	         secureChannelConfigBuilder.setCertificateAlias("1.0.1");
	         
	         
	         // create daps config builder
	          de.fhg.aisec.ids.idscp2.default_drivers.daps.aisec_daps.DefaultDapsDriverConfig.Builder dapsDriverConfigBuilder = (new de.fhg.aisec.ids.idscp2.default_drivers.daps.aisec_daps.DefaultDapsDriverConfig.Builder()).setDapsUrl("https://daps.aisec.fraunhofer.de").setKeyAlias("1");
	         	        
	          dapsDriverConfigBuilder.setKeyPassword("password".toCharArray()).setKeyStorePath(Paths.get(ks_cert_path)).setKeyStorePassword("password".toCharArray()).setTrustStorePath(Paths.get(ts_cert_path)).setTrustStorePassword("password".toCharArray());       
	          
	         
	          // create idscp configuration
	          clientConfiguration = (new de.fhg.aisec.ids.idscp2.idscp_core.api.configuration.Idscp2Configuration.Builder()).setAttestationConfig(localAttestationConfig).setDapsDriver((DapsDriver)(new DefaultDapsDriver(dapsDriverConfigBuilder.build()))).build();
	          secureChannelDriver = (SecureChannelDriver)(new NativeTLSDriver());
	          secureChannelConfig =  secureChannelConfigBuilder.build();
	         
	          GetClientConnection connF= new GetClientConnection();
	          
	          //Connection object
	           CompletableFuture<Idscp2Connection> connectionFuture =connF.getIDSConnection(clientConfiguration, secureChannelConfig);
	 
		           
	          Idscp2Connection connection = connectionFuture.get();
	     	         
	         //send Message 
	          connection.nonBlockingSend("Hello World".getBytes());
	          	          
	          
			}catch(Exception e) {
				
				e.printStackTrace();
			}
	          
	}

}
