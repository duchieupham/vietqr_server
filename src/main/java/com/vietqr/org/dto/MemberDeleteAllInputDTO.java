package com.vietqr.org.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

public class MemberDeleteAllInputDTO implements Serializable {

        /**
        *
        */
        private static final long serialVersionUID = 1L;
        @NotBlank
        private String bankId;

        public MemberDeleteAllInputDTO() {
            super();
        }

        public MemberDeleteAllInputDTO(String bankId) {
            this.bankId = bankId;
        }

        public String getBankId() {
            return bankId;
        }

        public void setBankId(String bankId) {
            this.bankId = bankId;
        }
}
