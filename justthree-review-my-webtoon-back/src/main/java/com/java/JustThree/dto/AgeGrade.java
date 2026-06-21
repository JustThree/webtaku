package com.java.JustThree.dto;

public final class AgeGrade {
    public static final String ADULT_CODE = "4";
    public static final String ADULT_NAME = "19세 이상";

    private AgeGrade() {}

    public static boolean isAdult(String ageGradCd) {
        return ADULT_CODE.equals(ageGradCd);
    }
}
