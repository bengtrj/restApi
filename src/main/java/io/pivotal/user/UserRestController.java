package io.pivotal.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(path = "/users")
class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method = POST)
    @ResponseStatus(CREATED)
    User create(@RequestBody User user) {
        return userRepository.save(user);
    }

    @RequestMapping(method = DELETE, path = "/{id}")
    @ResponseStatus(NO_CONTENT)
    void delete(@PathVariable Long id) {
        userRepository.delete(id);
    }

    @RequestMapping(method = GET)
    List<User> listAll() {
        return userRepository.findAll();
    }

    @RequestMapping(method = GET, path = "/{id}")
    User findById(@PathVariable Long id) {
        return userRepository.findOne(id);
    }

    @RequestMapping(method = PUT, path = "/{id}")
    User update(@RequestBody User user, @PathVariable Long id) {
        user.setId(id);
        return userRepository.save(user);
    }

}
