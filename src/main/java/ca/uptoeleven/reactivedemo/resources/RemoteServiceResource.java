package ca.uptoeleven.reactivedemo.resources;

import ca.uptoeleven.reactivedemo.EntityModel;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Path("/remote")
@Produces(MediaType.APPLICATION_JSON)
public class RemoteServiceResource {

	@GET
	@Path("ids")
	public List<Integer> getIds() {
		List<Integer> ids = new ArrayList<Integer>();
		for(int i = 1; i < 7; i++) {
			ids.add(i);
		}
		return ids;
	}

	@GET
	@Path("entities/{id}")
	public EntityModel getEntity(@PathParam("id") int id) {
		delay();
		return new EntityModel(id, (id % 2 != 0));
	}

	@POST
	@Path("validate")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doubleOdd(EntityModel model) {
		delay();
		if (model.isOdd()) {
			return Response.ok(new EntityModel(model.getId() * 2, false)).build();
		} else {
			return Response.status(400).build();
		}
	}

	private void delay() {
		try {
			Thread.sleep(200);
		} catch (Exception e) {}
	}
}
