package com.vietqr.org.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vietqr.org.dto.BusinessListItemDTO;
import com.vietqr.org.dto.MemberDTO;
import com.vietqr.org.dto.BusinessChoiceDTO;
import com.vietqr.org.dto.BusinessItemDTO;
import com.vietqr.org.entity.BusinessMemberEntity;

@Service
public interface BusinessMemberService {

	public int insertBusinessMember(BusinessMemberEntity entity);

	public List<MemberDTO> getBusinessMembersByBusinessId(String businessId);

	public void deleteMemberFromBusiness(String id);

	public List<BusinessListItemDTO> getBusinessListItem(String userId);

	public List<BusinessChoiceDTO> getBusinessChoiceByUserId(String userId);

	public List<BusinessItemDTO> getBusinessItemByUserId(String userId);

	public int getRoleFromBusiness(String userId, String businessId);

	public void deleteBusinessMemberByUserIdAndBankId(String userId, String bankId);
}
