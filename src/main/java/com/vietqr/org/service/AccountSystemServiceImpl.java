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
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        // Generate unique IDs
        String id = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String loginId = UUID.randomUUID().toString();
        String accountSettingUUID = UUID.randomUUID().toString();
        String accountWalletUUID = UUID.randomUUID().toString();

        // Create AccountInformationEntity
        AccountInformationEntity accountInformationEntity = new AccountInformationEntity();
        accountInformationEntity.setId(id);
        accountInformationEntity.setUserId(userId);
        accountInformationEntity.setFirstName(userRequestDTO.getFirstName());
        accountInformationEntity.setMiddleName(userRequestDTO.getMiddleName());
        accountInformationEntity.setLastName(userRequestDTO.getLastName());
        accountInformationEntity.setBirthDate("");
        accountInformationEntity.setAddress(userRequestDTO.getAddress());
        accountInformationEntity.setGender(userRequestDTO.getGender());
        accountInformationEntity.setEmail(userRequestDTO.getEmail());
        accountInformationEntity.setNationalId(userRequestDTO.getNationalId());
        accountInformationEntity.setOldNationalId(userRequestDTO.getOldNationalId());
        accountInformationEntity.setNationalDate(userRequestDTO.getNationalDate());

        // Set carrier type id
        if (userRequestDTO.getPhoneNo() != null && !userRequestDTO.getPhoneNo().trim().isEmpty()) {
            String prefix = userRequestDTO.getPhoneNo().substring(0, 3);
            String carrierTypeId = mobileCarrierService.getTypeIdByPrefix(prefix);
            if (carrierTypeId != null) {
                accountInformationEntity.setCarrierTypeId(carrierTypeId);
            } else {
                accountInformationEntity.setCarrierTypeId("");
            }
        } else {
            accountInformationEntity.setCarrierTypeId("");
        }
        accountInformationEntity.setStatus(true);

        // Save AccountInformationEntity
        accountInformationRepository.save(accountInformationEntity);

        // Create AccountLoginEntity
        AccountLoginEntity accountLoginEntity = new AccountLoginEntity();
        accountLoginEntity.setId(loginId);
        accountLoginEntity.setPhoneNo(userRequestDTO.getPhoneNo());
        LocalDateTime currentDateTime = LocalDateTime.now();
        long time = currentDateTime.toEpochSecond(ZoneOffset.UTC);
        accountLoginEntity.setTime(time);
        accountLoginEntity.setSyncBitrix(false);
        accountLoginEntity.setPassword(userRequestDTO.getPassword());
        accountLoginEntity.setStatus(true);
        accountLoginEntity.setEmail(userRequestDTO.getEmail());

        // Save AccountLoginEntity
        accountLoginRepository.save(accountLoginEntity);

        // Create AccountSettingEntity
        AccountSettingEntity accountSettingEntity = new AccountSettingEntity();
        accountSettingEntity.setId(accountSettingUUID.toString());
        accountSettingEntity.setGuideMobile(false);
        accountSettingEntity.setGuideWeb(false);
        accountSettingEntity.setStatus(true);
        accountSettingEntity.setVoiceMobile(true);
        accountSettingEntity.setVoiceMobileKiot(true);
        accountSettingEntity.setVoiceWeb(true);
        accountSettingEntity.setUserId(userId);
        accountSettingEntity.setLastLogin(time);
        accountSettingEntity.setAccessCount(1);
        accountSettingEntity.setEdgeImgId("");
        accountSettingEntity.setFooterImgId("");
        accountSettingEntity.setThemeType(1);
        accountSettingEntity.setKeepScreenOn(false);
        accountSettingEntity.setQrShowType(0);
        accountSettingEntity.setNotificationMobile(true);

        // Save AccountSettingEntity
        accountSettingRepository.save(accountSettingEntity);

        // Create AccountWalletEntity
        AccountWalletEntity accountWalletEntity = new AccountWalletEntity();
        accountWalletEntity.setId(accountWalletUUID.toString());
        accountWalletEntity.setUserId(userId);
        accountWalletEntity.setAmount("0");
        accountWalletEntity.setEnableService(true);
        accountWalletEntity.setActive(true);
        accountWalletEntity.setPoint(50);

        // Set wallet ID
        String walletId = "";
        do {
            walletId = RandomCodeUtil.generateRandomId(12); // Generate random code
        } while (accountWalletService.checkExistedWalletId(walletId) != null);
        accountWalletEntity.setWalletId(walletId);

        // Set sharing code
        String sharingCode = "";
        do {
            sharingCode = RandomCodeUtil.generateRandomId(12); // Generate random code
        } while (accountWalletService.checkExistedSharingCode(sharingCode) != null);
        accountWalletEntity.setSharingCode(sharingCode);

        // Save AccountWalletEntity
        accountWalletRepository.save(accountWalletEntity);


        // Create and return UserResponseDTO
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(userId);
        userResponseDTO.setPhoneNo(userRequestDTO.getPhoneNo());
        userResponseDTO.setEmail(userRequestDTO.getEmail());
        userResponseDTO.setFirstName(userRequestDTO.getFirstName());
        userResponseDTO.setMiddleName(userRequestDTO.getMiddleName());
        userResponseDTO.setLastName(userRequestDTO.getLastName());
        userResponseDTO.setAddress(userRequestDTO.getAddress());
        userResponseDTO.setGender(userRequestDTO.getGender());
        userResponseDTO.setNationalId(userRequestDTO.getNationalId());
        userResponseDTO.setOldNationalId(userRequestDTO.getOldNationalId());
        userResponseDTO.setNationalDate(userRequestDTO.getNationalDate());
        userResponseDTO.setStatus(true);

        return userResponseDTO;
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
