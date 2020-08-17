package ru.natlex.geo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.natlex.geo.DAO.UserDAO;
import ru.natlex.geo.entity.User;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserDAO userDao;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userDao.getUserByUsername(s);
        if (user == null)
            throw new UsernameNotFoundException("No such user " + s);

        return user;
    }
}
