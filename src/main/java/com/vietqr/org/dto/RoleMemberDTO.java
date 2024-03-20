package com.vietqr.org.dto;

public interface RoleMemberDTO {
    String getId();
    String getName();
    String getDescription();
    int getRole();
    // 0: blue
    // 1: green
    // 2: red
    int getColor();
}
