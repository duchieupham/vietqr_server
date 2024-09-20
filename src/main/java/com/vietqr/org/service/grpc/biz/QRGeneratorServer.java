//package com.vietqr.org.service.grpc.biz;
//
//import com.example.grpc.qrgenerator.QRGeneratorServiceGrpc;
//import com.example.grpc.qrgenerator.RequestStaticQR;
//import com.example.grpc.qrgenerator.RequestDynamicQR;
//import com.example.grpc.qrgenerator.RequestSemiDynamicQR;
//import com.example.grpc.qrgenerator.VietQR;
//import com.vietqr.org.controller.VietQRController;
//import com.vietqr.org.dto.*;
//import com.vietqr.org.dto.mb.VietQRStaticMMSRequestDTO;
//import com.vietqr.org.entity.AccountBankReceiveEntity;
//import com.vietqr.org.entity.BankTypeEntity;
//import com.vietqr.org.entity.TerminalBankEntity;
//import com.vietqr.org.entity.TerminalBankReceiveEntity;
//import com.vietqr.org.service.AccountBankReceiveService;
//import com.vietqr.org.service.BankTypeService;
//import com.vietqr.org.util.StringUtil;
//import com.vietqr.org.util.VietQRUtil;
//import com.vietqr.org.util.bank.mb.MBTokenUtil;
//import com.vietqr.org.util.bank.mb.MBVietQRUtil;
//import io.grpc.stub.StreamObserver;
//import net.devh.boot.grpc.server.service.GrpcService;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//
//import java.util.Objects;
//import java.util.UUID;
//
//@GrpcService
//public class QRGeneratorServer extends QRGeneratorServiceGrpc.QRGeneratorServiceImplBase {
//    private static final Logger logger = Logger.getLogger(QRGeneratorServer.class);
//    private final String LOG_ERROR = "Failed at QRGeneratorServer: ";
//    private final VietQRController vietQRController;
//    @Autowired
//    BankTypeService bankTypeService;
//
//    @Autowired
//    AccountBankReceiveService accountBankReceiveService;
//
//    VietQR empty = VietQR
//            .newBuilder()
//            .setBankCode("")
//            .setBankName("")
//            .setBankAccount("")
//            .setUserBankName("")
//            .setAmount(0L)
//            .setContent("")
//            .setQrCode("")
//            .setImgId("")
//            .setExisting(1)
//            .setTransactionId("")
//            .setTransactionRefId("")
//            .setQrLink("")
//            .setTerminalCode("")
//            .setSubTerminalCode("")
//            .setServiceCode("")
//            .setOrderId("")
//            .build();
//
//    public QRGeneratorServer(VietQRController vietQRController) {
//        this.vietQRController = vietQRController;
//    }
//
//    private VietQR clone(VietQRDTO dto) {
//        return VietQR
//                .newBuilder()
//                .setBankCode(dto.getBankCode())
//                .setBankName(dto.getBankName())
//                .setBankAccount(dto.getBankAccount())
//                .setUserBankName(dto.getUserBankName())
//                .setAmount(Long.parseLong(dto.getAmount()))
//                .setContent(dto.getContent())
//                .setQrCode(dto.getQrCode())
//                .setImgId(dto.getImgId())
//                .setExisting(dto.getExisting())
//                .setTransactionId(dto.getTransactionId())
//                .setTransactionRefId(dto.getTransactionRefId())
//                .setQrLink(dto.getQrLink())
//                .setTerminalCode(dto.getTerminalCode())
//                .setSubTerminalCode(dto.getSubTerminalCode())
//                .setServiceCode(dto.getServiceCode())
//                .setOrderId(dto.getOrderId())
//                .build();
//    }
//
//    @Override
//    public void generateStaticQR(RequestStaticQR request, StreamObserver<VietQR> responseObserver) {
//        try {
//            VietQRCreateCustomerDTO dto = new VietQRCreateCustomerDTO();
//            dto.setAmount(request.getAmount());
//            dto.setContent(request.getContent());
//            dto.setBankCode(request.getBankCode());
//            dto.setBankAccount(request.getBankAccount());
//            dto.setTransType(request.getTransType());
//            dto.setCustomerBankCode(request.getCustomerBankCode());
//            dto.setTerminalCode(request.getTerminalCode());
//
//            Object result = vietQRController.generateStaticQrCustomer(dto, request.getToken().trim()).getBody();
//            if (result instanceof VietQRDTO) {
//                VietQR response = clone((VietQRDTO) result);
//                responseObserver.onNext(response);
//                responseObserver.onCompleted();
//            } else {
//                responseObserver.onNext(empty);
//                responseObserver.onCompleted();
//            }
//        } catch (Exception e) {
//            logger.error(LOG_ERROR + "generateStaticQR: " + e.getMessage() + " at: " + System.currentTimeMillis());
//            responseObserver.onNext(empty);
//            responseObserver.onCompleted();
//        }
//    }
//
//    @Override
//    public void generateDynamicQR(RequestDynamicQR request, StreamObserver<VietQR> responseObserver) {
//        try {
//            VietQRCreateCustomerDTO dto = new VietQRCreateCustomerDTO();
//            dto.setAmount(request.getAmount());
//            dto.setContent(request.getContent());
//            dto.setBankCode(request.getBankCode());
//            dto.setBankAccount(request.getBankAccount());
//            dto.setUserBankName(request.getUserBankName());
//            dto.setTransType(request.getTransType());
//            dto.setCustomerBankAccount(request.getCustomerBankAccount());
//            dto.setCustomerBankCode(request.getCustomerBankCode());
//            dto.setCustomerName(request.getCustomerName());
//            dto.setOrderId(request.getOrderId());
//            dto.setSign(request.getSign());
//            dto.setSubTerminalCode(request.getSubTerminalCode());
//            dto.setTerminalCode(request.getTerminalCode());
//            dto.setServiceCode(request.getServiceCode());
//            dto.setUrlLink(request.getUrlLink());
//            dto.setNote(request.getNote());
//            dto.setReconciliation(request.getReconciliation());
//
//            Object result = vietQRController.generateDynamicQrCustomer(dto, request.getToken().trim()).getBody();
//            if (result instanceof VietQRDTO) {
//                VietQR response = clone((VietQRDTO) result);
//                responseObserver.onNext(response);
//                responseObserver.onCompleted();
//            } else {
//                responseObserver.onNext(empty);
//                responseObserver.onCompleted();
//            }
//        } catch (Exception e) {
//            logger.error(LOG_ERROR + "generateDynamicQR: " + e.getMessage() + " at: " + System.currentTimeMillis());
//            responseObserver.onNext(empty);
//            responseObserver.onCompleted();
//        }
//    }
//
//    @Override
//    public void generateSemiDynamicQR(RequestSemiDynamicQR request, StreamObserver<VietQR> responseObserver) {
//        try {
//            VietQRCreateCustomerDTO dto = new VietQRCreateCustomerDTO();
//            dto.setAmount(request.getAmount());
//            dto.setContent(request.getContent());
//            dto.setBankCode(request.getBankCode());
//            dto.setBankAccount(request.getBankAccount());
//            dto.setTransType(request.getTransType());
//            dto.setCustomerBankCode(request.getCustomerBankCode());
//            dto.setTerminalCode(request.getTerminalCode());
//            dto.setServiceCode(request.getServiceCode());
//
//            Object result = vietQRController.generateSemiDynamicQrCustomer(dto, request.getToken().trim()).getBody();
//            if (result instanceof VietQRDTO) {
//                VietQR response = clone((VietQRDTO) result);
//                responseObserver.onNext(response);
//                responseObserver.onCompleted();
//            } else {
//                responseObserver.onNext(empty);
//                responseObserver.onCompleted();
//            }
//        } catch (Exception e) {
//            logger.error(LOG_ERROR + "generateSemiDynamicQR: " + e.getMessage() + " at: " + System.currentTimeMillis());
//            responseObserver.onNext(empty);
//            responseObserver.onCompleted();
//        }
//    }
//
//    private VietQRDTO generateStaticQR(VietQRCreateCustomerDTO dto, String token) {
//        VietQRDTO result = new VietQRDTO();
//        BankTypeEntity bankTypeEntity = null;
//        AccountBankReceiveEntity accountBankEntity = null;
//        String content = "";
//        String qr = "";
//        try {
//            if (dto.getContent() == null) {
//                dto.setContent("");
//            }
//            if (dto.getContent().length() <= 20) {
//                bankTypeEntity = bankTypeService.getBankTypeByBankCode(dto.getBankCode());
//                AccountBankReceiveEntity accountBankReceiveEntity = accountBankReceiveService.getAccountBankById();
//                if (dto.getBankCode().equals("MB")) {
//                    AccountBankReceiveEntity accountBankReceiveEntity =
//                            accountBankReceiveService
//                                    .getAccountBankReceiveByBankAccountAndBankCode(dto.getBankAccount(),
//                                            dto.getBankCode());
//
//                    if (Objects.nonNull(accountBankReceiveEntity)) {
//                        TerminalBankSyncDTO terminalBankSyncDTO = terminalBankReceiveService
//                                .getTerminalBankReceive(dto.getTerminalCode(), dto.getBankAccount(),
//                                        dto.getBankCode());
//                        if (Objects.nonNull(terminalBankSyncDTO)) {
//                            if (StringUtil.isNullOrEmpty(terminalBankSyncDTO.getData1())
//                                    && StringUtil.isNullOrEmpty(terminalBankSyncDTO.getData2())) {
//                                if (accountBankReceiveEntity.isMmsActive()) {
//                                    TerminalBankEntity terminalBankEntity =
//                                            terminalBankService
//                                                    .getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
//                                    if (terminalBankEntity != null) {
//                                        // luồng uu tien
//                                        if (StringUtil.isNullOrEmpty(dto.getContent())) {
//                                            content = terminalBankSyncDTO.getRawTerminalCode();
//                                        } else {
//                                            content = dto.getContent();
//                                        }
//                                        qr = MBVietQRUtil.generateStaticVietQRMMS(
//                                                new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
//                                                        terminalBankEntity.getTerminalId(), content));
//                                        String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
//                                        terminalBankReceiveService.updateQrCodeTerminalSync("", qr, traceTransfer,
//                                                terminalBankSyncDTO.getTerminalBankReceiveId());
//                                    } else {
//                                        System.out.println("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
//                                    }
//                                } else {
//                                    // luồng thuong
//                                    if (StringUtil.isNullOrEmpty(dto.getContent())) {
//                                        content = "SQR" + terminalBankSyncDTO.getTerminalCode();
//                                    } else {
//                                        content = "SQR" + terminalBankSyncDTO.getTerminalCode() + " " + dto.getContent();
//                                    }
//                                    String bankAccount = accountBankReceiveEntity.getBankAccount();
//                                    String caiValue = accountBankReceiveService.getCaiValueByBankId(accountBankReceiveEntity.getId());
//                                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", content, bankAccount);
//                                    qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
//                                    terminalBankReceiveService.updateQrCodeTerminalSync(qr, "", "",
//                                            terminalBankSyncDTO.getTerminalBankReceiveId());
//                                }
//                                VietQRDTO vietQRDTO = new VietQRDTO();
//                                vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
//                                vietQRDTO.setBankName(bankTypeEntity.getBankName());
//                                vietQRDTO.setBankAccount(accountBankReceiveEntity.getBankAccount());
//                                vietQRDTO.setUserBankName(accountBankReceiveEntity.getBankAccountName());
//                                vietQRDTO.setAmount(StringUtil.getValueNullChecker(dto.getAmount()) + "");
//                                vietQRDTO.setContent(content);
//                                vietQRDTO.setQrCode(qr);
//                                vietQRDTO.setImgId(bankTypeEntity.getImgId());
//                                vietQRDTO.setExisting(0);
//                                vietQRDTO.setTransactionId("");
//                                vietQRDTO.setTerminalCode(dto.getTerminalCode());
//                                String qrLink = "";
//                                vietQRDTO.setTransactionRefId("");
//                                vietQRDTO.setQrLink(qrLink);
//                                result = vietQRDTO;
//                                httpStatus = HttpStatus.OK;
//                            } else {
//                                String terminalCode = getRandomUniqueCodeInTerminalCode();
//                                TerminalBankReceiveEntity terminalBankReceiveEntity =
//                                        new TerminalBankReceiveEntity();
//                                terminalBankReceiveEntity.setId(UUID.randomUUID().toString());
//                                terminalBankReceiveEntity.setRawTerminalCode(terminalBankSyncDTO.getRawTerminalCode());
//                                terminalBankReceiveEntity.setTerminalCode(terminalCode);
//                                terminalBankReceiveEntity.setSubTerminalAddress("");
//                                terminalBankReceiveEntity.setBankId(terminalBankSyncDTO.getBankId());
//                                terminalBankReceiveEntity.setTerminalId(terminalBankSyncDTO.getTerminalId());
//                                terminalBankReceiveEntity.setTypeOfQR(1);
//                                if (accountBankReceiveEntity.isMmsActive()) {
//                                    TerminalBankEntity terminalBankEntity =
//                                            terminalBankService.getTerminalBankByBankAccount(accountBankReceiveEntity.getBankAccount());
//                                    if (terminalBankEntity != null) {
//                                        // luồng uu tien
//                                        if (StringUtil.isNullOrEmpty(dto.getContent())) {
//                                            content = terminalBankSyncDTO.getRawTerminalCode();
//                                        } else {
//                                            content = dto.getContent();
//                                        }
//                                        qr = MBVietQRUtil.generateStaticVietQRMMS(
//                                                new VietQRStaticMMSRequestDTO(MBTokenUtil.getMBBankToken().getAccess_token(),
//                                                        terminalBankEntity.getTerminalId(), content));
//                                        String traceTransfer = MBVietQRUtil.getTraceTransfer(qr);
//
//                                        terminalBankReceiveEntity.setData2(qr);
//                                        terminalBankReceiveEntity.setData1("");
//                                        terminalBankReceiveEntity.setTraceTransfer(traceTransfer);
//                                    } else {
//                                        System.out.println("TerminalController: insertTerminal: terminalBankEntity is null or bankCode is not MB");
//                                    }
//                                } else {
//                                    // luồng thuong
//                                    if (StringUtil.isNullOrEmpty(dto.getContent())) {
//                                        content = "SQR" + terminalCode;
//                                    } else {
//                                        content = "SQR" + terminalCode + " " + dto.getContent();
//                                    }
//                                    String bankAccount = accountBankReceiveEntity.getBankAccount();
//                                    String caiValue = accountBankReceiveService.getCaiValueByBankId(accountBankReceiveEntity.getId());
//                                    VietQRGenerateDTO vietQRGenerateDTO = new VietQRGenerateDTO(caiValue, "", content, bankAccount);
//                                    qr = VietQRUtil.generateTransactionQR(vietQRGenerateDTO);
//
//                                    terminalBankReceiveEntity.setData2("");
//                                    terminalBankReceiveEntity.setData1(qr);
//                                    terminalBankReceiveEntity.setTraceTransfer("");
//                                }
//
//                                terminalBankReceiveService.insert(terminalBankReceiveEntity);
//                                VietQRDTO vietQRDTO = new VietQRDTO();
//                                vietQRDTO.setBankCode(bankTypeEntity.getBankCode());
//                                vietQRDTO.setBankName(bankTypeEntity.getBankName());
//                                vietQRDTO.setBankAccount(accountBankReceiveEntity.getBankAccount());
//                                vietQRDTO.setUserBankName(accountBankReceiveEntity.getBankAccountName());
//                                vietQRDTO.setAmount(StringUtil.getValueNullChecker(dto.getAmount()) + "");
//                                vietQRDTO.setContent(content);
//                                vietQRDTO.setQrCode(qr);
//                                vietQRDTO.setImgId(bankTypeEntity.getImgId());
//                                vietQRDTO.setExisting(0);
//                                vietQRDTO.setTransactionId("");
//                                vietQRDTO.setTerminalCode(dto.getTerminalCode());
//                                String qrLink = "";
//                                vietQRDTO.setTransactionRefId("");
//                                vietQRDTO.setQrLink(qrLink);
//                                result = vietQRDTO;
//                                httpStatus = HttpStatus.OK;
//                            }
//                        } else {
//                            result = new ResponseMessageDTO("FAILED", "E152");
//                            httpStatus = HttpStatus.BAD_REQUEST;
//                        }
//                    } else {
//                        result = new ResponseMessageDTO("FAILED", "E25");
//                        httpStatus = HttpStatus.BAD_REQUEST;
//                    }
//                } else {
//                    // Ngan hang khong phai MB Bank
//                    result = new ResponseMessageDTO("FAILED", "E151");
//                    httpStatus = HttpStatus.BAD_REQUEST;
//                }
//            } else {
//                result = new ResponseMessageDTO("FAILED", "E26");
//                httpStatus = HttpStatus.BAD_REQUEST;
//            }
//        } catch (Exception e) {
//            httpStatus = HttpStatus.BAD_REQUEST;
//            logger.error("VietQRController: ERROR: generateQRCustomer: " + e.getMessage() + " at: " + System.currentTimeMillis());
//        }
//        return result;
//    }
//}