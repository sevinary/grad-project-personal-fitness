package com.fitness.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Enums {
    public enum Gender { MALE,FEMALE}
    public enum Goal { WEIGHT_LOSS, MUSCLE_GAIN, MAINTENANCE }
    public enum MuscleGroup { NONE, ARMS, LEGS, BACK, CHEST, SHOULDERS }
    public enum DayOfWeek { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }
    public enum ExerciseType { AEROBIC, ANAEROBIC, STRETCHING }

    @Converter
    public static class DayOfWeekArrayConverter implements AttributeConverter<DayOfWeek[], String>  {
        @Override
        public String convertToDatabaseColumn(DayOfWeek[] attribute){
            if(attribute == null)return null;
            return Arrays.stream(attribute).map(Enum::name).collect(Collectors.joining(","));
    }
        @Override
        public DayOfWeek[] convertToEntityAttribute(String dbData) {
            if(dbData == null || dbData.isEmpty())return new DayOfWeek[0];
            return Arrays.stream(dbData.split(","))
                    .map(DayOfWeek::valueOf)
                    .toArray(DayOfWeek[]::new);
        }
    }
}