package com.vietqr.org.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vietqr.org.dto.BusinessListItemDTO;
import com.vietqr.org.dto.MemberDTO;
import com.vietqr.org.dto.BusinessChoiceDTO;
import com.vietqr.org.dto.BusinessItemDTO;
import com.vietqr.org.entity.BusinessMemberEntity;
import com.vietqr.org.repository.BusinessMemberRepository;

@Service
public class BusinessMemberServiceImpl implements BusinessMemberService {

	@Autowired
	BusinessMemberRepository repo;

	@Override
	public int insertBusinessMember(BusinessMemberEntity entity) {
		return repo.save(entity) == null ? 0 : 1;
	}

	@Override
	public List<MemberDTO> getBusinessMembersByBusinessId(String businessId) {
		return repo.getBusinessMembersByBusinessId(businessId);
	}

	@Override
	public void deleteMemberFromBusiness(String id) {
		repo.deleteMemberFromBusinessInformation(id);
	}

	@Override
	public List<BusinessListItemDTO> getBusinessListItem(String userId) {
		return repo.getBusinessListItem(userId);
	}

	@Override
	public List<BusinessChoiceDTO> getBusinessChoiceByUserId(String userId) {
		return repo.getBusinessChoiceByUserId(userId);
	}

	@Override
	public List<BusinessItemDTO> getBusinessItemByUserId(String userId) {
		return repo.getBusinessItemByUserId(userId);
	}

	@Override
	public int getRoleFromBusiness(String userId, String businessId) {
		int result = 0;
		try {
			result = Integer.parseInt(repo.getRoleFromBusiness(userId, businessId) + "");
		} catch (Exception e) {
		}
		return result;
	}

	@Override
	public void deleteAllMemberFromBusiness(String businessId) {
		repo.deleteAllMemberFromBusiness(businessId);
	}

	// @Override
	// public void deleteBusinessMemberByUserIdAndBankId(String userId, String
	// bankId) {
	// repo.deleteBusinessMemberByUserIdAndBankId(userId, bankId);
	// }
}
