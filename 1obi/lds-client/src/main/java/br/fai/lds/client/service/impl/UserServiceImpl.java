package br.fai.lds.client.service.impl;

import br.fai.lds.client.service.RestService;
import br.fai.lds.client.service.UserService;
import br.fai.lds.model.entities.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class UserServiceImpl implements UserService<UserModel> {

    public UserServiceImpl(HttpSession httpSession) {
        this.httpSession = httpSession;
    } // quando duas classes implementam a mesma interface o spring nao consegue encontrar  ai quando vc cria um @Bean igual no @config

    private HttpSession httpSession;

    private static final String BASE_ENDPOINT = "http://localhost:8081/api/";

    private String buildEndpoint(String resource) {
        return BASE_ENDPOINT + resource;
    }

    @Autowired
    private RestService<UserModel> restService;

    @Override
    public int create(UserModel entity) {

        return restService.post("user/create", entity);
    }

    @Override
    public List<UserModel> find() {
        HttpHeaders requestHeaders = restService.getRequestHeaders(httpSession);

        return restService.get("user/find", requestHeaders);
    }

    @Override
    public UserModel findById(int id) {
        HttpHeaders requestHeaders = restService.getRequestHeaders(httpSession);

        return restService.getById("user/find/" + id, UserModel.class, requestHeaders);
    }

    @Override
    public boolean update(int id, UserModel entity) {
        HttpHeaders requestHeaders = restService.getRequestHeaders(httpSession);

        return restService.put("user/update/" + id, entity, requestHeaders);
    }

    @Override
    public boolean deleteById(int id) {
        HttpHeaders requestHeaders = restService.getRequestHeaders(httpSession);

        return restService.deleteById("user/delete/" + id, requestHeaders);
    }

    @Override
    public UserModel validateUsernameAndPassword(String username, String password) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = restService.getAuthenticationHeaders(username, password);

            HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);

            String resource = "account/login";

            ResponseEntity<UserModel> responseEntity = restTemplate.exchange(buildEndpoint(resource),
                    HttpMethod.POST, httpEntity, UserModel.class);

            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                return null;
            }

            UserModel user = responseEntity.getBody();

            return user;
        } catch (RestClientException e) {
            e.printStackTrace();
            return null;
        }
    }
}
