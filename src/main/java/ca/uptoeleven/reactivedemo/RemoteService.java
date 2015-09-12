package ca.uptoeleven.reactivedemo;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public abstract class RemoteService {

    protected final Client client;
    protected final WebTarget baseTarget;
    protected final WebTarget idsTarget;
    protected final WebTarget entityTarget;
    protected final WebTarget validateTarget;

    public RemoteService() {
        ClientConfig clientConfig = new ClientConfig();
        this.client = ClientBuilder.newClient(clientConfig);
        this.baseTarget = client.target("http://localhost:8080/remote" );
        this.idsTarget = baseTarget.path("ids" );
        this.entityTarget = baseTarget.path("entities/{id}" );
        this.validateTarget = baseTarget.path("validate" );
    }
}