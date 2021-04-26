package idscp2client;

import de.fhg.aisec.ids.idscp2.app_layer.AppLayerConnection;
import de.fhg.aisec.ids.idscp2.default_drivers.daps.aisec_daps.AisecDapsDriver;
import de.fhg.aisec.ids.idscp2.default_drivers.daps.aisec_daps.AisecDapsDriverConfig;
import de.fhg.aisec.ids.idscp2.default_drivers.rat.dummy.RatProverDummy;
import de.fhg.aisec.ids.idscp2.default_drivers.rat.dummy.RatVerifierDummy;
import de.fhg.aisec.ids.idscp2.default_drivers.secure_channel.tlsv1_3.NativeTLSDriver;
import de.fhg.aisec.ids.idscp2.default_drivers.secure_channel.tlsv1_3.NativeTlsConfiguration;
import de.fhg.aisec.ids.idscp2.idscp_core.api.configuration.AttestationConfig;
import de.fhg.aisec.ids.idscp2.idscp_core.api.configuration.Idscp2Configuration;
import de.fhg.aisec.ids.idscp2.idscp_core.api.idscp_connection.Idscp2Connection;
import de.fhg.aisec.ids.idscp2.idscp_core.api.idscp_connection.Idscp2ConnectionAdapter;
import de.fhg.aisec.ids.idscp2.idscp_core.api.idscp_connection.Idscp2ConnectionImpl;
import de.fhg.aisec.ids.idscp2.idscp_core.drivers.SecureChannelDriver;
import de.fhg.aisec.ids.idscp2.idscp_core.rat_registry.RatProverDriverRegistry;
import de.fhg.aisec.ids.idscp2.idscp_core.rat_registry.RatVerifierDriverRegistry;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author I050368
 */
public class IDSClient {

    private static Logger LOG = LoggerFactory.getLogger(IDSClient.class);
    public  static String ksFullPath ="";
  
