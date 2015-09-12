package ca.uptoeleven.reactivedemo;

import ca.uptoeleven.reactivedemo.resources.AsyncConsumerResource;
import ca.uptoeleven.reactivedemo.resources.ReactiveConsumerResource;
import ca.uptoeleven.reactivedemo.resources.RemoteServiceResource;
import ca.uptoeleven.reactivedemo.resources.SerialConsumerResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class ReactiveDemoApplication extends Application<ReactiveDemoConfiguration> {
	public static void main(String[] args) throws Exception {
		new ReactiveDemoApplication().run(args);
	}

	@Override
	public String getName() {
		return "hello-world";
	}

	@Override
	public void initialize(Bootstrap<ReactiveDemoConfiguration> bootstrap) {
		// nothing to do yet
	}

	@Override
	public void run(ReactiveDemoConfiguration configuration,
	                Environment environment) {
		final RemoteServiceResource remoteResource = new RemoteServiceResource();
		final SerialConsumerResource serialResource = new SerialConsumerResource();
		final ReactiveConsumerResource reactiveResource = new ReactiveConsumerResource();
		final AsyncConsumerResource asyncResource = new AsyncConsumerResource();

		environment.jersey().register(remoteResource);
		environment.jersey().register(serialResource);
		environment.jersey().register(asyncResource);
		environment.jersey().register(reactiveResource);
	}

}