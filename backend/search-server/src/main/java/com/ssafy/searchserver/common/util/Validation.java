package com.ssafy.searchserver.common.util;

import com.ssafy.searchserver.common.exeception.CustomException;
import com.ssafy.searchserver.common.exeception.ErrorCode;

import java.util.List;

public class Validation {

    private static final List<String> allowedCategories = List.of("all", "general","politics", "business", "tech", "science", "sports", "health", "entertainment", "food", "travel");
    private static final List<Integer> allowedPeriods = List.of(1, 7, 30);
    private static final List<String> G20Nations = List.of("AR", "AU", "BR", "CA", "CN", "FR", "DE", "IN", "ID", "IT", "JP", "MX", "RU", "SA", "ZA", "KR", "TR", "GB", "US", "EU");

    public static void validatePeriod(int period) {
        if (!allowedPeriods.contains(period)) {
            throw new CustomException(ErrorCode.INVALID_PERIOD);
        }
    }

    public static void validateCategory(String category) {
        if (!allowedCategories.contains(category.toLowerCase())) {
            throw new CustomException(ErrorCode.INVALID_CATEGORY);
        }
    }

    public static void validateCountry(String country) {
        if (!G20Nations.contains(country.toUpperCase())) {
            throw new CustomException(ErrorCode.INVALID_COUNTRY);
        }
    }

    public static void validateCategoryAndPeriod(String category, int period) {
        validateCategory(category);
        validatePeriod(period);
    }

    public static void validateCountryPeriodCategory(String country, int period, String category) {
        System.out.println(category+""+period+""+country);
        validateCountry(country);
        validatePeriod(period);
        validateCategory(category);
    }



}