    public  static String tsFullPath ="";
    public static void main(String[] args) {
        try {
            String host = "consumer-core";
            int port = 29292;
            String ksPath = "C:\\IDS\\certs\\provider-keystore.p12";
            //ksFullPath = IDSClient.class.getResource(ksPath).getPath();
    
            String tsPath = "C:\\IDS\\certs\\truststore.p12";
            
            //tsFullPath = IDSClient.class.getResource(tsPath).getPath();

            // register rat drivers
            RatProverDriverRegistry.INSTANCE.registerDriver(
                    RatProverDummy.RAT_PROVER_DUMMY_ID, RatProverDummy::new, null
            );
            RatVerifierDriverRegistry.INSTANCE.registerDriver(
                    RatVerifierDummy.RAT_VERIFIER_DUMMY_ID, RatVerifierDummy::new, null
            );

            // create attestation config
            AttestationConfig localAttestationConfig = (new AttestationConfig.Builder())
                    .setSupportedRatSuite(new String[]{RatProverDummy.RAT_PROVER_DUMMY_ID})
                    .setExpectedRatSuite(new String[]{RatVerifierDummy.RAT_VERIFIER_DUMMY_ID})
                    .setRatTimeoutDelay(Long.parseLong("3600000"))
                    .build();

            // secure channel config
            NativeTlsConfiguration secureChannelConfig = (new NativeTlsConfiguration.Builder())
                    .setHost(host)
                    .setServerPort(port)
                    .setKeyPassword("password".toCharArray())
                    .setKeyStorePath(Paths.get(ksPath))
                    .setKeyStoreKeyType("RSA")
                    .setKeyStorePassword("password".toCharArray())
                    .setTrustStorePath(Paths.get(tsPath))
                    .setTrustStorePassword("password".toCharArray())
                    .setCertificateAlias("1.0.1")
                    .build();


            // create daps config
            AisecDapsDriverConfig dapsDriverConfig = (new AisecDapsDriverConfig.Builder())
                    .setDapsUrl("https://daps.aisec.fraunhofer.de")
                    .setKeyAlias("1")
                    .setKeyPassword("password".toCharArray())
                    .setKeyStorePath(Paths.get(ksPath))
                    .setKeyStorePassword("password".toCharArray())
                    .setTrustStorePath(Paths.get(tsPath))
                    .setTrustStorePassword("password".toCharArray())
                    .build();


            // create idscp configuration
            Idscp2Configuration clientConfiguration = (new Idscp2Configuration.Builder())
                    .setAttestationConfig(localAttestationConfig)
                    .setDapsDriver(new AisecDapsDriver(dapsDriverConfig))
                    .build();

            SecureChannelDriver<AppLayerConnection, NativeTlsConfiguration> secureChannelDriver = new NativeTLSDriver<>();

            //Connection object
            CompletableFuture<AppLayerConnection> connectionFuture = secureChannelDriver.connect(
            		AppLayerConnection::new,
                    clientConfiguration,
                    secureChannelConfig
            );

            connectionFuture.thenAccept(connection -> {
                System.out.println("Client: New connection with id " + connection.getId());
                connection.addConnectionListener(new Idscp2ConnectionAdapter() {
                    @Override
                    public void onError(@SuppressWarnings("NullableProblems") Throwable t) {
                        System.out.println("Error: Client connection error occurred");
                        t.printStackTrace();
                    }

                    @Override
                    public void onClose() {
                        System.out.println("Client: Connection with id " + connection.getId() + " has been closed");
                    }
                });
                connection.addMessageListener((c, data) -> {
                    System.out.println("Received ping message: " + new String(data, StandardCharsets.UTF_8));
                    c.close();
                });
                connection.unlockMessaging();
                System.out.println("Send PING ...");
                //send Message
                connection.sendGenericMessage("IDS-HEADER","HELLOWORLD".getBytes());
            }).exceptionally(t -> {
                System.out.println("Error: Client endpoint error occurred.");
                t.printStackTrace();
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    
    public static void sendMessageToIDS(String message, String header) {
    	
        try {
        	
            String host = "34.200.42.174";
           // host = "consumer-core";
            int port = 29292;
            String ksPath = ksFullPath; 
             
            String tsPath = tsFullPath;
            // register rat drivers
            RatProverDriverRegistry.INSTANCE.registerDriver(
                    RatProverDummy.RAT_PROVER_DUMMY_ID, RatProverDummy::new, null
            );
            RatVerifierDriverRegistry.INSTANCE.registerDriver(
                    RatVerifierDummy.RAT_VERIFIER_DUMMY_ID, RatVerifierDummy::new, null
            );

            // create attestation config
            AttestationConfig localAttestationConfig = (new AttestationConfig.Builder())
                    .setSupportedRatSuite(new String[]{RatProverDummy.RAT_PROVER_DUMMY_ID})
                    .setExpectedRatSuite(new String[]{RatVerifierDummy.RAT_VERIFIER_DUMMY_ID})
                    .setRatTimeoutDelay(Long.parseLong("3600000"))
                    .build();

            // secure channel config
            NativeTlsConfiguration secureChannelConfig = (new NativeTlsConfiguration.Builder())
                    .setHost(host)
                    .setServerPort(port)
                    .setKeyPassword("password".toCharArray())
                    .setKeyStorePath(Paths.get(ksPath))
                    .setKeyStoreKeyType("RSA")
                    .setKeyStorePassword("password".toCharArray())
                    .setTrustStorePath(Paths.get(tsPath))
                    .setTrustStorePassword("password".toCharArray())
                    .setCertificateAlias("1.0.1")
                    .build();


            // create daps config
            AisecDapsDriverConfig dapsDriverConfig = (new AisecDapsDriverConfig.Builder())
                    .setDapsUrl("https://daps.aisec.fraunhofer.de")
                    .setKeyAlias("1")
                    .setKeyPassword("password".toCharArray())
                    .setKeyStorePath(Paths.get(ksPath))
                    .setKeyStorePassword("password".toCharArray())
                    .setTrustStorePath(Paths.get(tsPath))
                    .setTrustStorePassword("password".toCharArray())
                    .build();


            // create idscp configuration
            Idscp2Configuration clientConfiguration = (new Idscp2Configuration.Builder())
                    .setAttestationConfig(localAttestationConfig)
                    .setDapsDriver(new AisecDapsDriver(dapsDriverConfig))
                    .build();

            SecureChannelDriver<AppLayerConnection, NativeTlsConfiguration> secureChannelDriver = new NativeTLSDriver<>();

            //Connection object
            CompletableFuture<AppLayerConnection> connectionFuture = secureChannelDriver.connect(
            		AppLayerConnection::new,
                    clientConfiguration,
                    secureChannelConfig
            );


            connectionFuture.thenAccept(connection -> {
            	LOG.info("Client: New connection with id " + connection.getId());
                connection.addConnectionListener(new Idscp2ConnectionAdapter() {
                    @Override
                    public void onError(@SuppressWarnings("NullableProblems") Throwable t) {
                    	LOG.error("Error: Client connection error occurred," + t.getMessage());
                        t.printStackTrace();
                    }

                    @Override
                    public void onClose() {
                    	LOG.warn("Client: Connection with id " + connection.getId() + " has been closed");
                    }
                });
                connection.addMessageListener((c, data) -> {
                	LOG.info("Received message: " + new String(data, StandardCharsets.UTF_8));
                    c.close();
                });
                connection.unlockMessaging();
                LOG.info("Send message ...");
                //send Message
                connection.sendGenericMessage(header,message.getBytes());
            }).exceptionally(t -> {
            	LOG.error("Error: Client endpoint error occurred." + t.getMessage());
                t.printStackTrace();
                return null;
            });
        } catch (Exception e) {
        	LOG.error("Failed to send message:" + e.getMessage());
            e.printStackTrace();
        }
    }
}
