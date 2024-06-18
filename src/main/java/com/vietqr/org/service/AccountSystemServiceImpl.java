package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.repository.*;
import com.vietqr.org.util.RandomCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class AccountSystemServiceImpl implements AccountSystemService {
    private final static Logger logger = Logger.getLogger(String.valueOf(AccountSystemServiceImpl.class));

    @Autowired
    AccountSystemRepository repo;
    @Autowired
    AccountSettingService accountSettingService;
    @Autowired
    MobileCarrierService mobileCarrierService;
    @Autowired
    AccountWalletService accountWalletService;
    @Autowired
    AccountWalletRepository accountWalletRepository;
    @Autowired
    AccountSettingRepository accountSettingRepository;
    @Autowired
    private AccountInformationRepository accountInformationRepository;
    @Autowired
    private AccountLoginRepository accountLoginRepository;

    @Override
    public int insertNewAdmin(AccountSystemEntity entity) {
        return repo.save(entity) == null ? 0 : 1;
    }

    @Override
    public AccountSystemEntity loginAdmin(String username, String password) {
        return repo.loginAdmin(username, password);
    }

    @Override
    public String checkExistedAdmin(String id) {
        return repo.checkExistedAdmin(id);
    }

    @Override
    public IAccountSystemDTO findAdminById(String adminId) {
        return repo.findAdminById(adminId);
    }

    @Override
    public boolean resetUserPassword(String phoneNo, String newPassword) {
        int updateRows = repo.updateUserPassword(phoneNo, newPassword);
        return updateRows > 0;
    }

    @Override
    public boolean updateUserStatus(String id, boolean status) {
        int updatedRows = accountInformationRepository.updateUserStatus(id, status);
        return updatedRows > 0;
    }

    @Override
    public UserUpdateResponseDTO updateUser(String userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        int updatedRows = accountInformationRepository.updateUserByUserId(
                userId,
                userUpdateRequestDTO.getFirstName(),
                userUpdateRequestDTO.getMiddleName(),
                userUpdateRequestDTO.getLastName(),
                userUpdateRequestDTO.getAddress(),
                userUpdateRequestDTO.getGender(),
                userUpdateRequestDTO.getEmail(),
                userUpdateRequestDTO.getNationalId(),
                userUpdateRequestDTO.getOldNationalId(),
                userUpdateRequestDTO.getNationalDate()
        );

        if (updatedRows > 0) {
            IUserUpdateDTO updatedEntity = accountInformationRepository.findByUserId(userId);
            return new UserUpdateResponseDTO(
                    updatedEntity.getEmail(),
                    updatedEntity.getFirstName(),
                    updatedEntity.getMiddleName(),
                    updatedEntity.getLastName(),
                    updatedEntity.getAddress(),
                    updatedEntity.getGender(),
                    updatedEntity.getNationalId(),
                    updatedEntity.getOldNationalId(),
                    updatedEntity.getNationalDate()
            );
        }
        return null;
    }

}
