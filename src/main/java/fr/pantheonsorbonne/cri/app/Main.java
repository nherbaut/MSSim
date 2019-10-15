package fr.pantheonsorbonne.cri.app;

import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.utilities.Binder;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.moxy.internal.MoxyFilteringFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.moxy.json.internal.MoxyJsonAutoDiscoverable;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.bridge.SLF4JBridgeHandler;

import fr.pantheonsorbonne.cri.mapper.ExceptionMapper;
import fr.pantheonsorbonne.cri.services.DummyStubMessageHandler;
import fr.pantheonsorbonne.cri.services.ParallelStubMessageHandler;
import fr.pantheonsorbonne.cri.services.RestClientStubMessageHandler;
import fr.pantheonsorbonne.cri.services.StubMessageHandler;
import fr.pantheonsorbonne.cri.services.StubMessageHandlerImpl;

/**
 * Main class.
 *
 */
public class Main {
	// Base URI the Grizzly HTTP server will listen on
	public static final String BASE_URI = "http://0.0.0.0:8080";

	/**
	 * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
	 * application.
	 * 
	 * @return Grizzly HTTP server.
	 */
	public static HttpServer startServer(String nodeIdentifier) {
		// create a resource config that scans for JAX-RS resources and
		// providers
		// in com.mirlitone package

		final ResourceConfig rc = new ResourceConfig().packages("fr.labri.endpoints")//
				.property("nodeIdentifier", nodeIdentifier)//
				.register(JacksonFeature.class)//
				.register(DeclarativeLinkingFeature.class)//
				.register(ExceptionMapper.class)//
				.register(new Binder() {

					@Override
					public void bind(DynamicConfiguration config) {

						config.bind(BuilderHelper.link(StubMessageHandlerImpl.class).named("composite")
								.to(StubMessageHandler.class).build());
						config.bind(BuilderHelper.link(DummyStubMessageHandler.class).named("processing")
								.to(StubMessageHandler.class).build());
						config.bind(BuilderHelper.link(DummyStubMessageHandler.class).named("save")
								.to(StubMessageHandler.class).build());
						config.bind(BuilderHelper.link(DummyStubMessageHandler.class).named("load")
								.to(StubMessageHandler.class).build());
						config.bind(BuilderHelper.link(ParallelStubMessageHandler.class).named("parallel")
								.to(StubMessageHandler.class).build());//
						config.bind(BuilderHelper.link(RestClientStubMessageHandler.class).named("nextHop")
								.to(StubMessageHandler.class).build());//

					}
				})//
				.register(new AbstractBinder() {

					@Override
					protected void configure() {
						bind(nodeIdentifier).to(String.class).named("nodeIdentifier");

					}
				});

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
		final HttpServer server = startServer(args[0]);

		System.out.println(String.format(
				"Jersey app started with WADL available at " + "%sapplication.wadl\nType Stop and enter to stop it...",
				BASE_URI));
		Scanner scanner = new Scanner(System.in);

		while (true) {
			String command = scanner.nextLine();
			if ("stop".equals(command)) {
				server.stop();
				break;
			}
		}
	}
}