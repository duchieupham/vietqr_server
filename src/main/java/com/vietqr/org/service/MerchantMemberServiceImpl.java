package com.vietqr.org.service;

import com.vietqr.org.dto.AccountMemberDTO;
import com.vietqr.org.dto.IMerchantMemberDTO;
import com.vietqr.org.dto.IMerchantMemberDetailDTO;
import com.vietqr.org.entity.MerchantMemberEntity;
import com.vietqr.org.repository.MerchantMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantMemberServiceImpl implements MerchantMemberService {

    @Autowired
    private MerchantMemberRepository repo;

    @Override
    public String checkUserExistedFromMerchant(String merchantId, String id) {
        return repo.checkUserExistedFromMerchant(merchantId, id);
    }

    @Override
    public int insert(MerchantMemberEntity entity) {
        return repo.save(entity) != null ? 1 : 0;
    }

    @Override
    public void removeMemberFromMerchant(String merchantId, String userId) {
        repo.removeMemberFromMerchant(merchantId, userId);
    }

    @Override
    public void removeMemberFromTerminal(String terminalId, String userId) {
        repo.removeMemberFromTerminal(terminalId, userId);
    }

    @Override
    public void insertAll(List<MerchantMemberEntity> entity) {
        repo.saveAll(entity);
    }

    @Override
    public List<IMerchantMemberDTO> findMerchantMemberByMerchantId(String merchantId, String value, int page, int size) {
        return repo.findMerchantMemberMerchantId(merchantId, value, page, size);
    }

    @Override
    public IMerchantMemberDetailDTO getUserExistedFromMerchant(String merchantId, String userId) {
        return repo.getUserExistedFromMerchant(merchantId, userId);
    }

    @Override
    public void deleteMerchantMemberByUserIdAndMerchantId(String merchantId, String userId) {
        repo.deleteMerchantMemberByUserIdAndMerchantId(merchantId, userId);
    }

    @Override
    public int countMerchantMemberByMerchantId(String merchantId) {
        return repo.countMerchantMemberByMerchantId(merchantId);
    }

    @Override
    public List<AccountMemberDTO> getMembersFromTerminalId(String merchantId, String terminalId) {
        return repo.getMembersFromTerminalId(merchantId, terminalId);
    }

    @Override
    public String checkUserExistedFromTerminal(String merchantId, String terminalId, String userId) {
        return repo.checkUserExistedFromTerminal(merchantId, terminalId, userId);
    }
}
