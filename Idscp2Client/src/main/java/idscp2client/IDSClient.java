package idscp2client;

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

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;


/**
 * @author I050368
 */
public class IDSClient {

    public static void main(String[] args) {
        try {
            String host = "provider-core";
            int port = 29292;

            String ksPath = "ssl/provider-keystore.p12";
            String tsPath = "ssl/truststore.p12";

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
                    .setDapsUrl("https://daps-dev.aisec.fraunhofer.de")
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

            SecureChannelDriver<Idscp2Connection, NativeTlsConfiguration> secureChannelDriver = new NativeTLSDriver<>();

            //Connection object
            CompletableFuture<Idscp2Connection> connectionFuture = secureChannelDriver.connect(
                    Idscp2ConnectionImpl::new,
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
                connection.nonBlockingSend("PING".getBytes());
            }).exceptionally(t -> {
                System.out.println("Error: Client endpoint error occurred.");
                t.printStackTrace();
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
