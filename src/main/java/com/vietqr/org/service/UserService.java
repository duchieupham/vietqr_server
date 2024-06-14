package com.vietqr.org.service;

import com.vietqr.org.dto.UserRequestDTO;
import com.vietqr.org.dto.UserResponseDTO;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
}
