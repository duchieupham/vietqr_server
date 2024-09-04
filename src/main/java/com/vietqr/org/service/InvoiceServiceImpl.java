package com.vietqr.org.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vietqr.org.dto.*;
import com.vietqr.org.entity.InvoiceEntity;
import com.vietqr.org.repository.InvoiceRepository;
import com.vietqr.org.util.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository repo;

    @Override
    public void insert(InvoiceEntity entity) {
        repo.save(entity);
    }

    @Override
    public String getFileAttachmentId(String invoiceId) {
        return repo.getFileAttachmentId(invoiceId);
    }

    @Override
    public List<IInvoiceResponseDTO> getInvoiceByUserId(String userId, List<Integer> status, int offset, int size) {
        return repo.getInvoiceByUserId(userId, status, offset, size);
    }

    @Override
    public String checkDuplicatedInvoiceId(String invoiceId) {
        return repo.checkDuplicatedInvoiceId(invoiceId);
    }

    @Override
    public List<IInvoiceResponseDTO> getInvoiceByUserIdAndMonth(String userId, List<Integer> status, String month, int offset, int size) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByUserIdAndMonth(userId, status, fromDate, toDate, offset, size);
    }

    @Override
    public List<IInvoiceResponseDTO> getInvoiceByUserIdAndBankId(String userId, List<Integer> status, String bankId, int offset, int size) {
        return repo.getInvoiceByUserIdAndBankId(userId, status, bankId, offset, size);
    }

    @Override
    public List<IInvoiceResponseDTO> getInvoiceByUserIdAndBankIdAndMonth(String userId, List<Integer> status, String bankId, String month, int offset, int size) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo
                .getInvoiceByUserIdAndBankIdAndMonth(userId, status, bankId, fromDate, toDate, offset, size);
    }

    @Override
    public int countInvoiceByUserId(String userId, List<Integer> status) {
        return repo.countInvoiceByUserId(userId, status);
    }

    @Override
    public int countInvoiceByUserIdAndMonth(String userId, List<Integer> status, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByUserIdAndMonth(userId, status, fromDate, toDate);
    }

    @Override
    public int countInvoiceByUserIdAndBankId(String userId, List<Integer> status, String bankId) {
        return repo.countInvoiceByUserIdAndBankId(userId, status, bankId);
    }

    @Override
    public int countInvoiceByUserIdAndBankIdAndMonth(String userId, List<Integer> status, String bankId, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByUserIdAndBankIdAndMonth(userId, status, bankId, fromDate, toDate);
    }

    @Override
    public IInvoiceDetailDTO getInvoiceDetailById(String invoiceId) {
        return repo.getInvoiceDetailById(invoiceId);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByMerchantId(String value, int offset, int size, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByMerchantId(value, offset, size, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceUnpaid(String value, int offset, int size, String month, String userId) {
        StartEndTimeDTO startDate = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startDate.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startDate.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;

        return repo.getInvoiceUnpaid(value, offset, size, fromDate, toDate, userId);
    }

    @Override
    public int countInvoiceUnpaid(String value, String month, String userId) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceUnpaid(value, fromDate, toDate, userId);
    }


    @Override
    public int countInvoiceByMerchantId(String value, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByMerchantId(value, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByInvoiceNumber(String value, int offset, int size, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByInvoiceNumber(value, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByInvoiceNumber(String value, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByInvoiceNumber(value, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByBankAccount(String value, int offset, int size, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByBankAccount(value, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByBankAccount(String value, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByBankAccount(value, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByPhoneNo(String value, int offset, int size, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByPhoneNo(value, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByPhoneNo(String value, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByPhoneNo(value, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByStatus(int status, int offset, int size, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByStatus(status, offset, size, fromDate, toDate);
    }

    @Override
    public String getDataJson(String userId) {
        return repo.getDataJson(userId);
    }

    @Override
    public int countInvoiceByStatus(int status, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByStatus(status, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoices(int offset, int size, String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoices(offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoice(String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoice(fromDate, toDate);
    }

    @Override
    public IAdminExtraInvoiceDTO getExtraInvoice(String month) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(month);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getExtraInvoice(fromDate, toDate);
    }

    @Override
    public IInvoiceQrDetailDTO getInvoiceQrById(String invoiceId) {
        return repo.getInvoiceQrById(invoiceId);
    }

    @Override
    public IInvoiceDTO getInvoiceByInvoiceDetail(String invoiceId) {
        return repo.getInvoiceByInvoiceDetail(invoiceId);
    }

    @Override
    public InvoiceUpdateItemDTO getInvoiceById(String invoiceId) {
        return repo.getInvoiceById(invoiceId);
    }

    public InvoiceEntity findInvoiceById(String invoiceId) {
        return repo.findById(invoiceId).orElse(null);
    }

    @Override
    public void updateInvoiceById(long vatAmount, long totalAmount,
                                  long totalAmountAfterVat, String invoiceId) {
        repo.updateInvoiceById(vatAmount, totalAmount, totalAmountAfterVat, invoiceId);
    }

    @Override
    public void removeByInvoiceId(String invoiceId) {
        repo.removeByInvoiceId(invoiceId);
    }

    @Override
    public InvoiceEntity getInvoiceEntityById(String invoiceId) {
        return repo.getInvoiceEntityById(invoiceId);
    }

    @Override
    public InvoiceEntity getInvoiceEntityByRefId(String id, long amount) {
        return repo.getInvoiceEntityByRefId(id, amount);
    }

    @Override
    public int updateStatusInvoice(String id, int status) {
        return repo.updateStatusInvoice(id, status);
    }

    @Override
    public int updateStatusInvoice(String id, int status, long timePaid) {
        return repo.updateStatusInvoice(id, status, timePaid);
    }

    @Override
    public String checkExistedInvoice(String invoiceId) {
        return repo.checkExistedInvoice(invoiceId);
    }

    @Override
    public IInvoiceDTO getInvoiceRequestPayment(String invoiceId) {
        return repo.getInvoiceRequestPayment(invoiceId);
    }

    @Override
    public InvoiceUnpaidStatisticDTO getTotalInvoiceUnpaidByUserId(String userId) {
        return repo.getTotalInvoiceUnpaidByUserId(userId);
    }

    @Override
    public String getBankIdRechargeDefault(String id) {
        return repo.getBankIdRechargeDefault(id);
    }

    @Override
    public List<UserScheduleInvoiceDTO> getUserScheduleInvoice() {
        return repo.getUserScheduleInvoice();
    }

    @Override
    public InvoiceEntity findInvoiceByInvoiceItemIds(String invoiceItemId) {
        return repo.findInvoiceByInvoiceItemId(invoiceItemId);
    }

    @Override
    public InvoiceUpdateVsoDTO getInvoicesByBankId(String bankId) {
        return repo.getInvoicesByBankId(bankId);
    }

    @Override
    public void updateDataInvoiceByBankId(String data, String bankId) {
        repo.updateDataInvoiceByBankId(data, bankId);
    }

    @Override
    public void updateFileInvoiceById(String id, String invoiceId) {
        repo.updateFileInvoiceById(id, invoiceId);
    }

    @Override
    public List<IInvoiceLatestDTO> getInvoiceLatestByUserId(String userId) {
        return repo.getInvoiceLatestByUserId(userId);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByMerchantName(String value, int offset, int size, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByMerchantName(value, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByMerchantName(String value, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByMerchantName(value, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByVsoCode(String value, int offset, int size, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByVsoCode(value, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByVsoCode(String value, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByVsoCode(value, fromDate, toDate);
    }

    @Override
    public IInvoicePaymentDTO getInvoicePaymentInfo(String invoiceId) {
        return repo.getInvoicePaymentInfo(invoiceId);
    }

    @Override
    public List<IAdminInvoiceDTO> getAllInvoicesByInvoiceNumber(String value, int offset, int size) {
        return repo.getAllInvoicesByInvoiceNumber(value, offset, size);
    }

    @Override
    public int countAllInvoicesByInvoiceNumber(String value) {
        return repo.countAllInvoicesByInvoiceNumber(value);
    }

    @Override
    public List<IAdminInvoiceDTO> getAllInvoicesByBankAccount(String value, int offset, int size) {
        return repo.getAllInvoicesByBankAccount(value, offset, size);
    }

    @Override
    public int countAllInvoicesByBankAccount(String value) {
        return repo.countAllInvoicesByBankAccount(value);
    }

    @Override
    public List<IAdminInvoiceDTO> getAllInvoicesByPhoneNo(String value, int offset, int size) {
        return repo.getAllInvoicesByPhoneNo(value, offset, size);
    }

    @Override
    public int countAllInvoicesByPhoneNo(String value) {
        return repo.countAllInvoicesByPhoneNo(value);
    }

    @Override
    public List<IAdminInvoiceDTO> getAllInvoicesByMerchantId(String value, int offset, int size) {
        return repo.getAllInvoicesByMerchantId(value, offset, size);
    }


    @Override
    public int countAllInvoicesByMerchantId(String value) {
        return repo.countAllInvoicesByMerchantId(value);
    }

    @Override
    public List<IAdminInvoiceDTO> getAllInvoicesByMerchantName(String value, int offset, int size) {
        return  repo.getAllInvoicesByMerchantName(value, offset, size);

    }

    @Override
    public int countAllInvoicesByMerchantName(String value) {
        return repo.countAllInvoicesByMerchantName(value);
    }

    @Override
    public List<IAdminInvoiceDTO> getAllInvoicesByVsoCode(String value, int offset, int size) {
        return repo.getAllInvoicesByVsoCode(value, offset, size);
    }

    @Override
    public int countAllInvoicesByVsoCode(String value) {
        return repo.countAllInvoicesByVsoCode(value);
    }

    @Override
    public List<IAdminInvoiceDTO> getAllInvoices(int offset, int size) {
        return repo.getAllInvoices(offset, size);
    }

    @Override
    public int countAllInvoices() {
        return repo.countAllInvoices();
    }

    @Override
    public List<IAdminInvoiceDTO> getAllInvoicesByInvoiceNumber(int dataType, String value, int offset, int size) {
        return repo.getAllInvoicesByInvoiceNumber(dataType, value, offset, size);
    }

    @Override
    public int countAllInvoicesByInvoiceNumber(int dataType, String value) {
        return repo.countAllInvoicesByInvoiceNumber(dataType, value);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByInvoiceNumber(int dataType, String value, int offset, int size, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByInvoiceNumber(dataType, value, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByInvoiceNumber(int dataType, String value, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByInvoiceNumber(dataType, value, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getAllInvoicesByBankAccount(int dataType, String value, int offset, int size) {
        return repo.getAllInvoicesByBankAccount(dataType, value, offset, size);
    }

    @Override
    public int countAllInvoicesByBankAccount(int dataType, String value) {
        return repo.countAllInvoicesByBankAccount(dataType, value);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByBankAccount(int dataType, String value, int offset, int size, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByBankAccount(dataType, value, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByBankAccount(int dataType, String value, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByBankAccount(dataType, value, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getAllInvoicesByPhoneNo(int dataType, String value, int offset, int size) {
        return repo.getAllInvoicesByPhoneNo(dataType, value, offset, size);
    }

    @Override
    public int countAllInvoicesByPhoneNo(int dataType, String value) {
        return repo.countAllInvoicesByPhoneNo(dataType, value);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByPhoneNo(int dataType, String value, int offset, int size, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByPhoneNo(dataType, value, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByPhoneNo(int dataType, String value, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByPhoneNo(dataType, value, fromDate, toDate);
    }

    @Override
    public List<IAdminInvoiceDTO> getAllInvoicesByMerchantId(int dataType, String value, int offset, int size) {
        return repo.getAllInvoicesByMerchantId(dataType, value, offset, size);
    }

    @Override
    public int countAllInvoicesByMerchantId(int dataType, String value) {
        return repo.countAllInvoicesByMerchantId(dataType, value);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoiceByMerchantId(int dataType, String value, int offset, int size, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoiceByMerchantId(dataType, value, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoiceByMerchantId(int dataType, String value, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoiceByMerchantId(dataType, value, fromDate, toDate);
    }

    @Override
    public List<AdminMerchantDTO> getUnpaidInvoicesByMerchantId(String merchantId, int offset, int size) {
        List<IAdminInvoiceDTO> unpaidInvoices = repo.getUnpaidInvoicesByMerchantId(merchantId, offset, size);

        // Chuyển đổi từ IAdminInvoiceDTO sang AdminMerchantDTO và lọc hóa đơn đã thanh toán đầy đủ
        return unpaidInvoices.stream().map(item -> {
                    AdminMerchantDTO dto = new AdminMerchantDTO();
                    dto.setInvoiceId(Optional.ofNullable(item.getInvoiceId()).orElse(""));
                    dto.setVso(Optional.ofNullable(item.getVso()).orElse(""));
                    dto.setMerchantName(Optional.ofNullable(item.getMidName()).orElse(""));
                    dto.setMerchantId(Optional.ofNullable(item.getMerchantId()).orElse(""));
                    dto.setBillNumber(Optional.ofNullable(item.getBillNumber()).orElse(""));
                    dto.setInvoiceName(Optional.ofNullable(item.getInvoiceName()).orElse(""));

                    // Lấy thông tin thanh toán
                    IInvoicePaymentDTO paymentInfo = getInvoicePaymentInfo(item.getInvoiceId());
                    long pendingAmount = Optional.ofNullable(paymentInfo.getPendingFee()).orElse(0L);
                    long completeAmount = Optional.ofNullable(paymentInfo.getCompleteFee()).orElse(0L);

                    dto.setCompleteAmount(completeAmount);
                    dto.setPendingAmount(pendingAmount);

                    dto.setVietQrAccount(Optional.ofNullable(item.getPhoneNo()).orElse(""));
                    dto.setEmail(Optional.ofNullable(item.getEmail()).orElse(""));

                    // Chỉ trả về những hóa đơn chưa thanh toán hoặc thanh toán một phần
                    if (pendingAmount > 0) {
                        return dto;
                    } else {
                        return null; // Bỏ qua các hóa đơn đã thanh toán đầy đủ
                    }
                }).filter(Objects::nonNull) // Loại bỏ các giá trị null
                .collect(Collectors.toList());
    }


    @Override
    public int countUnpaidInvoicesByMerchantId(String merchantId) {
        List<IAdminInvoiceDTO> unpaidInvoices = repo.getUnpaidInvoicesByMerchantId(merchantId, 0, Integer.MAX_VALUE);

        // Chỉ đếm những hóa đơn chưa thanh toán hoặc thanh toán một phần
        long count = unpaidInvoices.stream().filter(item -> {
            IInvoicePaymentDTO paymentInfo = getInvoicePaymentInfo(item.getInvoiceId());
            long pendingAmount = Optional.ofNullable(paymentInfo.getPendingFee()).orElse(0L);
            return pendingAmount > 0;
        }).count();

        return (int) count;
    }

    @Override
    public List<IAdminInvoiceDTO> getAllInvoicesByStatus(String status, int offset, int size) {
        return repo.getAllInvoicesByStatus(status, offset, size);
    }

    @Override
    public int countAllInvoicesByStatus(String status) {
        return repo.countAllInvoicesByStatus(status);
    }

    @Override
    public List<IAdminInvoiceDTO> getInvoicesByStatus(String status, int offset, int size, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.getInvoicesByStatus(status, offset, size, fromDate, toDate);
    }

    @Override
    public int countInvoicesByStatus(String status, String time) {
        StartEndTimeDTO startEndTimeDTO = DateTimeUtil.getStartEndMonth(time);
        long fromDate = startEndTimeDTO.getStartTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        long toDate = startEndTimeDTO.getEndTime() - DateTimeUtil.GMT_PLUS_7_OFFSET;
        return repo.countInvoicesByStatus(status, fromDate, toDate);
    }

    private AccountBankInfoDTO getBankAccountInfoByData(String data) {
        AccountBankInfoDTO dto = new AccountBankInfoDTO();
        ObjectMapper mapper = new ObjectMapper();
        try {
            dto = mapper.readValue(data, AccountBankInfoDTO.class);
        } catch (Exception e) {
            dto = new AccountBankInfoDTO();
        }
        return dto;
    }

}
