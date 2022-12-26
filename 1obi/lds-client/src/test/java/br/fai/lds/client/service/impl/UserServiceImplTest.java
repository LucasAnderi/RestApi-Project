package br.fai.lds.client.service.impl;

import br.fai.lds.client.service.RestService;
import br.fai.lds.model.entities.UserModel;
import br.fai.lds.model.enums.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
//RestTemplate serve para fazer o exchange

@ExtendWith(MockitoExtension.class) //
class UserServiceImplTest {

    @Mock
    private HttpSession httpSession;//injeção de dependencias para essa classe funcionar assim como os 2 abaixos

    @Mock
    private RestService restService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserServiceImpl cut; // class under test ou sut - system undertest

    @Test
    void shouldInjectBeans() {
        assertNotNull(cut);
        assertNotNull(httpSession);
        assertNotNull(restService);
        assertNotNull(restTemplate);

    }

    @Test
    void whenValidUserIsProvided_shouldCreate() {

        UserModel user = createFirstMockUser();

        final int id = cut.create(user);

        assertThat(id).isEqualTo(-1);

    } // solução amanha dia 29/11

    @Test
    void whenNoUserIsFound_shouldReturnEmptyList() {
        List<UserModel> users = cut.find();

        assertThat(users).isEmpty();

    }


    private UserModel createFirstMockUser() {

        UserModel user = new UserModel();
        user.setId(1);
        user.setUsername("tiburrsinho");
        user.setFullName("Tiburssios tiburssin");
        user.setEmail("tiburssin@gmail.com");
        user.setPassword("123");
        user.setType(UserType.ADMINISTRADOR);
        user.setActive(true);

        Timestamp dateTime = new Timestamp(System.currentTimeMillis());

        user.setLastModified(dateTime);
        user.setCreatedAt(dateTime);

        return user;
    }

    private UserModel createSecondtMockUser() {

        UserModel user = new UserModel();
        user.setId(2);
        user.setUsername("tiburrsinho2");
        user.setFullName("Tiburssios2 tiburssin2");
        user.setEmail("tiburssi2n@gmail.com");
        user.setPassword("12322");
        user.setType(UserType.USUARIO);
        user.setActive(false);

        Timestamp dateTime = new Timestamp(System.currentTimeMillis());

        user.setLastModified(dateTime);
        user.setCreatedAt(dateTime);

        return user;
    }

}