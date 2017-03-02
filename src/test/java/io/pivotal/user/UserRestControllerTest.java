package io.pivotal.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.RestApiApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestApiApplication.class)
@WebAppConfiguration
public class UserRestControllerTest {

    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setup() throws Exception {
        userRepository.deleteAll();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCreate() throws Exception {

        User user = createUser("Bengt", "+35308511111111", "Dublin");

        this.mockMvc.perform(post("/users")
                .content(toJson(user))
                .contentType(APPLICATION_JSON))

                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))

                .andExpect(jsonPath("$.id",             notNullValue()))
                .andExpect(jsonPath("$.name",           is("Bengt")))
                .andExpect(jsonPath("$.phoneNumber",    is("+35308511111111")))
                .andExpect(jsonPath("$.address",        is("Dublin")));

    }

    @Test
    public void testGetAll() throws Exception {

        List<User> users = Arrays.asList(
                createUser("Bengt", "+35308511111111", "Dublin"),
                createUser("John", "+35308522222222", "London")
        );

        userRepository.save(users);

        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].name", is("Bengt")))
                .andExpect(jsonPath("$[0].phoneNumber", is("+35308511111111")))
                .andExpect(jsonPath("$[0].address", is("Dublin")))

                .andExpect(jsonPath("$[1].id", notNullValue()))
                .andExpect(jsonPath("$[1].name", is("John")))
                .andExpect(jsonPath("$[1].phoneNumber", is("+35308522222222")))
                .andExpect(jsonPath("$[1].address", is("London")));

    }

    @Test
    public void testGetOne() throws Exception {

        User user = createUser("Bengt", "+35308511111111", "Dublin");
        User savedUser = userRepository.save(user);

        this.mockMvc.perform(get("/users/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))

                .andExpect(jsonPath("$.id", is(savedUser.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Bengt")))
                .andExpect(jsonPath("$.phoneNumber", is("+35308511111111")))
                .andExpect(jsonPath("$.address", is("Dublin")));

    }

    @Test
    public void testUpdate() throws Exception {

        //Create a new user in the database
        User user = userRepository.save(createUser("Bengt", "+35308511111111", "Dublin"));

        //Create a copy of the user in memory, updates address
        User userUpdated = createUser(user.getName(), user.getPhoneNumber(), "Berlin");

        //Performs the PUT request
        this.mockMvc.perform(put("/users/" + user.getId())
                .content(toJson(userUpdated))
                .contentType(APPLICATION_JSON))

                //HTTP assertions
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))

                //Response body assertions
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Bengt")))
                .andExpect(jsonPath("$.phoneNumber", is("+35308511111111")))
                .andExpect(jsonPath("$.address", is("Berlin")));

    }

    @Test
    public void testDelete() throws Exception {

        //Create a new user in the database
        User user = userRepository.save(createUser("Bengt", "+35308511111111", "Dublin"));

        //Performs the DELETE request
        this.mockMvc.perform(delete("/users/" + user.getId())
                .contentType(APPLICATION_JSON))

                //HTTP assertions
                .andExpect(status().isNoContent())

                //Response body assertions
                .andExpect(content().bytes(EMPTY_BYTE_ARRAY));

    }

    private User createUser(String name, String phoneNumber, String address) {
        User user = new User();
        user.setName(name);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);

        return user;
    }

    private String toJson(Object o) throws IOException {
        return new ObjectMapper().writeValueAsString(o);
    }

}