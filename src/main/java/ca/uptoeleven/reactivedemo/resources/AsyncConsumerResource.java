package ca.uptoeleven.reactivedemo.resources;

import ca.uptoeleven.reactivedemo.EntityModel;
import ca.uptoeleven.reactivedemo.ProcessResult;
import ca.uptoeleven.reactivedemo.RemoteService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
@Path("/async" )
@Produces(MediaType.APPLICATION_JSON)
public class AsyncConsumerResource extends TimedResource {

	@GET
	@SneakyThrows
	public Response get() {

		final CountDownLatch outerLatch = new CountDownLatch(1);

		AsyncRemoteService svc = new AsyncRemoteService();

		Queue<EntityModel> results = new ConcurrentLinkedQueue<EntityModel>();

		start();

		Future<List<Integer>> ids = svc.getIds(new InvocationCallback<List<Integer>>() {
			@Override
			@SneakyThrows
			public void completed(List<Integer> ids) {
				final CountDownLatch innerLatch = new CountDownLatch(ids.size());
				for (int id : ids) {
					svc.getEntity(id, new InvocationCallback<EntityModel>() {
						@Override
						public void completed(EntityModel entityModel) {
							if (entityModel.isOdd()) {
								svc.transformOdd(entityModel, new InvocationCallback<EntityModel>() {
									@Override
									public void completed(EntityModel entityModel) {
										results.add(entityModel);
										innerLatch.countDown();
									}

									@Override
									public void failed(Throwable throwable) {
										log.error("Error!", throwable);
										innerLatch.countDown();
									}
								});
							} else {
								innerLatch.countDown();
							}
						}

						@Override
						public void failed(Throwable throwable) {
							log.error("Error!", throwable);
							innerLatch.countDown();
						}
					});
				}
				innerLatch.await(10, TimeUnit.SECONDS);
				outerLatch.countDown();
			}

			@Override
			public void failed(Throwable throwable) {
				log.error("Error!", throwable);
				outerLatch.countDown();
			}
		});
		outerLatch.await(10, TimeUnit.SECONDS);
		return Response.ok(new ProcessResult(results, stop())).build();
	}

	class AsyncRemoteService extends RemoteService {

		public Future<List<Integer>> getIds(InvocationCallback<List<Integer>> callback) {
			return idsTarget
					.request().async()
					.get(callback);
		}

		public Future<EntityModel> getEntity(int id, InvocationCallback<EntityModel> callback) {
			return entityTarget
					.resolveTemplate("id", id)
					.request().async()
					.get(callback);
		}

		public Future<EntityModel> transformOdd(EntityModel entity, InvocationCallback<EntityModel> callback) {
			return validateTarget
					.request().async()
					.post(Entity.entity(entity, MediaType.APPLICATION_JSON), callback);
		}
	}
}
