package idscp2client;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import de.fraunhofer.iais.eis.ArtifactRequestMessage;
import de.fraunhofer.iais.eis.ArtifactResponseMessage;
import de.fraunhofer.iais.eis.ContractAgreementMessage;
import de.fraunhofer.iais.eis.ContractOfferMessage;
import de.fraunhofer.iais.eis.ContractRejectionMessage;
import de.fraunhofer.iais.eis.ContractRequestMessage;
import de.fraunhofer.iais.eis.ContractResponseMessage;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.RejectionMessage;
import de.fraunhofer.iais.eis.ResourceUpdateMessage;
import kotlin.jvm.internal.Reflection;

public class MyIdsMessageTypeExtractionProcessor implements Processor {

	public MyIdsMessageTypeExtractionProcessor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		Message message = (Message)exchange.getMessage().getHeader("idscp2-header", Message.class);
		
		if(message!=null){
			
			  String messageType = message instanceof ArtifactRequestMessage ? Reflection.getOrCreateKotlinClass(ArtifactRequestMessage.class).getSimpleName() : (message instanceof ArtifactResponseMessage ? Reflection.getOrCreateKotlinClass(ArtifactResponseMessage.class).getSimpleName() : (message instanceof ContractRequestMessage ? Reflection.getOrCreateKotlinClass(ContractRequestMessage.class).getSimpleName() : (message instanceof ContractResponseMessage ? Reflection.getOrCreateKotlinClass(ContractResponseMessage.class).getSimpleName() : (message instanceof ContractOfferMessage ? Reflection.getOrCreateKotlinClass(ContractOfferMessage.class).getSimpleName() : (message instanceof ContractAgreementMessage ? Reflection.getOrCreateKotlinClass(ContractAgreementMessage.class).getSimpleName() : (message instanceof ContractRejectionMessage ? Reflection.getOrCreateKotlinClass(ContractRejectionMessage.class).getSimpleName() : (message instanceof ResourceUpdateMessage ? Reflection.getOrCreateKotlinClass(ResourceUpdateMessage.class).getSimpleName() : (message instanceof RejectionMessage ? Reflection.getOrCreateKotlinClass(RejectionMessage.class).getSimpleName() : Reflection.getOrCreateKotlinClass(message.getClass()).getSimpleName()))))))));
			 exchange.setProperty("ids-type", messageType);
			
		}
	}

}
