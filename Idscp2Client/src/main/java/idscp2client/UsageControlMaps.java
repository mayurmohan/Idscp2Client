package idscp2client;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jsonldjava.shaded.com.google.common.collect.MapMaker;

import de.fhg.aisec.ids.idscp2.app_layer.AppLayerConnection;
import de.fhg.aisec.ids.idscp2.app_layer.Utils;
import de.fraunhofer.iais.eis.ContractAgreement;
import kotlin.jvm.internal.Intrinsics;

public final class UsageControlMaps {
	   private static final Logger LOG;
	   private static final Map contractMap;
	   private static final Map exchangeConnectionMap;
	   private static final Map connectionContracts;
	   private static final Map protectedBodies;
	   public static final UsageControlMaps INSTANCE;

	   public final ContractAgreement getExchangeContract(Exchange exchange) throws Throwable {
	      Intrinsics.checkNotNullParameter(exchange, "exchange");
	      AppLayerConnection var10000 = (AppLayerConnection)exchangeConnectionMap.get(exchange);
	      ContractAgreement var13;
	      if (var10000 != null) {
	         AppLayerConnection var2 = var10000;
	         boolean var3 = false;
	         boolean var4 = false;
	         URI var12 = (URI)connectionContracts.get(var2);
	         if (var12 != null) {
	            URI var7 = var12;
	            boolean var8 = false;
	            boolean var9 = false;
	            var13 = (ContractAgreement)contractMap.get(var7);
	           if (var13 == null) {
	               throw (Throwable)(new RuntimeException("Contract " + var7 + " is not available!"));
	            }
	         } else {
	            var13 = null;
	         }
	      } else {
	         var13 = null;
	      }

	      return var13;
	   }

	   public final void protectBody(Exchange exchange, URI contractUri) {
	      Intrinsics.checkNotNullParameter(exchange, "exchange");
	      Intrinsics.checkNotNullParameter(contractUri, "contractUri");
	      Map var10000 = protectedBodies;
	      Message var10002 = exchange.getMessage();
	      Intrinsics.checkNotNullExpressionValue(var10002, "exchange.message");
	      Object var4 = var10002.getBody();
	      Intrinsics.checkNotNullExpressionValue(var4, "exchange.message.body");
	      var10000.put(exchange, var4);
	      Message var3 = exchange.getMessage();
	      Intrinsics.checkNotNullExpressionValue(var3, "exchange.message");
	      var3.setBody("### Usage control protected body, contract " + contractUri + " ###");
	   }

	   public final boolean isProtected(Exchange exchange) {
	      Intrinsics.checkNotNullParameter(exchange, "exchange");
	      return protectedBodies.containsKey(exchange);
	   }

	   public final void unprotectBody(Exchange exchange) {
	      Intrinsics.checkNotNullParameter(exchange, "exchange");
	      Message var10000 = exchange.getMessage();
	      Intrinsics.checkNotNullExpressionValue(var10000, "exchange.message");
	      var10000.setBody(protectedBodies.get(exchange));
	      Map var2 = protectedBodies;
	      boolean var3 = false;
	      var2.remove(exchange);
	   }

	   public final void addContractAgreement(ContractAgreement contractAgreement) {
	      Intrinsics.checkNotNullParameter(contractAgreement, "contractAgreement");
	      Map var10000 = contractMap;
	      URI var10001 = contractAgreement.getId();
	      Intrinsics.checkNotNullExpressionValue(var10001, "contractAgreement.id");
	      var10000.put(var10001, contractAgreement);
	   }

	   public final void setConnectionContract( AppLayerConnection connection, URI contractUri) {
	      Intrinsics.checkNotNullParameter(connection, "connection");
	      Logger var10000;
	      if (contractUri != null) {
	         connectionContracts.put(connection, contractUri);
	         var10000 = LOG;
	         Intrinsics.checkNotNullExpressionValue(var10000, "LOG");
	         if (var10000.isDebugEnabled()) {
	            LOG.debug("UC: Assigned contract " + contractUri + " to connection " + connection);
	         }
	      } else {
	         Map var3 = connectionContracts;
	         boolean var4 = false;
	         var3.remove(connection);
	         var10000 = LOG;
	         Intrinsics.checkNotNullExpressionValue(var10000, "LOG");
	         if (var10000.isDebugEnabled()) {
	            LOG.debug("UC: Assigned no contract to connection " + connection);
	         }
	      }

	   }

	   public final void setExchangeConnection( Exchange exchange, AppLayerConnection connection) {
	      Intrinsics.checkNotNullParameter(exchange, "exchange");
	      Intrinsics.checkNotNullParameter(connection, "connection");
	      exchangeConnectionMap.put(exchange, connection);
	      Logger var10000 = LOG;
	      Intrinsics.checkNotNullExpressionValue(var10000, "LOG");
	      if (var10000.isDebugEnabled()) {
	         LOG.debug("UC: Assigned exchange " + exchange + " to connection " + connection);
	      }

	   }

	   private UsageControlMaps() {
	   }

	   static {
	      UsageControlMaps var0 = new UsageControlMaps();
	      INSTANCE = var0;
	      LOG = LoggerFactory.getLogger(Utils.class);
	      ConcurrentMap var10000 = (new MapMaker()).makeMap();
	      Intrinsics.checkNotNullExpressionValue(var10000, "MapMaker().makeMap()");
	      contractMap = (Map)var10000;
	      var10000 = (new MapMaker()).weakKeys().makeMap();
	      Intrinsics.checkNotNullExpressionValue(var10000, "MapMaker().weakKeys().makeMap()");
	      exchangeConnectionMap = (Map)var10000;
	      var10000 = (new MapMaker()).weakKeys().makeMap();
	      Intrinsics.checkNotNullExpressionValue(var10000, "MapMaker().weakKeys().makeMap()");
	      connectionContracts = (Map)var10000;
	      var10000 = (new MapMaker()).weakKeys().makeMap();
	      Intrinsics.checkNotNullExpressionValue(var10000, "MapMaker().weakKeys().makeMap()");
	      protectedBodies = (Map)var10000;
	   }
	   
}