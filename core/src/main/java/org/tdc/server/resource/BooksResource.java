package org.tdc.server.resource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.jackson.Formatted;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.tdc.server.dto.BookDTO;
import org.tdc.server.process.ServerProcessor;

/**
 * REST Resource for uploading and processing Book spreadsheets.
 */
@Path("/tdc/books")
public class BooksResource {
	private final ServerProcessor processor;
	
	public BooksResource(ServerProcessor processor) {
		this.processor = processor;
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadExcelBookFile(MultipartInput input) {
		List<InputPart> parts = input.getParts();
		InputPart part = parts.get(0);
		String bookID;
		try {
			File bookFile = part.getBody(new GenericType<File>(){});
			bookID = processor.uploadBookFile(bookFile);
		}
		catch (IOException ex) {
			throw new RuntimeException("Unable to save file: " + ex);
		}
		input.close();
		Response response;
		try {
			URI uri = new URI("/tdc/books/" + bookID);
			response = Response.status(Response.Status.CREATED).location(uri).build();
		}
		catch (URISyntaxException e) {
			throw new RuntimeException("Unable to create location URI", e);
		}
		return response;
	}

	@GET
	@Path("/{bookID}")
	@Produces(MediaType.APPLICATION_JSON)
	@Formatted
	public BookDTO getBook(@PathParam("bookID") String bookID) {
		return processor.getBookInfo(bookID);
	}
}
