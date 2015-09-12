package ca.uptoeleven.reactivedemo.resources;

import ca.uptoeleven.reactivedemo.EntityModel;
import ca.uptoeleven.reactivedemo.ProcessResult;
import ca.uptoeleven.reactivedemo.RemoteService;
import com.codahale.metrics.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.*;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Path("/serial")
@Produces(MediaType.APPLICATION_JSON)
public class SerialConsumerResource extends TimedResource {

	@GET
	public Response run() {
		start();

		SerialRemoteService svc = new SerialRemoteService();

		List<EntityModel> models = new ArrayList<EntityModel>();
		List<Integer> results = svc.getIds();

		for(Integer i : results) {
			EntityModel model =svc.getEntity(i);
			if (model.isOdd()) {
				EntityModel updatedModel = svc.transformOdd(model);
				models.add(updatedModel);
			}
		}

		return Response.ok(new ProcessResult(models, stop())).build();
	}

	class SerialRemoteService extends RemoteService {
		public List<Integer> getIds() {
			return idsTarget
					.request(MediaType.APPLICATION_JSON)
					.get(new GenericType<List<Integer>>() {});
		}

		public EntityModel getEntity(int id) {
			return entityTarget.resolveTemplate("id", id)
					.request(MediaType.APPLICATION_JSON)
					.get(EntityModel.class);
		}

		public EntityModel transformOdd(EntityModel model) {
			return validateTarget
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(model, MediaType.APPLICATION_JSON))
					.readEntity(EntityModel.class);
		}
	}
}
