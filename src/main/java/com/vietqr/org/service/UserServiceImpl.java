package com.vietqr.org.service;

import com.vietqr.org.dto.UserRequestDTO;
import com.vietqr.org.dto.UserResponseDTO;
import com.vietqr.org.entity.AccountInformationEntity;
import com.vietqr.org.entity.AccountLoginEntity;
import com.vietqr.org.repository.AccountInformationRepository;
import com.vietqr.org.repository.AccountLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private AccountInformationRepository accountInformationRepository;

    @Autowired
    private AccountLoginRepository accountLoginRepository;

    @Autowired
    MobileCarrierService mobileCarrierService;

   // @Autowired
//    private BCryptPasswordEncoder passwordEncoder;
    @Override
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        // Generate unique IDs
        String id = UUID.randomUUID().toString();
        String userId = UUID.randomUUID().toString();
        String loginId = UUID.randomUUID().toString();

        // Hash password
       // String hashedPassword = passwordEncoder.encode(userRequestDTO.getPassword());

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
        // set carrier type id
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
        accountLoginEntity.setPassword(userRequestDTO.getPassword());
        accountLoginEntity.setStatus(true);
        accountLoginEntity.setEmail(userRequestDTO.getEmail());

        // Save AccountLoginEntity
        accountLoginRepository.save(accountLoginEntity);

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
        userResponseDTO.setCarrierTypeId("");
        userResponseDTO.setStatus(true);

        return userResponseDTO;
    }
}
