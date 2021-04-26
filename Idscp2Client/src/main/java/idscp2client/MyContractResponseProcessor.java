/**
 * 
 */
package idscp2client;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;

import de.fhg.aisec.ids.idscp2.app_layer.Utils;
import de.fraunhofer.iais.eis.ContractAgreement;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractAgreementMessageBuilder;
import de.fraunhofer.iais.eis.ContractOffer;
import de.fraunhofer.iais.eis.ContractResponseMessage;

/**
 * @author I050368
 *
 */
public class MyContractResponseProcessor implements Processor{

	/**
	 * 
	 */
	public MyContractResponseProcessor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		
		ContractResponseMessage contractResponseMessage = (ContractResponseMessage)exchange.getMessage().getHeader("idscp2-header", ContractResponseMessage.class);
		
	    ContractOffer contractOfferReceived = (ContractOffer)Utils.INSTANCE.getSERIALIZER().deserialize((String)exchange.getMessage().getBody(String.class), ContractOffer.class);
		   
	    ContractAgreementMessageBuilder contractAgreementMessageBuilder = new ContractAgreementMessageBuilder();
	    contractAgreementMessageBuilder._correlationMessage_(contractResponseMessage.getId());
	    
	    exchange.getMessage().setHeader("idscp2-header", contractAgreementMessageBuilder);
	    
	    
	    ContractAgreementBuilder contractAgreementBuilder = new ContractAgreementBuilder();
	    
	    ContractAgreement contractAgreement = contractAgreementBuilder._consumer_(contractOfferReceived.getConsumer())._provider_(contractOfferReceived.getProvider())._contractAnnex_(contractOfferReceived.getContractAnnex())._contractDate_(contractOfferReceived.getContractDate())._contractDocument_(contractOfferReceived.getContractDocument())._contractEnd_(contractOfferReceived.getContractEnd())._contractStart_(contractOfferReceived.getContractStart())._obligation_(contractOfferReceived.getObligation())._prohibition_(contractOfferReceived.getProhibition())._permission_(contractOfferReceived.getPermission()).build();
	    
	    UsageControlMaps usageControlMaps = UsageControlMaps.INSTANCE;
	    
	    usageControlMaps.addContractAgreement(contractAgreement);
	    
	    String var12 = Utils.INSTANCE.getSERIALIZER().serialize(contractAgreement);
	    
	    Message var16 = exchange.getMessage();
	    var16.setBody(var12);
	}

}
