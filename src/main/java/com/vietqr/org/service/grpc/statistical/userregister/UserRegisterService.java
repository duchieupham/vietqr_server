package com.vietqr.org.service.grpc.statistical.userregister;

import com.example.grpc.UserRegister;
import org.springframework.stereotype.Service;

@Service
public class UserRegisterService {
    private final UserRegisterRepository userRegisterRepository;

    public UserRegisterService(UserRegisterRepository userRegisterRepository) {
        this.userRegisterRepository = userRegisterRepository;
    }

    public UserRegister getUserRegisterData(long startDate, long endDate) {
        IUserRegisterDTO dto = userRegisterRepository.getUserRegisterData(startDate, endDate);
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO(dto);
        return UserRegister.newBuilder()
                .setUserCount(userRegisterDTO.getUserCount())
                .setAndroidPlatform(userRegisterDTO.getAndroidPlatform())
                .setIosPlatform(userRegisterDTO.getIosPlatform())
                .setWebPlatform(userRegisterDTO.getWebPlatform())
                .build();
    }
}
