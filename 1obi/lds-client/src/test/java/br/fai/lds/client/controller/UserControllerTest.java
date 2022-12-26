package br.fai.lds.client.controller;

import br.fai.lds.client.config.security.SecurityConfig;
import br.fai.lds.client.config.security.providers.FaiAuthenticationProvider;
import br.fai.lds.client.service.ReportService;
import br.fai.lds.client.service.UserService;
import br.fai.lds.model.entities.UserModel;
import br.fai.lds.model.enums.UserType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, FaiAuthenticationProvider.class})
class UserControllerTest {

    public static final String LIST_PAGE_ENDPOINT = "/user/list";

    public static final String EDIT_PAGE_ENDPOINT = "/user/edit/";

    public static final String DETAIL_PAGE_ENDPOINT = "/user/detail/";
    public static final String DELETE_PAGE_ENDPOINT = "/user/delete/";
    public static final String SIGN_IN_PAGE = "http://localhost/account/sign-in";
    @MockBean
    private UserService userService;

    @MockBean
    private ReportService reportService;

    @MockBean
    private Model model;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldInjectBeans() {
        // assertJ
        assertThat(userService).isNotNull();

        // junit5
        assertNotNull(reportService);
        assertNotNull(mockMvc);
    }

    // given - when - should
    @Test
    void getUsers_whenNotAuthenticated_shouldRedirectToSignInPage() throws Exception {
        mockMvc.perform(get(LIST_PAGE_ENDPOINT))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/account/sign-in"));
    }

    @Test
    @WithMockUser
    void getUsers_whenAuthenticated_whenNoUsersIsReturned_shouldShowEmptyListPage() throws Exception {

        mockMvc.perform(get(LIST_PAGE_ENDPOINT)
                        .sessionAttr("currentUser", new UserModel())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(model().attributeExists("user", "users"));
    }

    @Test
    @WithMockUser
        // BYPASSA A AUTENTICAÇÃO
    void getUsers_whenAuthenticated_whenThereAreUsers_shouldListPage() throws Exception {

        UserModel tiburssinho = new UserModel();
        tiburssinho.setId(1);
        tiburssinho.setFullName("Tiburssinho Tiburssius");
        tiburssinho.setEmail("tiburssinho@gmail.com");
        tiburssinho.setType(UserType.ADMINISTRADOR);

        UserModel aroldo = new UserModel();
        aroldo.setId(2);
        aroldo.setFullName("Aroldo Aroldus");
        aroldo.setEmail("aroldo@gmail.com");
        aroldo.setType(UserType.USUARIO);

        // forma antiga
//        List<UserModel> users = new ArrayList<>();
//        users.add(tiburssinho);
//        users.add(aroldo);

        //forma nova
        List<UserModel> users = Arrays.asList(tiburssinho, aroldo);

        when(userService.find()).thenReturn(users);

        MvcResult mvcResult = mockMvc.perform(get(LIST_PAGE_ENDPOINT)
                        .sessionAttr("currentUser", new UserModel()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user", "users"))
                .andExpect(view().name("user/list"))
                .andReturn();

        final String html = mvcResult.getResponse()
                .getContentAsString();

        assertThat(html).contains("<td>" + tiburssinho.getId() + "</td>");
        assertThat(html).contains("<td>" + tiburssinho.getFullName()
                + "</td>");
        assertThat(html).contains("<td>" + tiburssinho.getEmail() + "</td>");

        assertThat(html).contains("<td>" + aroldo.getId() + "</td>");
        assertThat(html).contains("<td>" + aroldo.getFullName()
                + "</td>");
        assertThat(html).contains("<td>" + aroldo.getEmail() + "</td>");
    }


    @Test
    void getEditPage_whenNotAuthenticated_shouldRedirectToSignInPage() throws Exception {
        mockMvc.perform(get(EDIT_PAGE_ENDPOINT + 1))
                .andDo(print()) // nao é necessario serve só pra printar no console do test os detalhes da request
                .andExpect(status().is3xxRedirection()) // vai para o redirecionamento
                .andExpect(redirectedUrl(SIGN_IN_PAGE));
    }

    @Test
    @WithMockUser
    void getDetailPage_whenAuthenticated_whenUserIsFound_shouldShowDetailPage() throws Exception {

        when(userService.findById(anyInt())).thenReturn(new UserModel()); // o detail necessita do objeto que vem do findbyid se nao ele volta no null e redireciona
        //anyint acima  aceita qualquer inteiro que a gente mandar na url abaixo

        mockMvc.perform(get(DETAIL_PAGE_ENDPOINT + 1))
                .andExpect(status().isOk())// 300+ geralemnte é sobre redirecionamento
                .andExpect(view().name("user/detail"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    @WithMockUser
    void getDetailPage_whenAuthenticated_whenNoUserIsReturned_shouldRedirectToListPage() throws Exception {
        mockMvc.perform(get(DETAIL_PAGE_ENDPOINT + 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LIST_PAGE_ENDPOINT));

        verify(model, never()).addAttribute(anyString(), any(UserModel.class)); // model nunca poderia chamar o metodo model.addAttribute;
        // times() - no lugar do never ve  quantas vezes ele vai chmar o addAttributte

    }

    @Test
    @WithMockUser
    void getDelete_whenAuthenticated_whenDeleteUser_ShouldReturnToList() throws Exception {


        mockMvc.perform(get(DELETE_PAGE_ENDPOINT + 1))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LIST_PAGE_ENDPOINT));
        verify(userService, times(1)).deleteById(1);
    }


    //1- nao ta autenticado
    //2- se deu certo verify se o userService chama o delete/update time 1
    //3- deletou entao tem que mostrar o userList dnv - redirect
    // fazer o update e o delete !!!!!!


}