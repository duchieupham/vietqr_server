package com.vietqr.org.dto;

public interface IRoleMemberDTO {
    String getId();
    String getName();
    String getDescription();
    int getRole();
    // 0: blue
    // 1: green
    // 2: red
    int getColor();
}
