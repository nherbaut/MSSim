package fr.pantheonsorbonne.cri.endpoints;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;

import fr.pantheonsorbonne.cri.model.StubMessage;
import fr.pantheonsorbonne.cri.services.JMSUtils;
import fr.pantheonsorbonne.cri.services.StubMessageHandlerBuilder;

public abstract class JMSEndoint {

	public static void onMessageReceived(Message incomingMessage) {

		ThreadResources.executor.submit(() -> {

			try {
				TextMessage txtMessage = (TextMessage) incomingMessage;
				StringReader reader = new StringReader(txtMessage.getText());
				StubMessage message = (StubMessage) JMSUtils.getJaxbContext().createUnmarshaller().unmarshal(reader);
				String id = txtMessage.getStringProperty("identifier");
				StubMessageHandlerBuilder.of(message, message.getNodeFromId(id)).handleStubMessage();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		});

	}

}
