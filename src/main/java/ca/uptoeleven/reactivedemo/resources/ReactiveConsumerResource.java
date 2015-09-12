package ca.uptoeleven.reactivedemo.resources;


import ca.uptoeleven.reactivedemo.EntityModel;
import ca.uptoeleven.reactivedemo.ProcessResult;
import ca.uptoeleven.reactivedemo.RemoteService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.client.rx.rxjava.RxObservable;
import rx.Observable;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Slf4j
@Path("/reactive" )
@Produces(MediaType.APPLICATION_JSON)
public class ReactiveConsumerResource extends TimedResource {

	@GET
	public Response run() {
		start();
		ReactiveRemoteService svc = new ReactiveRemoteService();

		Observable<Integer> idsObs = svc.getIds();
		Observable<EntityModel> entityObs = idsObs.flatMap(id -> svc.getEntity(id));
		Observable<EntityModel> transformed = entityObs
				.filter(e -> e.isOdd())
				.flatMap(e -> svc.transformOdd(e));

		// Kicks off all async requests and waits for completion
		List<EntityModel> models = Lists.newArrayList(transformed.toBlocking().toIterable());
		return Response.ok(new ProcessResult(models, stop())).build();
	}

	@GET
	@Path("fluid")
	public Response run2() {
		start();
		ReactiveRemoteService svc = new ReactiveRemoteService();

		Observable<EntityModel> transformed =
				svc.getIds()
						.flatMap(id -> svc.getEntity(id))
						.filter(e -> e.isOdd())
						.flatMap(e -> svc.transformOdd(e));

		// Kicks off all async requests and waits for completion
		List<EntityModel> models = Lists.newArrayList(transformed.toBlocking().toIterable());
		return Response.ok(new ProcessResult(models, stop())).build();
	}

	class ReactiveRemoteService extends RemoteService {
		public Observable<Integer> getIds() {
			return RxObservable.from(idsTarget)
					.request().rx()
					.get(new GenericType<List<Integer>>() {})
					.flatMap(Observable::from);
		}

		public Observable<EntityModel> getEntity(int id) {
			return RxObservable.from(entityTarget)
					.resolveTemplate("id", id)
					.request().rx()
					.get(EntityModel.class);
		}

		public Observable<EntityModel> transformOdd(EntityModel model) {
			return RxObservable.from(validateTarget)
					.request().rx()
					.post(Entity.entity(model, MediaType.APPLICATION_JSON))
					.map(response -> response.readEntity(EntityModel.class));
		}
	}
}
