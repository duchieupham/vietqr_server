package com.vietqr.org.service;

import com.vietqr.org.dto.*;
import com.vietqr.org.entity.*;
import com.vietqr.org.repository.*;
import com.vietqr.org.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountSystemServiceImpl implements AccountSystemService {
//    private final static Logger logger = Logger.getLogger(String.valueOf(AccountSystemServiceImpl.class));

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
    public void updateUser(String userId, UserUpdateRequestDTO userUpdateRequestDTO) {
        accountInformationRepository.updateUserByUserId(
                userId,
                StringUtil.getValueNullChecker(userUpdateRequestDTO.getFirstName()),
                StringUtil.getValueNullChecker(userUpdateRequestDTO.getMiddleName()),
                StringUtil.getValueNullChecker(userUpdateRequestDTO.getLastName()),
                StringUtil.getValueNullChecker(userUpdateRequestDTO.getAddress()),
                StringUtil.getValueNullChecker(userUpdateRequestDTO.getGender()),
                StringUtil.getValueNullChecker(userUpdateRequestDTO.getNationalId()),
                StringUtil.getValueNullChecker(userUpdateRequestDTO.getOldNationalId()),
                StringUtil.getValueNullChecker(userUpdateRequestDTO.getNationalDate())
        );
    }

    @Override
    public void updateAccountEmail(String userId, String email, int type) {
        accountInformationRepository.updateEmailByUserId(userId, email);
        accountLoginRepository.updateEmailByUserId(email, userId);
        if (type == 1) {
            accountLoginRepository.updateIsVerifiedByUserId(userId, email);
        } else {
            accountLoginRepository.updateInvalidateByUserId(userId);
        }
    }
}
