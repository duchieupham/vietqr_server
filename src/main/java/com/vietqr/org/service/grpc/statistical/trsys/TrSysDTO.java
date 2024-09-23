package com.vietqr.org.service.grpc.statistical.trsys;

public class TrSysDTO {
     private int totalNumberCredits;
     private Long totalAmountCredits;
     private int totalNumberRecon;
     private Long totalAmountRecon;
     private int totalNumberWithoutRecon;
     private Long totalAmountWithoutRecon;
     private int totalNumberPushError;
     private Long totalAmountPushErrorSum;

     public TrSysDTO() {}

     public TrSysDTO(ITrSysDTO dto) {
          this.totalNumberCredits = dto.getTotalNumberCredits();
          this.totalAmountCredits = dto.getTotalAmountCredits();
          this.totalNumberRecon = dto.getTotalNumberRecon();
          this.totalAmountRecon = dto.getTotalAmountRecon();
          this.totalNumberWithoutRecon = dto.getTotalNumberWithoutRecon();
          this.totalAmountWithoutRecon = dto.getTotalAmountWithoutRecon();
          this.totalNumberPushError = dto.getTotalNumberPushError();
          this.totalAmountPushErrorSum = dto.getTotalAmountPushErrorSum();
     }

     public int getTotalNumberCredits() {
          return totalNumberCredits;
     }

     public void setTotalNumberCredits(int totalNumberCredits) {
          this.totalNumberCredits = totalNumberCredits;
     }

     public Long getTotalAmountCredits() {
          return totalAmountCredits;
     }

     public void setTotalAmountCredits(Long totalAmountCredits) {
          this.totalAmountCredits = totalAmountCredits;
     }

     public int getTotalNumberRecon() {
          return totalNumberRecon;
     }

     public void setTotalNumberRecon(int totalNumberRecon) {
          this.totalNumberRecon = totalNumberRecon;
     }

     public Long getTotalAmountRecon() {
          return totalAmountRecon;
     }

     public void setTotalAmountRecon(Long totalAmountRecon) {
          this.totalAmountRecon = totalAmountRecon;
     }

     public int getTotalNumberWithoutRecon() {
          return totalNumberWithoutRecon;
     }

     public void setTotalNumberWithoutRecon(int totalNumberWithoutRecon) {
          this.totalNumberWithoutRecon = totalNumberWithoutRecon;
     }

     public Long getTotalAmountWithoutRecon() {
          return totalAmountWithoutRecon;
     }

     public void setTotalAmountWithoutRecon(Long totalAmountWithoutRecon) {
          this.totalAmountWithoutRecon = totalAmountWithoutRecon;
     }

     public int getTotalNumberPushError() {
          return totalNumberPushError;
     }

     public void setTotalNumberPushError(int totalNumberPushError) {
          this.totalNumberPushError = totalNumberPushError;
     }

     public Long getTotalAmountPushErrorSum() {
          return totalAmountPushErrorSum;
     }

     public void setTotalAmountPushErrorSum(Long totalAmountPushErrorSum) {
          this.totalAmountPushErrorSum = totalAmountPushErrorSum;
     }
}

