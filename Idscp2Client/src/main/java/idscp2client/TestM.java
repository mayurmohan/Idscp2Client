package idscp2client;

import java.net.URI;

import de.fraunhofer.iais.eis.ArtifactRequestMessageBuilder;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
public class TestM {

	public TestM() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
		String uri ="https://example.com/some_artifact";
		Object uriVal= uri;
		ArtifactRequestMessageBuilder builder = new ArtifactRequestMessageBuilder();
		URI requestedArtifact =null;
		 if (uriVal != null) {
			 
			 requestedArtifact = uriVal instanceof URI ? (URI)uriVal : URI.create(uriVal.toString());
		 }
		
	if(requestedArtifact!=null) {
		builder._requestedArtifact_(requestedArtifact);
		}
	Serializer ser = new Serializer();
		
	System.out.println(ser.serialize(builder.build()));
		}catch(Exception e) {
			
			e.printStackTrace();
		}
	}

}
