package fr.pantheonsorbonne.cri.services;

import java.io.StringWriter;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pantheonsorbonne.cri.model.Node;
import fr.pantheonsorbonne.cri.model.StubMessage;

public class RestClientStubMessageHandler extends StubMessageHandlerImpl {

	public RestClientStubMessageHandler(StubMessage message, String nodeIdentifier) {
		super(message, nodeIdentifier);

	}

	private final Client client = ClientBuilder.newClient();
	private static final Logger LOGGER = LoggerFactory.getLogger(RestClientStubMessageHandler.class);

	@Override
	public void handleStubMessage() {

		Node nextNode = this.getMessage().firstNext(this.getMessage().getNodeFromId(this.getNodeIdentifier()));

		WebTarget target = client.target(UriBuilder.fromUri(nextNode.getUrl()).path("rest").path(nextNode.getId()).build());
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_XML);
		
		StringWriter writer =new StringWriter();
		try {
			JAXBContext.newInstance(StubMessage.class).createMarshaller().marshal(this.getMessage(), writer);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(writer.toString());
		Response response = invocationBuilder.post(Entity.entity(this.getMessage(), MediaType.APPLICATION_JSON));
		if (response.getStatus() != 200) {
			System.out.println(response.getHeaders());
			System.out.println(response.getStatusInfo());
			System.out.println(response.readEntity(String.class));
			throw new RuntimeException("response is " + response.getStatus());
		}

	}

}
