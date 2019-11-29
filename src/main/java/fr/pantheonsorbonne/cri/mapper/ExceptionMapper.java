package fr.pantheonsorbonne.cri.mapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionMapper implements javax.ws.rs.ext.ExceptionMapper<Throwable> {
	@Override
	public Response toResponse(Throwable exception) {
		exception.printStackTrace();
		if (exception instanceof WebApplicationException) {
			WebApplicationException wae =  (WebApplicationException) exception;
			return wae.getResponse();
		} else {
			throw new RuntimeException(exception.getMessage());
		}

	}
}
