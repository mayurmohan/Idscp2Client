/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package idscp2client;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The www.Sample.com producer.
 */
public class Idscp2ClientProducer extends DefaultProducer {
    private static final transient Logger LOG = LoggerFactory.getLogger(Idscp2ClientProducer.class);
    private Idscp2ClientEndpoint endpoint;

	public Idscp2ClientProducer(Idscp2ClientEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
    }

    public void process(final Exchange exchange) throws Exception {
    	
        String input = exchange.getIn().getBody(String.class);
		String greetingMessage = endpoint.getGreetingsMessage();
		if(greetingMessage == null || greetingMessage.isEmpty()) {
			greetingMessage = "Hello!";
		}
		
		String header ="";
		
		if(exchange.getIn().getHeader("IDS_MSG")!=null) {
			header= exchange.getIn().getHeader("IDS_MSG").toString();
		}
		
		if(header == "") {
			header = "IDS_MSG";
		}
		
		IDSClient.sendMessageToIDS(greetingMessage,header);
		String messageInUpperCase = greetingMessage.toUpperCase();
		if (input != null) {
		    LOG.debug(input);
			messageInUpperCase = input + " : " + messageInUpperCase;
		}
		exchange.getIn().setBody(messageInUpperCase);
    }

}
