package fr.labri.app;

import java.io.IOException;
import java.net.URI;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;

import fr.labri.mapper.ExceptionMapper;
import fr.labri.services.StubMessageHandler;
import fr.labri.services.StubMessageHandlerImpl;

/**
 * Main class.
 *
 */
public class Main {
	// Base URI the Grizzly HTTP server will listen on
	public static final String BASE_URI = "http://localhost:8080/myapp/";

	/**
	 * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
	 * application.
	 * 
	 * @return Grizzly HTTP server.
	 */
	public static HttpServer startServer() {
		// create a resource config that scans for JAX-RS resources and
		// providers
		// in com.mirlitone package

		final ResourceConfig rc = new ResourceConfig().packages("fr.labri.endpoints")
				.register(DeclarativeLinkingFeature.class).register(ExceptionMapper.class)
				.registerInstances(new Binder() {

					@Override
					public void bind(DynamicConfiguration config) {
						
						config.bind(BuilderHelper.link(StubMessageHandlerImpl.class).named("composite").to(StubMessageHandler.class).build());
						config.bind(BuilderHelper.link(StubMessageHandlerImpl.class).named("processing").to(StubMessageHandler.class).build());
						config.bind(BuilderHelper.link(StubMessageHandlerImpl.class).named("save").to(StubMessageHandler.class).build());
						config.bind(BuilderHelper.link(StubMessageHandlerImpl.class).named("load").to(StubMessageHandler.class).build());
						config.bind(BuilderHelper.link(StubMessageHandlerImpl.class).named("nextHop").to(StubMessageHandler.class).build());//

					}
				});

		// create and start a new instance of grizzly http server
		// exposing the Jersey application at BASE_URI
		return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		final HttpServer server = startServer();
		System.out.println(String.format(
				"Jersey app started with WADL available at " + "%sapplication.wadl\nHit enter to stop it...",
				BASE_URI));
		System.in.read();
		server.stop();
	}
}
