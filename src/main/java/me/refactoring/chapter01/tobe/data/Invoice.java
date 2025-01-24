package me.refactoring.chapter01.tobe.data;

import java.util.List;

public record Invoice(String customer, List<Performance> performances) {

}
