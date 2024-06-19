package com.vietqr.org.repository;

import com.vietqr.org.dto.IFeePackageDTO;
import com.vietqr.org.entity.FeePackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeePackageRepository extends JpaRepository<FeePackageEntity, String> {

    @Query(value = "SELECT a.id AS id, a.active_fee AS activeFee, a.annual_fee AS annualFee, "
            + "a.description AS description, "
            + "a.fix_fee AS fixFee, a.percent_fee AS percentFee, a.record_type AS recordType, a.ref_id AS refId, "
            + "COALESCE(a.service_type, '') AS serviceType, a.short_name AS shortName, a.title AS title, a.vat AS vat "
            + "FROM viet_qr.fee_package a "
            + "WHERE (title LIKE %:value%) OR (description LIKE %:value%) LIMIT :offset, :size ", nativeQuery = true)
    List<IFeePackageDTO> getFeePackageByName(String value, int offset, int size);

    @Query(value = "SELECT a.id AS id, a.active_fee AS activeFee, a.annual_fee AS annualFee, "
            + "a.description AS description, "
            + "a.fix_fee AS fixFee, a.percent_fee AS percentFee, a.record_type AS recordType, a.ref_id AS refId, "
            + "a.service_type AS serviceType, a.short_name AS shortName, a.title AS title, a.vat AS vat "
            + "FROM viet_qr.fee_package a "
            + "WHERE (annual_fee = :value) OR (active_fee = :value) OR (fix_fee = :value) "
            + "LIMIT :offset, :size ", nativeQuery = true)
    List<IFeePackageDTO> getListFeePackageByFee(String value, int offset, int size);

    @Query(value = "SELECT count(id) FROM viet_qr.fee_package " +
            "WHERE (title LIKE %:value%) OR (description LIKE %:value%) ", nativeQuery = true)
    int countFeePackageByName(String value);

    @Query(value = "SELECT count(id) FROM viet_qr.fee_package " +
            "WHERE (annual_fee = :value) OR (active_fee = :value) OR (fix_fee = :value) ", nativeQuery = true)
    int countFeePackageByFee(String value);
}
