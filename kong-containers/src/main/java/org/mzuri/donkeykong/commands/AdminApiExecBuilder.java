package org.mzuri.donkeykong.commands;

import org.mzuri.donkeykong.resources.AuthMethod;
import org.mzuri.donkeykong.resources.OIDCPlugin;
import org.mzuri.donkeykong.resources.Route;
import org.mzuri.donkeykong.resources.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

public class AdminApiExecBuilder implements Command {

    //      -d '{"host": "httpbin.org", "path" : "/anything", "id": "4727453c-dd05-4fe4-a8d8-1cc5b267e39e", "protocol":"http", "read_timeout":60000,"tls_verify_depth":null,"port":80,"updated_at":1610093790,"ca_certificates":null,"created_at":1605802543,"connect_timeout":60000,"write_timeout":60000,"name":"HTTP-Bin","retries":5,"tls_verify":null,"tags":null,"client_certificate":null}'
    public AdminApiExec buildService(String host, String path, String name) throws JsonProcessingException {

        Service service = new Service(name, host, path, 80);

        String json =  new ObjectMapper().writeValueAsString(service);

        return new AdminApiExec(AdminApiCommand.SERVICE_CREATE, Map.of("-d", json));
    }

    //    -d '{"id": "ae22a6c1-2f9a-4ec1-98d9-d20b0b36fcd2","path_handling":"v0", "paths": ["/httpbin/v1"],"destinations":null,"headers":null,"protocols":["http"],"created_at":1605802777,"snis":null, "service":{"id": "4727453c-dd05-4fe4-a8d8-1cc5b267e39e"},"name":"httpbin","strip_path":true,"preserve_host":false,"regex_priority":0,"updated_at":1605802777,"sources":null,"methods":null,"https_redirect_status_code":426,"hosts":null,"tags":null}'
    public AdminApiExec buildRoute(String name, List<String> paths, String serviceName) throws JsonProcessingException {
        Service service = new Service(serviceName);
        Route route = new Route(name, service, paths);
        String json =  new ObjectMapper().writeValueAsString(route);

        return new AdminApiExec(AdminApiCommand.ROUTE_CREATE, Map.of("-d", json));
    }

    public AdminApiExec buildOidcPlugin(String serviceName) throws JsonProcessingException {
        Service service = new Service(serviceName);
        OIDCPlugin oidcPlugin = new OIDCPlugin(service,"http://keycloak:8080/auth/realms/master/.well-known/openid-configuration", true, List.of(AuthMethod.CLIENT_CREDENTIALS));

        String json =  new ObjectMapper().writeValueAsString(oidcPlugin);

        return new AdminApiExec(AdminApiCommand.PLUGIN_OIDC, Map.of("-d", json));
    }

    public AdminApiExec buildOidcIntrospectionPlugin(String serviceName) throws JsonProcessingException {
        Service service = new Service(serviceName);
        OIDCPlugin oidcPlugin = new OIDCPlugin(service,"http://keycloak:8080/auth/realms/master/.well-known/openid-configuration", true, List.of(AuthMethod.INTROSPECTION));

        oidcPlugin.getConfig().setClientIds(List.of("clientId"));
        oidcPlugin.getConfig().setClientSecrets(List.of("secret"));

        String json =  new ObjectMapper().writeValueAsString(oidcPlugin);

        return new AdminApiExec(AdminApiCommand.PLUGIN_OIDC, Map.of("-d", json));
    }
}
