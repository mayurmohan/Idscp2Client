/**
 * 
 */
package idscp2client;

import java.net.URI;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;

/**
 * @author I050368
 *
 */
public class MyArtifactRequestCreationProcessor implements Processor{

	/**
	 * 
	 */
	public MyArtifactRequestCreationProcessor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		
		Object uriVal= exchange.getProperty("artifactUri");
		ArtifactRequestMessageBuilder builder = new ArtifactRequestMessageBuilder();
		URI requestedArtifact =null;
		 if (uriVal != null) {
			 
			 requestedArtifact = uriVal instanceof URI ? (URI)uriVal : URI.create(uriVal.toString());
		 }
		
	if(requestedArtifact!=null) {
		builder._requestedArtifact_(requestedArtifact);
		Serializer ser = new Serializer();
		exchange.getMessage().setHeader("idscp2-header",ser.serialize(builder.build()));
	}
	}

}
